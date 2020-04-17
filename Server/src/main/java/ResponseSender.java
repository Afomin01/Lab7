import Instruments.ClientRequest;
import Instruments.SerializeManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ResponseSender implements Runnable {
    private SocketChannel client;
    private ServerResponse serverResponse;

    public ResponseSender(SocketChannel client, ServerResponse serverResponse) {
        this.client = client;
        this.serverResponse = serverResponse;
    }

    @Override
    public void run() {
        try {
            ByteBuffer buf = ByteBuffer.allocate(5000);
            buf.clear();
            buf.put(SerializeManager.toByte(serverResponse));
            buf.flip();
            while (buf.hasRemaining()) {
                client.write(buf);
            }
            buf.clear();
            if(serverResponse.getCode()==ServerRespenseCodes.SERVER_FATAL_ERROR || serverResponse.getCode()==ServerRespenseCodes.EXIT){
                try {
                    client.close();
                } catch (IOException ignored) {
                }
            }

        }catch (IOException e){
            if (serverResponse.getCode() != ServerRespenseCodes.SERVER_FATAL_ERROR) {
                Main.log.severe("IOException for client.");
            }
            try {
                client.close();
            } catch (IOException ignored) {
            }
        }
    }
}
