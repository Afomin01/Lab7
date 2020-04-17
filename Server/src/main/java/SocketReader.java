import Instruments.*;
import Storable.Route;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public class SocketReader implements Runnable {
    private final SocketChannel client;
    private final ThreadPoolExecutor executePool;
    private final ThreadPoolExecutor responsePool;
    private ClientRequest clientRequest;
    private final ICollectionManager<Route> manager;
    ByteBuffer buf = ByteBuffer.allocate(10000);

    public SocketReader(SocketChannel client, ThreadPoolExecutor executePool, ThreadPoolExecutor responsePool,ICollectionManager<Route> manager){
        this.client = client;
        this.executePool = executePool;
        this.responsePool = responsePool;
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            buf.clear();
            int bytesRead = client.read(buf);
            if (bytesRead > 4) {
                buf.flip();
                clientRequest = (ClientRequest) SerializeManager.fromByte(buf.array());
                if(clientRequest==null) throw new NullRequestException();
                executePool.execute(new RequestExecutor(client, clientRequest, responsePool,manager));
            }

        } catch (IOException e){
            responsePool.execute(new ResponseSender(client, new ServerResponse(ServerRespenseCodes.SERVER_FATAL_ERROR)));
            Main.log.severe("IOException for client. Disconnecting...");
        } catch (NullRequestException e) {
            responsePool.execute(new ResponseSender(client, new ServerResponse(ServerRespenseCodes.SERVER_FATAL_ERROR)));
            Main.log.severe("Null request for client. Disconnecting...");
        }
    }
}
