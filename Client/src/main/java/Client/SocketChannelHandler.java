package Client;

import Client.Utils.UniversalServerResponseDecoder;
import Commands.GetTableItems;
import Controllers.MainWindowController;
import Instruments.ClientRequest;
import Instruments.SerializeManager;
import Instruments.ServerResponseCodes;
import Instruments.ServerResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.ResourceBundle;

public class SocketChannelHandler implements Runnable{
    private ByteBuffer buf = ByteBuffer.allocate(1000000);
    private ByteBuffer buf2 = ByteBuffer.allocate(1000000);
    private String login, password;

    private SocketChannel socketChannel;
    private MainWindowController controller;

    public SocketChannelHandler(SocketChannel socketChannel, MainWindowController controller, String login, String password) {
        this.socketChannel=socketChannel;
        this.controller = controller;
        this.login = login;
        this.password = password;
    }

    @Override
    public void run() {
        try {
            socketChannel.configureBlocking(false);
            socketChannel.socket().setReceiveBufferSize(1000000);
            ServerResponse serverResponse;
            sendRequest(new ClientRequest(new GetTableItems(),login,password));

            while (true){
                try {
                    if (Thread.interrupted() || socketChannel.isBlocking()) break;
                    int read = socketChannel.read(buf);
                    if(read==-1) {
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
                        break;
                    }
                    if (read > 3) {
                        if (read > 15000) {
                            Thread.sleep(200);
                            socketChannel.read(buf);
                        }
                        serverResponse = (ServerResponse) SerializeManager.fromByte(buf.array());
                        if (serverResponse.getCode().equals(ServerResponseCodes.SET_ONLY)) {
                            controller.updateTableView(FXCollections.observableList(serverResponse.getSet()));
                        } else if (serverResponse.getCode().equals(ServerResponseCodes.NEW_ITEM_OR_UPDATE)) {
                            controller.addTableViewItem(serverResponse.getRoute());
                        } else if(serverResponse.getCode().equals(ServerResponseCodes.REMOVE_ITEMS_UPDATE)){
                            controller.removeItems(FXCollections.observableList(serverResponse.getSet()));
                        } else if (serverResponse.getCode().equals(ServerResponseCodes.REMOVE_ITEM_BY_ID)) {
                            ServerResponse tmpresp = new ServerResponse(serverResponse.getCode());
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setResizable(false);
                                    alert.setHeaderText(UniversalServerResponseDecoder.decodeResponse(tmpresp));
                                    alert.setTitle(UniversalServerResponseDecoder.decodeResponse(tmpresp));
                                    alert.showAndWait();
                                }
                            });
                        } else if(serverResponse.getCode().equals(ServerResponseCodes.CHANGE_FIELDS_NO_RIGHTS)||serverResponse.getCode().equals(ServerResponseCodes.CHANGE_FIELDS_ERROR)||serverResponse.getCode().equals(ServerResponseCodes.CHANGE_FIELDS_OK)){
                            ServerResponse tmpresp = new ServerResponse(serverResponse.getCode());
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setResizable(false);
                                    alert.setHeaderText(UniversalServerResponseDecoder.decodeResponse(tmpresp));
                                    alert.setTitle(UniversalServerResponseDecoder.decodeResponse(tmpresp));
                                    alert.showAndWait();
                                }
                            });
                        } else {
                            ServerResponse sr = serverResponse;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    controller.getCommandsTabController().displayServerResponse(sr);
                                }
                            }).start();
                        }
                    }
                    buf.clear();
                }catch (IOException | NullPointerException e ){
                }
            }
        } catch (IOException | NullPointerException | InterruptedException e) {
        }
    }
    public void sendRequest(ClientRequest request){
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(10000);
            byteBuffer.clear();
            byteBuffer.put(SerializeManager.toByte(request));
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                socketChannel.write(byteBuffer);
            }
            byteBuffer.clear();
        }catch (IOException e){
        }
    }
}
