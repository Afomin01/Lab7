import Instruments.ClientRequest;
import Instruments.ServerResponse;

import java.io.IOException;

public class ResponseSender implements Runnable {
    private SocketConnected socket;
    private ClientRequest clientRequest;
    private ServerResponse serverResponse;

    public ResponseSender(SocketConnected socket, ClientRequest clientRequest, ServerResponse serverResponse) {
        this.socket = socket;
        this.clientRequest = clientRequest;
        this.serverResponse = serverResponse;
    }

    @Override
    public void run() {
        try {
            socket.getOutputStream().writeObject(serverResponse);
        }catch (IOException e){
            Main.log.severe("IOException for client "+clientRequest.getLogin());
        }
    }
}
