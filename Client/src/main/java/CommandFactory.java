import Commands.*;
import Exceptions.EOFElementCreationException;
import Exceptions.WrongCommandArgumentsException;
import Storable.Coordinates;
import Storable.Location;
import Storable.Route;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;

public class CommandFactory {
    private String username;
    public ICommand getCommand(String[] type, Console reader, String user){
        ICommand returning = null;
        username=user;
        try {
            switch (type[0]) {
                case "":
                    return null;
                case "add":
                    if (type.length == 1){
                        Route adding = elementCreator(reader);
                        if (adding != null) returning = new Add(adding);
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Add);
                    break;
                case "add_if_max":
                    if (type.length == 1){
                        Route adding = elementCreator(reader);
                        if (adding != null) returning = new AddIfMax(adding);
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Add_If_Max);
                    break;
                case "clear":
                    if (type.length == 1) returning = new Clear(user);
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Clear);
                    break;
                case "count_greater_than_distance":
                    if (type.length == 2){
                        try {
                            returning = new CountGreaterThanDistance(Double.parseDouble(type[1]));
                        }catch (NumberFormatException e){
                            System.out.println("Некорректный ввод числового значения. Необходим double");
                        }
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Count_Greater_Than_Distance);
                    break;
                case "history":
                    if (type.length == 1) returning = new History(user);
                    else throw new WrongCommandArgumentsException(EAvailableCommands.History);
                    break;
                case "execute_script":
                    if (type.length == 2) returning = new ExecuteScript(type[1],user);
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Execute_Script);
                    break;
                case "exit":
                    if (type.length == 1) returning = new Exit(user);
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Exit);
                    break;
                case "help":
                    if (type.length == 1) returning = new Help();
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Help);
                    break;
                case "info":
                    if (type.length == 1) returning = new Info();
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Info);
                    break;
                case "print_unique_distance":
                    if (type.length == 1) returning = new PrintUniqueDistance();
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Print_Unique_Distance);
                    break;
                case "remove_all_by_distance":
                    if (type.length == 2) {
                        try {
                            returning = new RemoveAllByDistance(Double.parseDouble(type[1]),user);
                        }catch (NumberFormatException e){
                            System.out.println("Некорректный ввод числового значения. Необходим double");
                        }
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Remove_All_By_Distance);
                    break;
                case "remove_greater":
                    if (type.length == 1){
                        Route adding = elementCreator(reader);
                        if (adding != null) returning = new RemoveGreater(adding,user);
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Remove_Greater);
                    break;
                case "show":
                    if (type.length == 1) returning = new Show();
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Show);
                    break;
                case "remove_by_id":
                    if (type.length == 2){
                        try {
                            returning = new RemoveById(Long.parseLong(type[1]),user);
                        }catch (NumberFormatException e){
                            System.out.println("Некорректный ввод числового значения. Необходим int");
                        }
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Remove_By_Id);
                    break;
                case "update":
                    if (type.length == 2){
                        try {
                            Route adding = elementCreator(reader);
                            if (adding != null) returning = new UpdateId(Long.parseLong(type[1]), adding,user);
                        }catch (NumberFormatException e){
                            System.out.println("Некорректный ввод числового значения. Необходим long");
                        }
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Update);
                    break;
                default:
                    Main.outputInfo("Неопознанная команда \"" + type[0] + "\". Для вывода справки введите help");
                    break;
            }
        }catch (WrongCommandArgumentsException e){
            Main.outputInfo(e.getMessage());
            returning = null;
        }
        return returning;
    }

    private Route elementCreator(Console reader){
        try {
            Route adding = new Route(username);
            Coordinates coord = new Coordinates();
            Location Lfrom = new Location();
            Location Lto = new Location();

            Main.outputInfo("Введите значение поля name (String)");
            String temp = reader.readLine();
            if(temp == null) throw new EOFElementCreationException();

            while (temp.isEmpty() || temp.matches("[\\s]*")) {
                Main.outputInfo("Данное поле не может быть пустым");
                temp = reader.readLine();
                if(temp == null) throw new EOFElementCreationException();
            }

            adding.setName(temp);

            Main.outputInfo("Введите значение  coordinates: x (double)");
            while (true) {
                try {
                    temp = reader.readLine();
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        Main.outputInfo("Данное поле не может быть пустым");
                        temp = reader.readLine();
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    coord.setx(Double.parseDouble(temp));
                    break;
                } catch (NumberFormatException e) {
                    Main.outputInfo("Некорректный ввод. Введите double");
                }
            }

            Main.outputInfo("Введите значение поля coordinates: y (Double)");
            while (true) {
                try {
                    temp = reader.readLine();
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        Main.outputInfo("Данное поле не может быть пустым");
                        temp = reader.readLine();
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    while (Double.parseDouble(temp) <= -462) {
                        Main.outputInfo("Значение поля должно быть больше -462");
                        temp = reader.readLine();
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    coord.sety(Double.parseDouble(temp));
                    adding.setCoordinates(coord);
                    break;
                } catch (NumberFormatException e) {
                    Main.outputInfo("Некорректный ввод. Введите Double");
                }
            }

            Main.outputInfo("Введите значение поля from: x (Integer)");
            while (true) {
                try {
                    temp = reader.readLine();
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        Main.outputInfo("Данное поле не может быть пустым");
                        temp = reader.readLine();
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    Lfrom.setX(Integer.parseInt(temp));
                    break;
                } catch (NumberFormatException e) {
                    Main.outputInfo("Некорректный ввод. Введите Integer");
                }
            }

            Main.outputInfo("Введите значение поля from: y (Long)");
            while (true) {
                try {
                    temp = reader.readLine();
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        Main.outputInfo("Данное поле не может быть пустым");
                        temp = reader.readLine();
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    Lfrom.setY(Long.parseLong(temp));
                    break;
                } catch (NumberFormatException e) {
                    Main.outputInfo("Некорректный ввод. Введите Long");
                }
            }

            Main.outputInfo("Введите значение поля from: name (String)");
            temp = reader.readLine();
            if(temp == null) throw new EOFElementCreationException();
            while (temp.isEmpty() || temp.matches("[\\s]*")) {
                Main.outputInfo("Данное поле не может быть пустым");
                temp = reader.readLine();
                if(temp == null) throw new EOFElementCreationException();
            }
            Lfrom.setName(temp);
            adding.setFrom(Lfrom);

            Main.outputInfo("Введите значение поля to: x (Integer)");
            while (true) {
                try {
                    temp = reader.readLine();
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        Main.outputInfo("Данное поле не может быть пустым");
                        temp = reader.readLine();
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    Lto.setX(Integer.parseInt(temp));
                    break;
                } catch (NumberFormatException e) {
                    Main.outputInfo("Некорректный ввод. Введите Integer");
                }
            }

            Main.outputInfo("Введите значение поля to: y (Long)");
            while (true) {
                try {
                    temp = reader.readLine();
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        Main.outputInfo("Данное поле не может быть пустым");
                        temp = reader.readLine();
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    Lto.setY(Long.parseLong(temp));
                    break;
                } catch (NumberFormatException e) {
                    Main.outputInfo("Некорректный ввод. Введите Long");
                }
            }

            Main.outputInfo("Введите значение поля to: name (String)");
            temp = reader.readLine();
            if(temp == null) throw new EOFElementCreationException();
            while (temp.isEmpty() || temp.matches("[\\s]*")) {
                Main.outputInfo("Данное поле не может быть пустым");
                temp = reader.readLine();
                if(temp == null) throw new EOFElementCreationException();
            }
            Lto.setName(temp);
            adding.setTo(Lto);

            Main.outputInfo("Введите значение поля distance (double)");
            while (true) {
                try {
                    temp = reader.readLine();
                    if(temp == null) throw new EOFElementCreationException();
                    while (Double.parseDouble(temp) <= 1) {
                        Main.outputInfo("Данное поле должно быть больше 1");
                        temp = reader.readLine();
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    adding.setDistance(Double.parseDouble(temp));
                    break;
                } catch (NumberFormatException e) {
                    Main.outputInfo("Некорректный ввод. Введите double");
                }
            }
            return adding;
        } catch (EOFElementCreationException e) {
            Main.outputInfo(e.getMessage());
            return null;
        }
    }
}
