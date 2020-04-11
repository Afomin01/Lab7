package Commands;

import Instruments.ServerResponse;
import Instruments.XMLParser;
import Storable.Route;

import java.util.LinkedHashSet;
import java.util.Set;

public class Save implements ICommand {
    private String fileName;
    private String user;

    public void setFileName(String fileName, String user) {
        this.fileName = fileName;
        this.user = user;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Save;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        ServerResponse serverResponse = new ServerResponse();
        try {
            XMLParser.unparse((LinkedHashSet<Route>) set, fileName);
            serverResponse.setText("Сохранение успешно");
        }
        catch (Exception e){
            System.out.println("Ошибка сохранения в файл");
            e.printStackTrace();
            serverResponse.setText("При сохранении произошла ошибка");
        }
        return serverResponse;
    }
}

//TODO rework save
//TODO change so that user can save only his elements


