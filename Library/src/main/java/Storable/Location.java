package Storable;

import java.io.Serializable;

/**
 * Класс для хранения данных о положении
 */

public class Location implements Serializable {
    public Location(){};
    public Location(Integer x, Long y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    /** Поле для хранения координаты х. Поле не может быть null */
    private Integer x;
    /** Поле для хранения координаты у. Поле не может быть null */
    private Long y;
    /** Поле для хранения названия точки. Поле не может быть null */
    private String name;


    public Integer getX(){
        return x;
    }
    public Long getY(){
        return y;
    }
    public String getName(){
        return name;
    }
    public void setX(Integer X){
        x = X;
    }
    public void setY(Long Y){
        y = Y;
    }
    public void setName(String Name){
        name = Name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Location){
            Location other = (Location) obj;
            return other.x.equals(this.x) && other.y.equals(this.y) && other.name.equals(this.name);
        }
        return false;
    }

    @Override
    public String toString() {
        return " x: "+x+"\n y: "+y+"\n name: "+name;
    }
}
