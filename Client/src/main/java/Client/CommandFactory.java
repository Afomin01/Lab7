package Client;

import Commands.*;
import Client.Exceptions.EOFElementCreationException;
import Client.Exceptions.WrongCommandArgumentsException;
import Storable.Coordinates;
import Storable.Location;
import Storable.Route;
import javafx.scene.control.TextArea;

import java.io.Console;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class CommandFactory {
    private String username;
    public ICommand getCommand(String[] type, TextArea reader, String user){
        ICommand returning = null;
        username=user;
        try {
            switch (type[0]) {
                case "":
                    return null;
                case "add":
                    if (type.length == 1){
                        Route adding = elementCreator(reader);
                        if (adding != null) returning = new Add(adding,user);
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Add);
                    break;
                case "add_if_max":
                    if (type.length == 1){
                        Route adding = elementCreator(reader);
                        if (adding != null) returning = new AddIfMax(adding,user);
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
                            outputInfo(reader,"console.incorrectIn","double");
                            dollar(reader);
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
                            outputInfo(reader,"console.incorrectIn","double");
                            dollar(reader);
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
                            outputInfo(reader,"console.incorrectIn","int");
                            dollar(reader);
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
                            outputInfo(reader,"console.incorrectIn","long");
                            dollar(reader);
                        }
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Update);
                    break;
                default:
                    outputInfo(reader,"console.unknown","");
                    dollar(reader);;
                    break;
            }
        }catch (WrongCommandArgumentsException e){
            outputInfo(reader,"console.incorrectFormat",e.getMessage());
            dollar(reader);
            returning = null;
        }
        return returning;
    }

    private String readLine(TextArea reader){
        int y = reader.getText().split("\n").length;
        return Arrays.asList(reader.getText().split("\n")).get(y-1);
    }
    private void outputInfo(TextArea textArea, String key, String info){
        textArea.setText(textArea.getText() + "\n"+ResourceBundle.getBundle("MessagesBundle").getString(key)+" "+info);
        textArea.end();
    }
    private void outputEnterInfo(TextArea textArea, String key, String type){
        textArea.setText(textArea.getText() + "\n"+ResourceBundle.getBundle("MessagesBundle").getString("console.enterField")+ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()).getString(key)+" ("+type+")");
        textArea.end();
    }
    private void dollar(TextArea textArea){
        textArea.setText(textArea.getText() + "$");
        textArea.end();
    }
    private Route elementCreator(TextArea reader){
        try {
            Route adding = new Route(username);
            Coordinates coord = new Coordinates();
            Location Lfrom = new Location();
            Location Lto = new Location();

            outputEnterInfo(reader,"table.name","String");
            dollar(reader);
            String temp = readLine(reader);
            if(temp == null) throw new EOFElementCreationException();

            while (temp.isEmpty() || temp.matches("[\\s]*")) {
                outputInfo(reader,"console.notEmpty","");
                dollar(reader);
                temp = readLine(reader);
                if(temp == null) throw new EOFElementCreationException();
            }

            adding.setName(temp);

            outputEnterInfo(reader,"console.coordX","double");
            dollar(reader);
            while (true) {
                try {
                    temp = readLine(reader);
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        outputInfo(reader,"console.notEmpty","");
                        dollar(reader);
                        temp = readLine(reader);
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    coord.setx(Double.parseDouble(temp));
                    break;
                } catch (NumberFormatException e) {
                    outputInfo(reader,"console.incorrectIn","double");
                    dollar(reader);
                }
            }

            outputEnterInfo(reader,"console.coordY","Double");
            dollar(reader);
            while (true) {
                try {
                    temp = readLine(reader);
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        outputInfo(reader,"console.notEmpty","");
                        dollar(reader);
                        temp = readLine(reader);;
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    while (Double.parseDouble(temp) <= -462) {
                        outputInfo(reader,"console.higher","-462");
                        dollar(reader);
                        temp = readLine(reader);
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    coord.sety(Double.parseDouble(temp));
                    adding.setCoordinates(coord);
                    break;
                } catch (NumberFormatException e) {
                    outputInfo(reader,"console.incorrectIn","Double");
                    dollar(reader);
                }
            }

            outputEnterInfo(reader,"console.fromX","Integer");
            dollar(reader);
            while (true) {
                try {
                    temp = readLine(reader);
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        outputInfo(reader,"console.notEmpty","");
                        dollar(reader);
                        temp = readLine(reader);
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    Lfrom.setX(Integer.parseInt(temp));
                    break;
                } catch (NumberFormatException e) {
                    outputInfo(reader,"console.incorrectIn","Integer");
                    dollar(reader);
                }
            }

            outputEnterInfo(reader,"console.fromY","Long");
            dollar(reader);
            while (true) {
                try {
                    temp = readLine(reader);
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        outputInfo(reader,"console.notEmpty","");
                        dollar(reader);
                        temp = readLine(reader);
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    Lfrom.setY(Long.parseLong(temp));
                    break;
                } catch (NumberFormatException e) {
                    outputInfo(reader,"console.incorrectIn","Long");
                    dollar(reader);
                }
            }

            outputEnterInfo(reader,"console.fromName","String");
            dollar(reader);
            temp = readLine(reader);
            if(temp == null) throw new EOFElementCreationException();
            while (temp.isEmpty() || temp.matches("[\\s]*")) {
                outputInfo(reader,"console.notEmpty","");
                dollar(reader);
                temp = readLine(reader);
                if(temp == null) throw new EOFElementCreationException();
            }
            Lfrom.setName(temp);
            adding.setFrom(Lfrom);

            outputEnterInfo(reader,"console.toX","Integer");
            dollar(reader);
            while (true) {
                try {
                    temp = readLine(reader);
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        outputInfo(reader,"console.notEmpty","");
                        dollar(reader);
                        temp = readLine(reader);
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    Lto.setX(Integer.parseInt(temp));
                    break;
                } catch (NumberFormatException e) {
                    outputInfo(reader,"console.incorrectIn","Integer");
                    dollar(reader);
                }
            }

            outputEnterInfo(reader,"console.toY","Long");
            dollar(reader);
            while (true) {
                try {
                    temp = readLine(reader);
                    if(temp == null) throw new EOFElementCreationException();
                    while (temp.isEmpty()) {
                        outputInfo(reader,"console.notEmpty","");
                        dollar(reader);
                        temp = readLine(reader);
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    Lto.setY(Long.parseLong(temp));
                    break;
                } catch (NumberFormatException e) {
                    outputInfo(reader,"console.incorrectIn","Long");
                    dollar(reader);
                }
            }

            outputEnterInfo(reader,"console.toName","String");
            dollar(reader);
            temp = readLine(reader);
            if(temp == null) throw new EOFElementCreationException();
            while (temp.isEmpty() || temp.matches("[\\s]*")) {
                outputInfo(reader,"console.notEmpty","");
                dollar(reader);
                temp = readLine(reader);
                if(temp == null) throw new EOFElementCreationException();
            }
            Lto.setName(temp);
            adding.setTo(Lto);

            outputEnterInfo(reader,"table.distance","double");
            dollar(reader);
            while (true) {
                try {
                    temp = readLine(reader);
                    if(temp == null) throw new EOFElementCreationException();
                    while (Double.parseDouble(temp) <= 1) {
                        outputInfo(reader,"console.higher","1");
                        dollar(reader);
                        temp = readLine(reader);
                        if(temp == null) throw new EOFElementCreationException();
                    }
                    adding.setDistance(Double.parseDouble(temp));
                    break;
                } catch (NumberFormatException e) {
                    outputInfo(reader,"console.incorrectIn","double");
                    dollar(reader);
                }
            }
            return adding;
        } catch (EOFElementCreationException e) {
/*            outputInfo(reader,e.getMessage());
            Main.dollar();*/
            return null;
        }
    }
}
