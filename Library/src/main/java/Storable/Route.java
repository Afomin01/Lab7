package Storable;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * класс объекта коллекции, хранящий данные о маршруте
 */
public class Route implements Comparable<Route>, Serializable {
    public Route(){}
    public Route(long id, String name, Coordinates coordinates, Date creationDate, Location from, Location to, double distance, String owner) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.owner = owner;
    }
    public Route(String owner){
        this.owner=owner;
    }

    /** Поле для хранения уникального идентификатора объекта. Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически */
    private long id;
    /** Поле для хранения имени маршрута. Поле не может быть null, Строка не может быть пустой */
    private String name;
    /** Поле для хранения координат. Поле не может быть null */
    private Coordinates coordinates;
    /** Поле для хранения даты создаия маршрута. Поле не может быть null, Значение этого поля должно генерироваться автоматически*/
    private Date creationDate;
    /** Поле для хранения объекта класса {@link Location}, хранящего в себе информации о начальной локации маршрута. Поле не может быть null */
    private Location from;
    /** Поле для хранения объекта класса {@link Location}, хранящего в себе информации о конечной локации маршрута. Поле не может быть null */
    private Location to;
    /** Поле для хранения дистанции маршрута. Значение поля должно быть больше 1 */
    private double distance;
    /** Поле для хранения логина владельца файла */
    private String owner;

    public String getOwner() {
        return owner;
    }

    public double getValue(){
        return Math.abs(distance+coordinates.getx()+coordinates.gety()+from.getX()+from.getY()+to.getX()+to.getY());
    }

    @Override
    public int compareTo(Route o) {
        return Double.compare(this.getValue(), o.getValue());
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }


    public Coordinates getCoordinates(){
        return coordinates;
    }


    public Date getCreationDate(){
        return creationDate;
    }

    public Location getFrom(){
        return from;
    }

    public Location getTo(){
        return to;
    }

    public double getDistance(){
        return distance;
    }

    public void setId(long Id) {
        id = Id;
    }

    public void setName(String Name){
        name = Name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(Date date) {
        creationDate = date;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public void setDistance(double Distance){
        distance = Distance;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Route){
            Route other = (Route) obj;
            return other.id/100000000 == this.id/100000000 && other.name.equals(this.name) && other.coordinates.equals(this.coordinates) && other.creationDate.equals(this.creationDate) && other.from == this.from && other.to.equals(this.to) && other.distance == this.distance;
        }
        return false;
    }

    @Override
    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return "id: "+id+"\nname: "+name+"\ncoordinates:\n"+coordinates.toString()+"\ncreationDate: "+df.format(creationDate)+"\nfrom:\n"+from.toString()+"\nto:\n"+to.toString()+"\ndistance: "+distance+"\nowner: "+owner;

    }

    @Override
    public int hashCode() {
        return (int) (id/100000000 + coordinates.getx() + coordinates.gety() + creationDate.getTime()/100000000 + from.getY() + from.getX() + to.getY() + to.getX() + distance);
    }
}
