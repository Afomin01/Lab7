package Server;

import Instruments.ICollectionManager;
import Instruments.ServerResponseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ServerSocketHandler implements Runnable{

    private static ArrayList<HistoryHandler> historyHandlers = new ArrayList<>();

    public static void addClient(String login){
        historyHandlers.add(new HistoryHandler(login));
    }
    public static void addCommandToHistory(String user, String command){
        historyHandlers.stream().filter(r-> r.getLogin().equals(user)).forEach(r->r.addHistory(command));
    }
    public static String getHistory(String user){
        String out ="";
        for(HistoryHandler r : historyHandlers.stream().filter(r-> r.getLogin().equals(user)).collect(Collectors.toList())){
            out=r.getHistory();
        }
        return out;
    }
    public static void deleteHistory(String login){
        historyHandlers.removeIf(r-> r.getLogin().equals(login));
    }

    private final CollectionManager manager;

    private final ThreadPoolExecutor readPool;
    private final ThreadPoolExecutor executePool;
    private final ThreadPoolExecutor responsePool;

    private ServerSocketChannel ss;
    private Selector selector;

    private final Logger log = Main.log;

    public ServerSocketHandler(ICollectionManager<Route> manager, int port) {
        this.manager = (CollectionManager) manager;
        Properties property = new Properties();
        int read = 5;
        int execute = 10;
        int response = 5;
        SocketAddress address = new InetSocketAddress(port);

        try {
            ss = ServerSocketChannel.open();
            ss.bind(address);
            selector = Selector.open();
            ss.configureBlocking(false);
            ss.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            log.severe("Error while creating channel. Shutting down ...");
            for(Handler h : log.getHandlers())  h.close();
            System.exit(1);
        }

        log.info("Server started successfully. Port: " + port);

        try{
            //property.load(ClassLoader.getSystemClassLoader().getResourceAsStream("app.properties"));
            property.load(new FileInputStream("app.properties"));
            read = Integer.parseInt(property.getProperty("readPoolSize"));
            execute = Integer.parseInt(property.getProperty("executePoolSize"));
            response = Integer.parseInt(property.getProperty("writePoolSize"));

        }catch (Exception e){
            log.severe("Error reading properties. Default properties for pool sizes set.");
        }finally {
            readPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(read);
            executePool = (ThreadPoolExecutor) Executors.newFixedThreadPool(execute);
            responsePool = (ThreadPoolExecutor) Executors.newFixedThreadPool(response);
            log.info("Pool sizes:\nread pool: "+read+"\nexecute pool: "+execute+"\nresponse pool: "+response);
            this.manager.setExecutor(responsePool);
            this.manager.setSelector(selector);
        }
    }

    public void run() {
        SelectionKey key=null;
        while (true){
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    key = iterator.next();
                    if(!key.channel().isOpen()){
                        key.cancel();
                    }else {
                        if (key.isAcceptable()) {
                            SocketChannel client = ss.accept();
                            client.configureBlocking(false);
                            client.socket().setSendBufferSize(1000000);
                            client.register(selector, SelectionKey.OP_READ);
                            client.socket().setSendBufferSize(1000000);
                        }
                        if (key.isReadable()) {
                            readPool.execute(new SocketReader((SocketChannel) key.channel(), executePool, responsePool, manager));
                        }
                    }
                    iterator.remove();
                }
            }catch (IOException e){
                log.warning("IOException for client. Connection was closed.");
                responsePool.execute(new ResponseSender((SocketChannel) key.channel(), new ServerResponse(ServerResponseCodes.SERVER_FATAL_ERROR)));
            }
        }
    }
}
