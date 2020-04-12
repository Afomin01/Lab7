import Commands.*;
import Instruments.ClientRequest;
import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Storable.Route;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Handler;

public class LegacySocketHandler implements Runnable {
    //private SSLSocket client;
    private SocketChannel client;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ICollectionManager<Route> manager;
    private ArrayList<String> history = new ArrayList<>();
    private String login="";
    private Statement statement;

    LegacySocketHandler(SocketChannel client, ICollectionManager<Route> manager) throws SocketHandlerException {
        this.client = client;
        this.manager = manager;
        try {
            statement=Main.getDBconnection().createStatement();
        } catch (SQLException e) {
            Main.log.severe("An error occurred while creating the output stream.");
            throw new SocketHandlerException();
        }
        try {
            outputStream = new ObjectOutputStream(client.socket().getOutputStream());
            outputStream.flush();
        } catch (IOException e) {
            Main.log.severe("An error occurred while creating the output stream.");
            throw new SocketHandlerException();
        }
        try {
            inputStream = new ObjectInputStream(client.socket().getInputStream());
            outputStream.writeObject(new ServerResponse("Соединение успешно установлено"));
        } catch (IOException e) {
            Main.log.severe("An error occurred while creating the input stream.");
            throw new SocketHandlerException();
        }
    }

    @Override
    public void run() {
        try {
            try {
                ServerResponse response;
                ClientRequest clientRequest;
                ICommand command;
                ServerResponse resp;
                while (true) {
                    clientRequest = (ClientRequest) inputStream.readObject();
                    command = clientRequest.getCommand();
                    if (command.getClass() == LogIn.class) {
                        ResultSet resultSet = statement.executeQuery("select * from users where username='"+clientRequest.getLogin()+"'");
                        if(resultSet.next()){
                            String passwordHash = resultSet.getString(2);
                            ResultSet saltSet = statement.executeQuery("select * from users where username='"+clientRequest.getLogin()+"'");
                            saltSet.next();
                            String salt= saltSet.getString(3);
                            resp = new ServerResponse(salt);
                            outputStream.writeObject(resp);
                            clientRequest = (ClientRequest) inputStream.readObject();
                            if(passwordHash.equals(clientRequest.getPassword())) {
                                resp = new ServerResponse("Авторизация успешна");
                                outputStream.writeObject(resp);

                                login = clientRequest.getLogin();
                                Main.log.info("Client with login " + login + " authorised successfully");
                                break;
                            }
                            else {
                                resp = new ServerResponse("Неверный логин/пароль, попробуйте снова");
                                resp.setAccess(false);
                                outputStream.writeObject(resp);
                            }
                        }
                        else {
                            resp = new ServerResponse("Неверный логин/пароль, попробуйте снова");
                            resp.setAccess(false);
                            outputStream.writeObject(resp);
                        }
                    }
                    if (command.getClass() == SignUp.class) {
                        ResultSet resultSet = statement.executeQuery("select * from users where username='" +clientRequest.getLogin()+ "'");
                        if(!resultSet.next()){
                            byte[] array = new byte[9];
                            new Random().nextBytes(array);
                            String salt = new String(array, StandardCharsets.UTF_8);

                            resp = new ServerResponse(salt);

                            outputStream.writeObject(resp);
                            clientRequest = (ClientRequest) inputStream.readObject();

                            try {
                                String sql = "insert into users values ('" + clientRequest.getLogin() + "' , '" + clientRequest.getPassword() + "' , '" + salt + "' )";
                                statement.execute(sql);

                                resp = new ServerResponse("Регистрация и авторизация успешна");
                                outputStream.writeObject(resp);

                                login = clientRequest.getLogin();
                                Main.log.info("Client with login " + login + " registered and authorised successfully");
                                break;
                            }catch (SQLException e){
                                e.printStackTrace();
                                resp = new ServerResponse("Ошибка регистрации, попробуйте еще раз.");
                                resp.setAccess(false);
                                outputStream.writeObject(resp);
                            }
                        }
                        else {
                            resp = new ServerResponse("Пользователь с данным login уже существует. Введите другой.");
                            resp.setAccess(false);
                            outputStream.writeObject(resp);
                        }
                    }
                }
                while (true) {
                    clientRequest = (ClientRequest) inputStream.readObject();
                    command = clientRequest.getCommand();
/*                    if (command.getCommandEnum() == EAvailableCommands.Save) {
                        Save save = (Save) command;
                        save.setFileName("sgd",login);
                        response = save.execute(set);
                        Main.log.fine("Command executed: " + command.getCommandEnum().toString()+" client "+login);*/
                    if (command.getCommandEnum() == EAvailableCommands.Execute_Script) {
                        ExecuteScript executeScript = (ExecuteScript) command;
                        executeScript.setCollectionFile("");
                        response = executeScript.execute(manager);
                        Main.log.fine("Command executed: " + command.getCommandEnum().toString()+" client "+login);
                    } else if (command.getCommandEnum() == EAvailableCommands.Info) {
                        Info info = (Info) command;
                        response = info.execute(manager);
                        Main.log.fine("Command executed: " + command.getCommandEnum().toString()+" client "+login);
                    } else {
                        response = command.execute(manager);
                        Main.log.fine("Command executed: " + command.getCommandEnum().toString()+" client "+login);
                    }
                    if (command.getCommandEnum() == EAvailableCommands.History) response.setText(historyPrinter());
                    historyMaker(command.getCommandEnum().toString());
                    outputStream.writeObject(response);
                }
            } catch (IOException e) {
                try {
                    if (inputStream.read() == -1) {
                        client.close();
                        Main.log.warning("The connection with client "+login+" was lost.");
                        throw new SocketHandlerException();
                    }
                } catch (IOException ex) {
                    throw new SocketHandlerException();
                }
            } catch (NullPointerException e) {
                Main.log.severe("Null pointer client "+login);
                throw new SocketHandlerException();
            } catch (ClassNotFoundException e) {
                Main.log.severe("Class not found client "+login);
                throw new SocketHandlerException();
            } catch (SQLException e) {
                e.printStackTrace();
                Main.log.severe("SQL error for client "+login);
                throw new SocketHandlerException();
            }
        } catch (SocketHandlerException e) {
            try {
                outputStream.writeObject(new ServerResponse("Критическая ошибка на стороне сервера", true));
            } catch (IOException ignored) {
            }
            Main.log.info(e.getMessage()+" for client "+ login);
        }
    }

    private void historyMaker(String name){
        if(history.size()<7){
            history.add(name);
        }
        else {
            for (int i=1;i<6;i++) {
                history.set(i, history.get(i + 1));
            }
            history.set(6,name);
        }
    }

    private String historyPrinter(){
        String out = "Последние введенные команды:\n";
        for(int i=0;i<history.size();i++){
            out = out+history.get(i)+"\n";
        }
        return out;
    }
}
