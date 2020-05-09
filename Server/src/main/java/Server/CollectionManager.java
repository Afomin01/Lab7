package Server;

import Instruments.ICollectionManager;
import Instruments.ManagerResponseCodes;
import Instruments.ServerResponse;
import Instruments.ServerResponseCodes;
import Storable.Coordinates;
import Storable.Location;
import Storable.Route;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;
import java.util.logging.Handler;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionManager implements ICollectionManager<Route> {
    private Connection connection;
    private final Set<Route> syncSet = Collections.synchronizedSet(new LinkedHashSet<>());
    private ThreadPoolExecutor executor;
    private Selector selector;

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    private void sendUpdate(Route route){
        ServerResponse serverResponse = new ServerResponse(ServerResponseCodes.NEW_ITEM_OR_UPDATE);
        serverResponse.setRoute(route);
        SelectionKey key;
        Set<SelectionKey> selectedKeys = selector.keys();;
        Iterator<SelectionKey> iterator = selectedKeys.iterator();

        while (iterator.hasNext()) {
            key = iterator.next();
            if (key.channel().isOpen()) {
                if (key.interestOps()== SelectionKey.OP_READ){
                    executor.execute(new ResponseSender((SocketChannel) key.channel(), serverResponse));
                }
            }
        }
    }
    private void sendRemoveItems(ArrayList<Route> list){
        ServerResponse serverResponse = new ServerResponse(ServerResponseCodes.REMOVE_ITEMS_UPDATE);
        serverResponse.setSet(list);
        SelectionKey key;
        Set<SelectionKey> selectedKeys = selector.keys();
        Iterator<SelectionKey> iterator = selectedKeys.iterator();

        while (iterator.hasNext()) {
            key = iterator.next();
            if (key.channel().isOpen()) {
                if (key.interestOps()== SelectionKey.OP_READ){
                    executor.execute(new ResponseSender((SocketChannel) key.channel(), serverResponse));
                }
            }
        }
    }
    public CollectionManager(Connection connection){
        this.connection = connection;
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("select * from routes");
            while (resultSet.next()){
                Route route = new Route(resultSet.getLong(1),
                        resultSet.getString(2),
                        new Coordinates(resultSet.getDouble(3),resultSet.getDouble(4)),
                        resultSet.getTimestamp(5),
                        new Location(resultSet.getInt(6),resultSet.getLong(7),resultSet.getString(8)),
                        new Location(resultSet.getInt(9),resultSet.getLong(10),resultSet.getString(11)),
                        resultSet.getDouble(12),
                        resultSet.getString(13));
                syncSet.add(route);
            }
        } catch (SQLException e) {
            Main.log.severe("Error reading collection from database. Shutting down ...");
            for(Handler h : Main.log.getHandlers())  h.close();
            System.exit(1);
        }
    }

    @Override
    public ManagerResponseCodes add(Route element) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "insert into routes(name, coordinate_x, coordinate_y, creation_date, from_x, from_y, from_name, to_x, to_y, to_name, distance, owner) values ('" +
                    element.getName() + "', " +
                    element.getCoordinates().getx() + ", " +
                    element.getCoordinates().gety() + ", '" +
                    df.format(element.getCreationDate()) + "', " +
                    element.getFrom().getX() + ", " +
                    element.getFrom().getY() + ", '" +
                    element.getFrom().getName() + "', " +
                    element.getTo().getX() + ", " +
                    element.getTo().getY() + ", '" +
                    element.getTo().getName() + "', " +
                    element.getDistance() + ", '" +
                    element.getOwner() + "') returning id";
            ResultSet rs = connection.createStatement().executeQuery(sql);
            if(rs.next()){
                element.setId(rs.getLong(1));
                syncSet.add(element);
                sendUpdate(element);
                return ManagerResponseCodes.OK;
            }
            else{
                return ManagerResponseCodes.UNKNOWN_ERROR;
            }
        }
        catch (SQLException e){
            return ManagerResponseCodes.SQL_ERROR;
        }
    }

    @Override
    public ManagerResponseCodes removeAll(Set<Route> c, String user) {
        try {
            String sql;
            ArrayList<Route> removeList = new ArrayList<>();
            int count = 0;
            for (Route r : c) {
                sql = "delete from routes where id="+r.getId()+" and owner='"+user+"'";
                if(connection.createStatement().executeUpdate(sql)>0){
                    if(syncSet.remove(r)){
                        removeList.add(r);
                        count++;
                    }
                }
            }
            if(count>0) {
                sendRemoveItems(removeList);
                return ManagerResponseCodes.OK;
            }
            else return ManagerResponseCodes.NO_CHANGES;
        }catch (SQLException e){
            return ManagerResponseCodes.SQL_ERROR;
        }
    }

    @Override
    public ManagerResponseCodes removeIf(Predicate<Route> filter, String user) {
        try {
            String sql;
            ArrayList<Route> removeList = new ArrayList<>();
            int count = 0;
            Set<Route> temp = syncSet.stream().filter(filter).collect(Collectors.toSet());
            for (Route r : temp) {
                sql = "delete from routes where id="+r.getId()+" and owner='"+user+"'";
                if(connection.createStatement().executeUpdate(sql)>0){
                    if(syncSet.remove(r)){
                        removeList.add(r);
                        count++;
                    }
                }
            }
            if(count>0){
                sendRemoveItems(removeList);
                return ManagerResponseCodes.OK;
            }
            else return ManagerResponseCodes.NO_CHANGES;
        }catch (SQLException e){
            return ManagerResponseCodes.SQL_ERROR;
        }
    }

    @Override
    public ManagerResponseCodes update(long id, Route r, String user) {
        try{
            String sql="select count(*) from routes where id="+id+" and owner='"+user+"'";
            ResultSet rs = connection.createStatement().executeQuery(sql);
            rs.next();
            if(rs.getLong(1)>0){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sql="update routes " +
                        "set name='"+r.getName()+
                        "', coordinate_x="+r.getCoordinates().getx()+
                        " , coordinate_y="+r.getCoordinates().gety()+
                        " , creation_date='"+df.format(r.getCreationDate())+
                        "', from_x="+r.getFrom().getX()+
                        " , from_y="+r.getFrom().getY()+
                        " , from_name='"+r.getFrom().getName()+
                        "', to_x="+r.getTo().getX()+
                        " , to_y="+r.getTo().getY()+
                        " , to_name='"+r.getTo().getName()+
                        "', distance="+r.getDistance()+
                        " , owner='"+r.getOwner()+"' where id="+id+" and owner='"+user+"'";
                if(connection.createStatement().executeUpdate(sql)>0) {
                    Route adding = new Route(id, r.getName(),r.getCoordinates(),r.getCreationDate(),r.getFrom(),r.getTo(),r.getDistance(),r.getOwner());
                    syncSet.removeIf(t->(t.getId()==id && t.getOwner().equals(user)) );
                    syncSet.add(adding);
                    sendUpdate(adding);
                    return ManagerResponseCodes.OK;
                }else return ManagerResponseCodes.NO_CHANGES;
            } else return ManagerResponseCodes.NO_CHANGES;
        }catch (SQLException e){
            return ManagerResponseCodes.SQL_ERROR;
        }
    }

    @Override
    public Stream<Route> stream() {
        return syncSet.stream();
    }
}