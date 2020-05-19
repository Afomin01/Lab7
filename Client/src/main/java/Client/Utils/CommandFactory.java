package Client.Utils;

import Client.Main;
import Commands.*;
import Client.Exceptions.EOFElementCreationException;
import Client.Exceptions.WrongCommandArgumentsException;
import Storable.Coordinates;
import Storable.Location;
import Storable.Route;
import javafx.scene.control.TextArea;

import java.io.Console;
import java.util.Arrays;
import java.util.Date;
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
                    if (type.length == 11){
                        Route adding = new Route(0,type[1],new Coordinates(Double.parseDouble(type[2]),Double.parseDouble(type[3])),new Date(),new Location(Integer.parseInt(type[4]),Long.parseLong(type[5]),type[6]),new Location(Integer.parseInt(type[7]),Long.parseLong(type[8]),type[9]),Double.parseDouble(type[10]), Main.login);
                        if (adding != null) returning = new Add(adding,user);
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Add);
                    break;
                case "add_if_max":
                    if (type.length == 11){
                        Route adding = new Route(0,type[1],new Coordinates(Double.parseDouble(type[2]),Double.parseDouble(type[3])),new Date(),new Location(Integer.parseInt(type[4]),Long.parseLong(type[5]),type[6]),new Location(Integer.parseInt(type[7]),Long.parseLong(type[8]),type[9]),Double.parseDouble(type[10]),Main.login);
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
                            outputInfo(reader,"console.incorrectIn","types.Double");
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
                            outputInfo(reader,"console.incorrectIn","types.Double");
                            dollar(reader);
                        }
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Remove_All_By_Distance);
                    break;
                case "remove_greater":
                    if (type.length == 11){
                        Route adding = new Route(0,type[1],new Coordinates(Double.parseDouble(type[2]),Double.parseDouble(type[3])),new Date(),new Location(Integer.parseInt(type[4]),Long.parseLong(type[5]),type[6]),new Location(Integer.parseInt(type[7]),Long.parseLong(type[8]),type[9]),Double.parseDouble(type[10]),Main.login);
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
                            outputInfo(reader,"console.incorrectIn","types.Integer");
                            dollar(reader);
                        }
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Remove_By_Id);
                    break;
                case "update":
                    if (type.length == 12){
                        try {
                            Route adding = new Route(0,type[2],new Coordinates(Double.parseDouble(type[3]),Double.parseDouble(type[4])),new Date(),new Location(Integer.parseInt(type[5]),Long.parseLong(type[6]),type[7]),new Location(Integer.parseInt(type[8]),Long.parseLong(type[9]),type[10]),Double.parseDouble(type[11]),Main.login);
                            if (adding != null) returning = new UpdateId(Long.parseLong(type[1]), adding,user);
                        }catch (NumberFormatException e){
                            outputInfo(reader,"console.incorrectIn","types.Long");
                            dollar(reader);
                        }
                    }
                    else throw new WrongCommandArgumentsException(EAvailableCommands.Update);
                    break;
                default:
                    reader.setText(reader.getText() + "\n"+ResourceBundle.getBundle("MessagesBundle").getString("console.unknown"));
                    dollar(reader);
                    reader.end();
                    break;
            }
        }catch (WrongCommandArgumentsException e){
            outputInfo(reader,"console.incorrectFormat",e.getMessage());
            dollar(reader);
            returning = null;
        }
        return returning;
    }

    private void outputInfo(TextArea textArea, String key, String info){
        textArea.setText(textArea.getText() + "\n"+ResourceBundle.getBundle("MessagesBundle").getString(key)+" "+ResourceBundle.getBundle("MessagesBundle").getString(info));
        textArea.end();
    }
    private void dollar(TextArea textArea){
    }
}
