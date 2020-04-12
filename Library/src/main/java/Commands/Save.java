package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Instruments.XMLParser;
import Storable.Route;

import java.util.LinkedHashSet;

//legacy class as it supposed to write the collection into file and we do not have any file now

public class Save implements ICommand {
    private String fileName;
    private String user;

    public void setFileName(String fileName, String user) {
        this.fileName = fileName;
        this.user = user;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Show;
    }/////////////////////////////////

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        ServerResponse serverResponse = new ServerResponse();
        try {
            XMLParser.unparse((LinkedHashSet<Route>) manager, fileName);
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



