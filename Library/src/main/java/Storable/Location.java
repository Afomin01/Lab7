package Storable;

import java.io.Serializable;

/**
 * Класс для хранения данных о положении
 */

public class Location implements Serializable {
    /** Поле для хранения координаты х. Поле не может быть null */
    private Integer x;
    /** Поле для хранения координаты у. Поле не может быть null */
    private Long y;
    /** Поле для хранения названия точки. Поле не может быть null */
    private String name;

    /**
     * Функция получения значения поля {@link Location#x}
     * @return координату х
     */
    public Integer getX(){
        return x;
    }

    /**
     * Функция получения значения поля {@link Location#y}
     * @return координату y
     */
    public Long getY(){
        return y;
    }

    /**
     * Функция получения значения поля {@link Location#name}
     * @return название точки
     */
    public String getName(){
        return name;
    }

    /**
     * * Метод для присваивания значения полю {@link Location#y}
     * @param X значение для координаты x
     */
    public void setX(Integer X){
        x = X;
    }

    /**
     * * Метод для присваивания значения полю {@link Location#x}
     * @param Y значение для координаты у
     */
    public void setY(Long Y){
        y = Y;
    }

    /**
     * * Метод для присваивания значения полю {@link Location#name}
     * @param Name название точки
     */
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
