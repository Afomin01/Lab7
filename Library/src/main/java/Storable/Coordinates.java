package Storable;

import java.io.Serializable;

/**
 * Класс, отвечающий за хранение координат с полями <b>x</b> и <b>y</b>
 */
public class Coordinates implements Serializable {
    public Coordinates(){};
    public Coordinates(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    /** Поле для хранения координаты х */
    private Double x;
    /** Поле для хранения координаты у. Значение поля должно быть больше -462, Поле не может быть null */
    private Double y;

    public double getx(){
        return x;
    }
    public Double gety(){
        return y;
    }
    public void setx(Double X){
        x = X;
    }
    public void sety(Double Y){
        y = Y;
    }

    @Override
    public String toString() {
        return " x: "+x+"\n y: "+y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Coordinates){
            Coordinates other = (Coordinates) obj;
            return other.x.equals(this.x) && other.y.equals(this.y);
        }
        return false;
    }

}
