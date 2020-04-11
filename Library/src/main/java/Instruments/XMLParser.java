package Instruments;

import Storable.Coordinates;
import Storable.Location;
import Storable.Route;

import javax.management.modelmbean.XMLParseException;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * класс для парсинга и сохранения в файл, имеет два метода {@link XMLParser#parse(String)} осуществляющий парсинг и {@link XMLParser#unparse(LinkedHashSet, String)} осуществляющий сохранение в файл
 */
public class XMLParser {

    /**
     * Метод, осуществляющий парсинг XML документа в коллекциию
     * @param path передает абсолютный путь к файлу
     * @return коллекция полученная из файла
     */
    public static LinkedHashSet<Route> parse(String path) throws XMLParseException {
        LinkedHashSet<Route> routes = new LinkedHashSet<>();
        try {
            Scanner file = new Scanner(new File(path));
            Pattern pattern = Pattern.compile("[>].*[<]");
            int Flag = 0;
            while (file.hasNextLine()) {
                Route el = new Route();
                Coordinates cor = new Coordinates();
                Location from = new Location();
                Location to = new Location();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                while (Flag != 12) {
                    String newS;
                    String afterS;
                    String s = file.findInLine(pattern);
                    if (s != null) {

                        newS = s.substring(0, s.length() - 1);
                        afterS = newS.substring(1);

                        if (Flag == 0) {
                            el.setId(Long.parseLong(afterS));
                        } else if (Flag == 1) {
                            el.setName(afterS);
                        } else if (Flag == 2) {
                            cor.setx(Double.parseDouble(afterS));
                        } else if (Flag == 3) {
                            cor.sety(Double.parseDouble(afterS));
                        } else if (Flag == 4) {
                            el.setCreationDate(dateFormat.parse(afterS));
                        } else if (Flag == 5) {
                            from.setX(Integer.parseInt(afterS));
                        } else if (Flag == 6) {
                            from.setY(Long.parseLong(afterS));
                        } else if (Flag == 7) {
                            from.setName(afterS);
                        } else if (Flag == 8) {
                            to.setX(Integer.parseInt(afterS));
                        } else if (Flag == 9) {
                            to.setY(Long.parseLong(afterS));
                        } else if (Flag == 10) {
                            to.setName(afterS);
                        } else {
                            el.setDistance(Double.parseDouble(afterS));
                        }
                        Flag++;
                    }
                    if (file.hasNextLine()) {
                        file.nextLine();
                    } else {
                        break;
                    }
                }
                if (file.hasNextLine()) {
                    Flag = 0;
                    el.setCoordinates(cor);
                    el.setFrom(from);
                    el.setTo(to);
                    routes.add(el);
                } else {
                    break;
                }

            }
            file.close();
            return routes;
        }
        catch (Exception e) {
            throw new XMLParseException();
        }
    }

    /**
     * Метод, осуществляющий сохранение коллекции в XML файл
     * @param path передает абсолютный путь к файлу
     * @param routes коллекция для сохранения
     */
    public static void unparse(LinkedHashSet<Route> routes, String path) throws Exception {
        OutputStream os = new FileOutputStream(path);
        Writer osr = new OutputStreamWriter(os);
        osr.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        osr.write("<root>\n");
        for (Route r : routes) {
            Coordinates cor;
            Location from;
            Location to;
            cor = r.getCoordinates();
            from = r.getFrom();
            to = r.getTo();

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            osr.write("\t<Route>\n");
            osr.write("\t<id>" + r.getId() + "</id>\n");
            osr.write("\t\t<name>" + r.getName() + "</name>\n");
            osr.write("\t\t<coordinates>\n");
            osr.write("\t\t\t<x>" + cor.getx() + "</x>\n");
            osr.write("\t\t\t<y>" + cor.gety() + "</y>\n");
            osr.write("\t\t</coordinates>\n");
            osr.write("\t\t<creationDate>" + df.format(r.getCreationDate()) + "</creationDate>\n");
            osr.write("\t\t<from>\n");
            osr.write("\t\t\t<x>" + from.getX() + "</x>\n");
            osr.write("\t\t\t<y>" + from.getY() + "</y>\n");
            osr.write("\t\t\t<name>" + from.getName() + "</name>\n");
            osr.write("\t\t</from>\n");
            osr.write("\t\t<to>\n");
            osr.write("\t\t\t<x>" + to.getX() + "</x>\n");
            osr.write("\t\t\t<y>" + to.getY() + "</y>\n");
            osr.write("\t\t\t<name>" + to.getName() + "</name>\n");
            osr.write("\t\t</to>\n");
            osr.write("\t\t<distance>" + r.getDistance() + "</distance>\n");
            osr.write("\t</Route>\n");
        }
        osr.write("</root>");
        osr.close();
    }
}


