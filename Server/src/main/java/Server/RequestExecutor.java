package Server;

import Commands.*;
import Instruments.ClientRequest;
import Instruments.ICollectionManager;
import Instruments.ServerResponseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

public class RequestExecutor implements Runnable {
    private final SocketChannel client;
    private final ClientRequest clientRequest;
    private final ThreadPoolExecutor responsePool;
    private final ICollectionManager<Route> manager;

    public RequestExecutor(SocketChannel client, ClientRequest clientRequest, ThreadPoolExecutor responsePool, ICollectionManager<Route> manager) {
        this.client = client;
        this.clientRequest = clientRequest;
        this.responsePool = responsePool;
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            ICommand command = clientRequest.getCommand();
            Statement statement = Main.getDBconnection().createStatement();
            ServerResponse resp;
            if (command.getClass() == LogIn.class) {
                ResultSet resultSet = statement.executeQuery("select * from users where username='" + clientRequest.getLogin() + "'");
                if (resultSet.next()) {
                    if (clientRequest.getPassword().equals("%")) {
                        String salt = resultSet.getString(3);
                        resp = new ServerResponse(ServerResponseCodes.TEXT_ONLY, salt);
                        resp.setAccess(true);
                        responsePool.execute(new ResponseSender(client, resp));
                    } else {
                        String passwordHash = resultSet.getString(2);
                        if (passwordHash.equals(clientRequest.getPassword())) {
                            resp = new ServerResponse(ServerResponseCodes.AUTHORISED);
                            resp.setAccess(true);
                            responsePool.execute(new ResponseSender(client, resp));

                            ServerSocketHandler.addClient(clientRequest.getLogin());
                            Main.log.info("Client with login " + clientRequest.getLogin() + " authorised successfully");

                        } else {
                            resp = new ServerResponse(ServerResponseCodes.INCORRECT_LOG_IN);
                            resp.setAccess(false);
                            responsePool.execute(new ResponseSender(client, resp));
                        }
                    }
                } else {
                    resp = new ServerResponse(ServerResponseCodes.INCORRECT_LOG_IN);
                    resp.setAccess(false);
                    responsePool.execute(new ResponseSender(client, resp));
                }
            } else if (command.getClass() == SignUp.class) {
                ResultSet resultSet = statement.executeQuery("select * from users where username='" + clientRequest.getLogin() + "'");
                if (!resultSet.next()) {
                    if (clientRequest.getPassword().equals("%")) {
                        byte[] array = new byte[9];
                        new Random().nextBytes(array);
                        String salt = new String(array, StandardCharsets.UTF_8);

                        resp = new ServerResponse(ServerResponseCodes.TEXT_ONLY, salt);
                        resp.setAccess(true);
                        responsePool.execute(new ResponseSender(client, resp));

                    } else {
                        try {
                            sendMail(((SignUp) command).getEmail(), clientRequest.getLogin(), ((SignUp) command).getPassword());
                            String sql = "insert into users values ('" + clientRequest.getLogin() + "' , '" + clientRequest.getPassword() + "' , '" + ((SignUp) command).getSalt() + "' )";
                            statement.execute(sql);

                            resp = new ServerResponse(ServerResponseCodes.AUTHORISED);
                            resp.setAccess(true);
                            responsePool.execute(new ResponseSender(client, resp));

                            ServerSocketHandler.addClient(clientRequest.getLogin());
                            Main.log.info("Client with login " + clientRequest.getLogin() + " registered and authorised successfully");
                        } catch (MessagingException e) {
                            resp = new ServerResponse(ServerResponseCodes.ERROR);
                            resp.setAccess(false);
                            responsePool.execute(new ResponseSender(client, resp));
                        }
                    }
                } else {
                    resp = new ServerResponse(ServerResponseCodes.INCORRECT_LOG_IN);
                    resp.setAccess(false);
                    responsePool.execute(new ResponseSender(client, resp));
                }
            } else {
                ResultSet resultSet = statement.executeQuery("select * from users where username='" + clientRequest.getLogin() + "'");
                if (resultSet.next()) {
                    if (resultSet.getString(1).equals(clientRequest.getLogin()) && resultSet.getString(2).equals(clientRequest.getPassword())) {
                        resp = command.execute(manager);
                        Main.log.fine("Command executed: " + command.getCommandEnum().toString() + " client " + clientRequest.getLogin());

                        if (command.getCommandEnum() == EAvailableCommands.History)
                            resp.setAdditionalInfo(ServerSocketHandler.getHistory(clientRequest.getLogin()));
                        if(!command.getCommandEnum().equals(EAvailableCommands.Not_A_Command)) ServerSocketHandler.addCommandToHistory(clientRequest.getLogin(), command.getCommandEnum().toString());
                        if (resp.getCode() == ServerResponseCodes.EXIT) {
                            ServerSocketHandler.deleteHistory(clientRequest.getLogin());
                            Main.log.info("Client with login " + clientRequest.getLogin() + " exits server. Socket for this client was closed and removed.");
                        }
                        responsePool.execute(new ResponseSender(client, resp));

                    } else {
                        responsePool.execute(new ResponseSender(client, new ServerResponse(ServerResponseCodes.SURPRISE_NOT_CORRECT_LOGIN_OR_PASSWORD)));
                        Main.log.severe("UNAUTHORISED CLINT " + clientRequest.getLogin());
                    }
                } else {
                    responsePool.execute(new ResponseSender(client, new ServerResponse(ServerResponseCodes.SURPRISE_NOT_CORRECT_LOGIN_OR_PASSWORD)));
                    Main.log.severe("UNAUTHORISED CLINT " + clientRequest.getLogin());
                }
            }
        } catch (SQLException e) {
            responsePool.execute(new ResponseSender(client, new ServerResponse(ServerResponseCodes.SQL_ERROR)));
            Main.log.severe("SQLException for client " + clientRequest.getLogin());
        }
    }

    public static void sendMail(String mail, String login, String password) throws MessagingException {
          if (!(mail.equals("no") || mail.equals(null) || mail.equals(""))) {

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("proglab7@gmail.com", "12BearFox01");
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("proglab7@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(mail));
            message.setSubject("Programming lab password and login");
            message.setText("Dear user,"
                    + "\n\n Here are your login details for client-server app:\n\npassword: " + password + "\nlogin: " + login + "\n\nAll the best,\n helios");

            Transport.send(message);
            Main.log.info("Email for user "+login+" was sent.");
        }
    }
}
