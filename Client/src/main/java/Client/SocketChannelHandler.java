package Client;

import Commands.GetTableItems;
import Commands.ICommand;
import Controllers.MainWindowController;
import Instruments.ClientRequest;
import Instruments.SerializeManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelHandler implements Runnable{
    ByteBuffer buf = ByteBuffer.allocate(1000000);
    ObservableList<Route> list;
    String login, password;

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
                    serverResponse = (ServerResponse) SerializeManager.fromByte(buf.array());
                    if(serverResponse.getCode().equals(ServerRespenseCodes.SET_ONLY)){
                        list = FXCollections.observableList(serverResponse.getSet());
                        controller.updateTableView(list);
                    }
                }
                buf.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendRequest(ClientRequest request) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10000);
        byteBuffer.clear();
        byteBuffer.put(SerializeManager.toByte(request));
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {
            socketChannel.write(byteBuffer);
        }
        byteBuffer.clear();
    }
}
