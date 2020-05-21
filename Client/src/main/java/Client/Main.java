package Client;

import Client.Utils.EThemes;
import Commands.LogIn;
import Commands.SignUp;
import Controllers.MainWindowController;
import Instruments.ClientRequest;
import Instruments.SerializeManager;
import Instruments.ServerResponse;
import Instruments.ServerResponseCodes;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Main extends Application {
    public static String login="", password="";
    public static Stage stage;
    public static MainWindowController controller;
    private static boolean connected = false;
    private static boolean inMainWindow = false;
    private static SocketChannel socketChannel;
    public static SocketChannelHandler handler;
    private static Preferences preferences = Preferences.userRoot();

    public static boolean isConnected() {
        return connected;
    }

    public static void main(String[] args) {
        if(preferences.get("language",null)!=null){
            Locale.setDefault(new Locale(preferences.get("language","en"), preferences.get("country","US")));
        }else {
            preferences.put("language",Locale.getDefault().getLanguage());
            preferences.put("country",Locale.getDefault().getCountry());
            preferences.put("theme", EThemes.DEFAULT.toString());
            preferences.put("host","localhost");
            preferences.putInt("port",4004);
            preferences.putBoolean("alerts",true);
        }
        launch(args);
    }

    public static boolean connectServer() {
        try {
            SocketAddress a = new InetSocketAddress(preferences.get("host", "localhost"), preferences.getInt("port", 4004));
            socketChannel = SocketChannel.open(a);
            connected = true;
            return true;
        } catch (IOException e) {
            connected = false;
            return false;
        }
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
            //primaryStage.initStyle(StageStyle.UNDECORATED);

            primaryStage.setTitle("Authorization");
            primaryStage.setWidth(625);
            primaryStage.setHeight(400);
            primaryStage.setMaxHeight(400);
            primaryStage.setMaxWidth(625);
            primaryStage.setMinHeight(400);
            primaryStage.setMinWidth(625);

            primaryStage.show();
        } catch (IOException ignored) {
        }
    }

    public static void reconnect(){
        connected=false;
        connected=connectServer();
        if(connected){
            if(logIn(login,password,true)!=ServerResponseCodes.AUTHORISED){
                Platform.runLater(() -> {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR, ResourceBundle.getBundle("MessagesBundle").getString("alerts.errorAutoLogin"));
                    alert1.setTitle(ResourceBundle.getBundle("MessagesBundle").getString("alerts.exit"));
                    alert1.setResizable(false);
                    alert1.setHeaderText(ResourceBundle.getBundle("MessagesBundle").getString("alerts.errorAutoLogin"));
                    alert1.showAndWait();
                    Main.exitUser();
                });
            }else {
                handler = new SocketChannelHandler(socketChannel,controller,login,password);
                Thread thread = new Thread(handler);
                thread.setDaemon(true);
                thread.start();
            }
        }else{
            Platform.runLater(() -> {
                ButtonType ok = new ButtonType(ResourceBundle.getBundle("MessagesBundle").getString("alerts.reconnect"), ButtonBar.ButtonData.OK_DONE);
                ButtonType exit = new ButtonType(ResourceBundle.getBundle("MessagesBundle").getString("alerts.exit"), ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert alert1 = new Alert(Alert.AlertType.ERROR, ResourceBundle.getBundle("MessagesBundle").getString("alerts.serverDisconnect"), ok, exit);
                alert1.setTitle(ResourceBundle.getBundle("MessagesBundle").getString("alerts.connectionError"));
                alert1.setResizable(false);
                alert1.setHeaderText(ResourceBundle.getBundle("MessagesBundle").getString("alerts.connectionError"));
                Optional<ButtonType> result = alert1.showAndWait();
                if (result.orElse(exit) == ok) {
                    Main.reconnect();
                }else System.exit(0);
            });
        }
    }

    public static void exitUser(){
        try {
            connected=false;
            login="";
            password="";
            inMainWindow = false;
            connected = connectServer();
            socketChannel.configureBlocking(true);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

            Parent root = fxmlLoader.load(Main.class.getResource("/AuthWindow.fxml").openStream());
            Scene scene = new Scene(root);

            stage.setScene(scene);

            stage.setTitle("Authorization");
            stage.setWidth(600);
            stage.setHeight(400);
            stage.setMaxHeight(400);
            stage.setMaxWidth(600);

            stage.show();
        } catch (IOException e) {
            System.exit(1);
        }
    }

    public static void openMainWindow() {
        if (!inMainWindow) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

                Parent root = fxmlLoader.load(Main.class.getResource("/MainWindow.fxml").openStream());
                controller = fxmlLoader.getController();

                Scene scene = new Scene(root);
                EThemes themes = EThemes.valueOf(Preferences.userRoot().get("theme","DEFAULT"));
                controller.changeTheme(themes);
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
                System.exit(1);
            }
        }
    }

    public static ServerResponseCodes logIn(String username, String pass, boolean reconnection) {
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
                    if (!reconnection) {
                        MessageDigest messageDigest = MessageDigest.getInstance("MD2");
                        password = pass + sr.getAdditionalInfo();
                        messageDigest.reset();
                        messageDigest.update(password.getBytes());

                        password = DatatypeConverter.printHexBinary(messageDigest.digest());
                    }
                    login=username;

                    toServer.write(SerializeManager.toByte(new ClientRequest(new LogIn(), username, password)));
                    b = new byte[10000];
                    fromServer.read(b);
                    sr = (ServerResponse) SerializeManager.fromByte(b);
                }
                if (sr.getCode() != ServerResponseCodes.AUTHORISED) {
                } else{
                }
                return sr.getCode();
            } catch (NullPointerException | IOException | NoSuchAlgorithmException e) {
                return ServerResponseCodes.ERROR;
            }
        } else {
            return ServerResponseCodes.ERROR;
        }
    }

    public static ServerResponseCodes signUp(String username, String pass, String email) {
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
                if(sr.getCode()== ServerResponseCodes.AUTHORISED) {
                }
                return sr.getCode();
            } catch (IOException | NoSuchAlgorithmException e) {
                return ServerResponseCodes.ERROR;
            }
        }else {
            return ServerResponseCodes.ERROR;
        }
    }
}
