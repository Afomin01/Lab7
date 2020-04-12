import Instruments.ICollectionManager;
import Storable.Coordinates;
import Storable.Location;
import Storable.Route;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Handler;
import java.util.stream.Stream;

public class CollectionManager implements ICollectionManager<Route> {
    private Connection connection;
    private final Set<Route> syncSet = Collections.synchronizedSet(new LinkedHashSet<>());

    public Set<Route> getSyncSet() {
        return syncSet;
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
    public boolean add(Route element) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "insert into routes(name, coordinate_x, coordinate_y, creation_date, from_x, from_y, from_name, to_x, to_y, to_name, distance, owner) values (" +
                    element.getName() + ", " +
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
                return syncSet.add(element);
            }
            else{
                return false;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeAll(Set<Route> c) {
        return syncSet.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<Route> filter) {
        return syncSet.removeIf(filter);
    }

    @Override
    public Set<Route> getSet() {
        return syncSet;
    }

    @Override
    public Stream<Route> stream() {
        return syncSet.stream();
    }
}
//TODO singleton