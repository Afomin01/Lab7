package Storable;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * класс объекта коллекции, хранящий данные о маршруте
 */
public class Route implements Comparable<Route>, Serializable {
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

    /**
     * Функция получения абстрактного значения маршрута, получаемая сложением всех полей, содержащих числа.
     * @return абстрактное значение маршрута, необходимое для сравнения объектов данного классаю Всегда положительно.
     */
    public double getValue(){
        return Math.abs(distance+coordinates.getx()+coordinates.gety()+from.getX()+from.getY()+to.getX()+to.getY());
    }

    @Override
    public int compareTo(Route o) {
        return Double.compare(this.getValue(), o.getValue());
    }

    /**
     * Функция получения значения поля {@link Route#id}
     * @return уникальный идентификатор объекта
     */
    public long getId(){
        return id;
    }

    /**
     * Функция получения значения поля {@link Route#name}
     * @return имя маршрута
     */
    public String getName(){
        return name;
    }

    /**
     * Функция получения значения поля {@link Route#coordinates}
     * @return объект класса {@link Coordinates}
     */
    public Coordinates getCoordinates(){
        return coordinates;
    }

    /**
     * Функция получения значения поля {@link Route#creationDate}
     * @return дату создания
     */
    public Date getCreationDate(){
        return creationDate;
    }

    /**
     * Функция получения значения поля {@link Route#from}
     * @return объект класса {@link Location}, хранящий информацию о начальной точке маршрута
     */
    public Location getFrom(){
        return from;
    }

    /**
     * Функция получения значения поля {@link Route#to}, хранящий информацию о конечной точке маршрута
     * @return объект класса {@link Location}
     */
    public Location getTo(){
        return to;
    }

    /**
     * Функция получения значения поля {@link Route#distance}
     * @return уникальный идентификатор объекта
     */
    public double getDistance(){
        return distance;
    }

    /**
     * * Метод для присваивания значения полю {@link Route#id}
     * @param Id значение уникального идентификатора
     */
    public void setId(long Id) {
        id = Id;
    }

    /**
     * * Метод для присваивания значения полю {@link Route#name}
     * @param Name название маршрута
     */
    public void setName(String Name){
        name = Name;
    }

    /**
     * * Метод для присваивания значения полю {@link Route#coordinates}
     * @param coordinates объект класса {@link Coordinates}, содержащий данные о координатах
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * * Метод для присваивания значения полю {@link Route#creationDate}
     * @param date дата создания маршрута
     */
    public void setCreationDate(Date date) {
        creationDate = date;
    }

    /**
     * * Метод для присваивания значения полю {@link Route#from}
     * @param from объект класса {@link Location}, содержащий информацию о начальной точке маршрута
     */
    public void setFrom(Location from) {
        this.from = from;
    }

    /**
     * * Метод для присваивания значения полю {@link Route#to}
     * @param to объект класса {@link Location}, содержащий информацию о конечной точке маршрута
     */
    public void setTo(Location to) {
        this.to = to;
    }

    /**
     * * Метод для присваивания значения полю {@link Route#distance}
     * @param Distance значение дистанции
     */
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
        return "id: "+id+"\nname: "+name+"\ncoordinates:\n"+coordinates.toString()+"\ncreationDate: "+df.format(creationDate)+"\nfrom:\n"+from.toString()+"\nto:\n"+to.toString()+"\ndistance: "+distance;

    }

    @Override
    public int hashCode() {
        return (int) (id/100000000 + coordinates.getx() + coordinates.gety() + creationDate.getTime()/100000000 + from.getY() + from.getX() + to.getY() + to.getX() + distance);
    }
}
