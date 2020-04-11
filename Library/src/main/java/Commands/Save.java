package Commands;

import Instruments.ServerResponse;
import Instruments.XMLParser;
import Storable.Route;

import java.util.LinkedHashSet;
import java.util.Set;

public class Save implements ICommand {
    private String fileName;

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
