package Client;

import Commands.LogIn;
import Commands.SignUp;
import Controllers.MainWindowController;
import Controllers.TableTabController;
import Instruments.ClientRequest;
import Instruments.SerializeManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    public static String login="", password="";
    public static Stage stage;
    public static MainWindowController controller;
    private static boolean loggedIn = false;
    private static boolean connected = false;
    private static boolean inMainWindow = false;
    private static SocketChannel socketChannel;
    public static SocketChannelHandler handler;//MAKE METHODS

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static boolean isConnected() {
        return connected;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static boolean connectServer() {
        if (!connected) {
            try {
                SocketAddress a = new InetSocketAddress("localhost", 4004);
                socketChannel = SocketChannel.open(a);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else return true;
    }

    @Override
    public void start(Stage primaryStage) {
        connected = connectServer();
        try {
            stage = primaryStage;
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

            Parent root = fxmlLoader.load(this.getClass().getResource("/AuthWindow.fxml").openStream());
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);

            primaryStage.setTitle("Authorization");
            primaryStage.setWidth(600);
            primaryStage.setHeight(400);
            primaryStage.setMaxHeight(400);
            primaryStage.setMaxWidth(600);

            primaryStage.show();
        } catch (IOException ignored) {
        }
    }

    public static void openMainWindow() {
        if (loggedIn && !inMainWindow) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

                Parent root = fxmlLoader.load(Main.class.getResource("/MainWindow.fxml").openStream());
                controller = fxmlLoader.getController();

                Scene scene = new Scene(root);
                stage.setScene(scene);

                stage.setTitle("Divoc");
                stage.setWidth(1920);
                stage.setHeight(1080);
                stage.setMaxHeight(Integer.MAX_VALUE);
                stage.setMaxWidth(Integer.MAX_VALUE);
                stage.setMaximized(true);
                inMainWindow = true;
                handler = new SocketChannelHandler(socketChannel,controller,login,password);
                Thread thread = new Thread(handler);
                thread.setDaemon(true);
                thread.start();

            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }
    }

    public static ServerRespenseCodes logIn(String username, String pass) {
        if (connected) {
            try {
                InputStream fromServer = socketChannel.socket().getInputStream();
                OutputStream toServer = socketChannel.socket().getOutputStream();
                toServer.flush();
                toServer.write(SerializeManager.toByte(new ClientRequest(new LogIn(), username, "%")));
                byte[] b = new byte[10000];
                fromServer.read(b);
                ServerResponse sr = (ServerResponse) SerializeManager.fromByte(b);
                if (sr.isAccess()) {
                    MessageDigest messageDigest = MessageDigest.getInstance("MD2");
                    password = pass + sr.getAdditionalInfo();
                    messageDigest.reset();
                    messageDigest.update(password.getBytes());

                    password = DatatypeConverter.printHexBinary(messageDigest.digest());
                    login=username;

                    toServer.write(SerializeManager.toByte(new ClientRequest(new LogIn(), username, password)));
                    b = new byte[10000];
                    fromServer.read(b);
                    sr = (ServerResponse) SerializeManager.fromByte(b);
                }
                if (sr.getCode() != ServerRespenseCodes.AUTHORISED) {
                    loggedIn = false;
                } else{
                    loggedIn = true;
                }
                return sr.getCode();
            } catch (NullPointerException | IOException | NoSuchAlgorithmException e) {
                loggedIn = false;
                return ServerRespenseCodes.ERROR;
            }
        } else {
            loggedIn = false;
            return ServerRespenseCodes.ERROR;
        }
    }

    public static ServerRespenseCodes signUp(String username, String pass, String email) {
        if (connected) {
            try {
                InputStream fromServer = socketChannel.socket().getInputStream();
                OutputStream toServer = socketChannel.socket().getOutputStream();
                toServer.flush();
                toServer.write(SerializeManager.toByte(new ClientRequest(new SignUp(), username, "%")));
                byte[] b = new byte[100000];
                fromServer.read(b);
                ServerResponse sr = (ServerResponse) SerializeManager.fromByte(b);
                if (sr.isAccess()) {
                    SignUp signUp = new SignUp();
                    signUp.setEmail("no");
                    if (!email.equals("no")) signUp.setPassword(pass);
                    String passwordc = pass;

                    MessageDigest messageDigest = MessageDigest.getInstance("MD2");
                    password = pass + sr.getAdditionalInfo();
                    messageDigest.reset();
                    messageDigest.update(password.getBytes());
                    password = DatatypeConverter.printHexBinary(messageDigest.digest());
                    login=username;
                    signUp.setSalt(sr.getAdditionalInfo());

                    toServer.write(SerializeManager.toByte(new ClientRequest(signUp, username, password)));
                    b = new byte[100000];
                    fromServer.read(b);
                    sr = (ServerResponse) SerializeManager.fromByte(b);
                }
                if(sr.getCode()==ServerRespenseCodes.AUTHORISED) loggedIn=true;
                else loggedIn=false;
                return sr.getCode();
            } catch (IOException | NoSuchAlgorithmException e) {
                loggedIn = false;
                return ServerRespenseCodes.ERROR;
            }
        }else {
            loggedIn = false;
            return ServerRespenseCodes.ERROR;
        }
    }
/*
            while (true) {
                try {
                    currentCommand = reader.readLine();
                    if (currentCommand == null){
                        toServer.write(SerializeManager.toByte(new ClientRequest(new Exit(login), login, password)));

                        byte[] b = new byte[10000];
                        fromServer.read(b);
                        ServerResponse resp = (ServerResponse) SerializeManager.fromByte(b);
                        outputInfo("Введен специальный символ. Завершение работы...");
                        System.exit(1);
                    }
                    ICommand command = commandFactory.getCommand(currentCommand.trim().split(" "), reader, login);
                    if (command != null) {

                        toServer.write(SerializeManager.toByte(new ClientRequest(command,login,password)));

                        byte[] b = new byte[100000];
                        fromServer.read(b);
                        ServerResponse response = (ServerResponse) SerializeManager.fromByte(b);
                        serverResponseDecode(response);
                        dollar();
                    }
                }catch (IOException | NullPointerException e) {
                    long t = System.currentTimeMillis();
                    long end = t + 15000;
                    foo=false;
                    while (System.currentTimeMillis() < end) {
                        try {
                            outputInfo("Ожидание...");
                            channel = new Socket("localhost", port);

                            fromServer = channel.getInputStream();
                            toServer = channel.getOutputStream();
                            toServer.flush();

                            reader = System.console();
                            foo=true;
                            outputInfo("Подключение восстановлено.");
                            dollar();
                            break;
                        } catch (Exception ignored) {
                            Thread.sleep(500);
                        }
                    }
                    if(!foo){
                        outputInfo("Ошибка повторного подключения. Завершение работы...");
                        System.exit(1);
                    }
                }
            }

        } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
            outputInfo("Ошибка подключения к серверу. Завершение работы...");
            System.exit(1);
        }
    }*/


    public static void serverResponseDecode(ServerResponse response) throws NullPointerException{
/*        switch (response.getCode()){
            case SEARCH_OK:
                outputInfo("Результаты поиска:\n"+response.getAdditionalInfo() + "\n");
                break;
            case COUNT:
                outputInfo("Результаты подсчета:\n"+response.getAdditionalInfo() + "\n");
                break;
            case SEARCH_NOT_FOUND:
                outputInfo("Поиск не дал результатов.");
                break;
            case SQL_ERROR:
                outputInfo("Ошибка исполнения запроса. Попробуйте еще раз.");
                break;
            case ADD_OK:
                outputInfo("Элемент успешно добавлен.");
                break;
            case NO_CHANGES:
                outputInfo("Коллекция не изменилась");
                break;
            case DELETE_OK:
                outputInfo("Удаление успешно");
                break;
            case CLEAR_OK:
                outputInfo("Все Ваши элементы успешно удалены");
                break;
            case UPDATE_OK:
                outputInfo("Поля элемента успешно обновлены");
                break;
            case ERROR:
                outputInfo("Неизвестная ошибка. Попробуйте еще раз.");
                break;
            case SCRIPT_RESULT:
                for (ServerResponse r : response.getScriptReport()){
                    serverResponseDecode(r);
                }
                break;
            case SERVER_FATAL_ERROR:
                outputInfo("\nКритическая ошибка сервера. Завершение работы...");
                System.exit(0);
                break;
            case TEXT_ONLY:
                outputInfo("Ответ сервера: \n"+response.getAdditionalInfo() + "\n");
                break;
            case EXIT:
                outputInfo( "\nЗавершение работы...");
                System.exit(0);
                break;
            case CONNECTED:
                outputInfo("Соединение установлено");
                break;
            case SURPRISE_NOT_CORRECT_LOGIN_OR_PASSWORD:
                outputInfo("Каким-то образом вы пытались выполнить команду, введя неверные данные для входа! Хакерам здесь не рады!:)");
                break;
            case SCRIPT_REC:
                outputInfo("В процессе выполнения скрипта юыла обнаружена рекурсия. Дальнейшее выполнение не производится."+response.getAdditionalInfo());
                break;
            case SCRIPT_FILE_ERR:
                outputInfo("Обнаружена ошибка чтения файла. Проверьте доступность и правильность пути к файлу.");
                break;
            case SCRIPT_COMMAND_ERR:
                outputInfo("В процессе выполнения была обнаружена ошибка в файле скрипта. Дальнейшее выполнение невозможно."+response.getAdditionalInfo());
                break;
            case SHOW:
                outputInfo("Элементы коллекции:\n"+response.getAdditionalInfo());
        }*/
    }
}
