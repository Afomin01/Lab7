import Commands.*;
import Instruments.ClientRequest;
import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

public class RequestExecutor implements Runnable {
    private final SocketChannel client;
    private final ClientRequest clientRequest;
    private final ThreadPoolExecutor responsePool;
    private final ICollectionManager<Route> manager;

    public RequestExecutor(SocketChannel client, ClientRequest clientRequest, ThreadPoolExecutor responsePool,ICollectionManager<Route> manager) {
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
                    if(clientRequest.getPassword().equals("%")) {
                        String salt = resultSet.getString(3);
                        resp = new ServerResponse(ServerRespenseCodes.TEXT_ONLY, salt);
                        resp.setAccess(true);
                        responsePool.execute(new ResponseSender(client,resp));
                    }
                    else {
                        String passwordHash = resultSet.getString(2);
                        if (passwordHash.equals(clientRequest.getPassword())) {
                            resp = new ServerResponse(ServerRespenseCodes.AUTHORISED);
                            resp.setAccess(true);
                            responsePool.execute(new ResponseSender(client,resp));

                            ServerSocketHandler.addClient(clientRequest.getLogin());
                            Main.log.info("Client with login " + clientRequest.getLogin() + " authorised successfully");

                        } else {
                            resp = new ServerResponse(ServerRespenseCodes.INCORRECT_LOG_IN);
                            resp.setAccess(false);
                            responsePool.execute(new ResponseSender(client, resp));
                        }
                    }
                } else {
                    resp = new ServerResponse(ServerRespenseCodes.INCORRECT_LOG_IN);
                    resp.setAccess(false);
                    responsePool.execute(new ResponseSender(client,resp));
                }
            }
            else if (command.getClass() == SignUp.class) {
                ResultSet resultSet = statement.executeQuery("select * from users where username='" + clientRequest.getLogin() + "'");
                if (!resultSet.next()) {
                    if(clientRequest.getPassword().equals("%")) {
                        byte[] array = new byte[9];
                        new Random().nextBytes(array);
                        String salt = new String(array, StandardCharsets.UTF_8);

                        resp = new ServerResponse(ServerRespenseCodes.TEXT_ONLY, salt);
                        resp.setAccess(true);
                        responsePool.execute(new ResponseSender(client, resp));

                    }else {
                        String sql = "insert into users values ('" + clientRequest.getLogin() + "' , '" + clientRequest.getPassword() + "' , '" + ((SignUp) command).getSalt() + "' )";///TODO erhheth
                        statement.execute(sql);

                        resp = new ServerResponse(ServerRespenseCodes.AUTHORISED);
                        resp.setAccess(true);
                        responsePool.execute(new ResponseSender(client, resp));

                        ServerSocketHandler.addClient(clientRequest.getLogin());
                        Main.log.info("Client with login " + clientRequest.getLogin() + " registered and authorised successfully");
                    }
                } else {
                    resp = new ServerResponse(ServerRespenseCodes.INCORRECT_LOG_IN);
                    resp.setAccess(false);
                    responsePool.execute(new ResponseSender(client,resp));
                }
            }else {
                ResultSet resultSet = statement.executeQuery("select * from users where username='" + clientRequest.getLogin() + "'");
                if(resultSet.next()) {
                    if (resultSet.getString(1).equals(clientRequest.getLogin()) && resultSet.getString(2).equals(clientRequest.getPassword())) {
                        resp = command.execute(manager);
                        Main.log.fine("Command executed: " + command.getCommandEnum().toString() + " client " + clientRequest.getLogin());

                        if (command.getCommandEnum() == EAvailableCommands.History)
                            resp.setAdditionalInfo(ServerSocketHandler.getHistory(clientRequest.getLogin()));
                        ServerSocketHandler.addCommandToHistory(clientRequest.getLogin(), command.getCommandEnum().toString());
                        if (resp.getCode() == ServerRespenseCodes.EXIT)
                            ServerSocketHandler.deleteHistory(clientRequest.getLogin());
                        responsePool.execute(new ResponseSender(client, resp));

                    } else {
                        responsePool.execute(new ResponseSender(client, new ServerResponse(ServerRespenseCodes.SURPRISE_NOT_CORRECT_LOGIN_OR_PASSWORD)));
                        Main.log.severe("UNAUTHORISED CLINT " + clientRequest.getLogin());
                    }
                }else{
                    responsePool.execute(new ResponseSender(client, new ServerResponse(ServerRespenseCodes.SURPRISE_NOT_CORRECT_LOGIN_OR_PASSWORD)));
                    Main.log.severe("UNAUTHORISED CLINT " + clientRequest.getLogin());
                }
            }

        }catch (SQLException e){
            responsePool.execute(new ResponseSender(client, new ServerResponse(ServerRespenseCodes.SQL_ERROR)));
            Main.log.severe("SQLException for client " + clientRequest.getLogin());
        }
    }
}
