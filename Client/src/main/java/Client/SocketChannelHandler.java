package Client;

import Commands.GetTableItems;
import Controllers.MainWindowController;
import Instruments.ClientRequest;
import Instruments.SerializeManager;
import Instruments.ServerResponseCodes;
import Instruments.ServerResponse;
import Storable.Route;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

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
                int read = socketChannel.read(buf);
                if(read>3){
                    if(read>15000) {
                        Thread.sleep(200);
                        socketChannel.read(buf);
                    }
                    serverResponse = (ServerResponse) SerializeManager.fromByte(buf.array());
                    if(serverResponse.getCode().equals(ServerResponseCodes.SET_ONLY)){
                        controller.updateTableView(FXCollections.observableList(serverResponse.getSet()));
                    }else if (serverResponse.getCode().equals(ServerResponseCodes.NEW_ITEM_OR_UPDATE)){
                        controller.addTableViewItem(serverResponse.getRoute());
                    }else if (serverResponse.getCode().equals(ServerResponseCodes.REMOVE_ITEMS_UPDATE)){
                        controller.removeItems(FXCollections.observableList(serverResponse.getSet()));
                    }else{
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
            }
        } catch (IOException | NullPointerException | InterruptedException e) {
            e.printStackTrace();
            try {
                int read = socketChannel.read(buf);
            } catch (IOException ioException) {

            }
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
            e.printStackTrace();
        }
    }
}
