import Instruments.ClientRequest;
import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

public class SocketReader implements Runnable {
    private final SocketConnected socket;
    private final ThreadPoolExecutor executePool;
    private final ThreadPoolExecutor responsePool;
    private ClientRequest clientRequest;
    private final ICollectionManager<Route> manager;

    public SocketReader(SocketConnected socket, ThreadPoolExecutor executePool, ThreadPoolExecutor responsePool,ICollectionManager<Route> manager){
        this.socket = socket;
        this.executePool = executePool;
        this.responsePool = responsePool;
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            clientRequest = (ClientRequest) socket.getInputStream().readObject();
            executePool.execute(new RequestExecutor(socket,clientRequest,responsePool,manager));
        } catch (IOException e) {
            responsePool.execute(new ResponseSender(socket, clientRequest, new ServerResponse(ServerRespenseCodes.SERVER_FATAL_ERROR)));
            Main.log.severe("IOException for client " + socket.getLogin());
        } catch (ClassNotFoundException e) {
            responsePool.execute(new ResponseSender(socket, clientRequest, new ServerResponse(ServerRespenseCodes.SERVER_FATAL_ERROR)));
            Main.log.severe("ClassNotFound for client " + socket.getLogin());
        }
    }
}
