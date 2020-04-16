import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SocketsHandler implements Runnable {

    private HashMap<Long,SocketConnected> mapOfSockets = new HashMap<>();
    private HashMap<Long,SocketConnected> socketsToAdd = new HashMap<>();

    private final ICollectionManager<Route> manager;

    private final ThreadPoolExecutor readPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    private final ThreadPoolExecutor executePool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private final ThreadPoolExecutor responsePool = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    public SocketsHandler(ICollectionManager<Route> manager) {
        this.manager = manager;
    }

    public void addSocket(Long id, SocketConnected socket){
        socketsToAdd.put(id,socket);
        Main.log.info("Accepted new socket with id "+id);
    }

    @Override
    public void run() {
        ArrayList<Long> IdToDelete = new ArrayList<>();
        while(true){
            System.out.println("fdglkdfs");
            if(!socketsToAdd.isEmpty()){
                mapOfSockets.putAll(socketsToAdd);
            }
            socketsToAdd.clear();
            for(int i=0;i<IdToDelete.size();i++){
                Main.log.info("Socket was shut down for client " + mapOfSockets.get(i).getLogin());
                mapOfSockets.remove(IdToDelete.get(i));
            }
            IdToDelete.clear();
            Set<Long> temp = mapOfSockets.keySet();
            for(long i : temp){
                try {
                    if(mapOfSockets.get(i).getInputStream().available()>0){
                        readPool.execute(new SocketReader(mapOfSockets.get(i),executePool,responsePool,manager));
                    }
                }catch (IOException e){
                    try{
                        mapOfSockets.get(i).getOutputStream().writeObject(new ServerResponse(ServerRespenseCodes.SERVER_FATAL_ERROR));
                    }catch (IOException ignored){
                    }
                    IdToDelete.add(i);
                }
            }
        }
    }
}
