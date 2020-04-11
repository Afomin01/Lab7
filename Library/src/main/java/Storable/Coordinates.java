package Storable;

import java.io.Serializable;

/**
 * Класс, отвечающий за хранение координат с полями <b>x</b> и <b>y</b>
 */
public class Coordinates implements Serializable {
    /** Поле для хранения координаты х */
    private Double x;
    /** Поле для хранения координаты у. Значение поля должно быть больше -462, Поле не может быть null */
    private Double y;

    /**
     * Функция получения значения поля {@link Coordinates#x}
     * @return координату х
     */
    public double getx(){
        return x;
    }
    /**
     * Функция получения значения поля {@link Coordinates#y}
     * @return координату y
     */
    public Double gety(){
        return y;
    }

    /**
     * Метод для присваивания значения полю {@link Coordinates#x}
     * @param X значение для координаты х
     */
    public void setx(Double X){
        x = X;
    }

    /**
     * * Метод для присваивания значения полю {@link Coordinates#y}
     * @param Y значение для координаты у
     */
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
