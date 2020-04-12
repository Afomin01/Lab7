package Commands;

import Instruments.ICollectionManager;
import Storable.Route;
import Instruments.ServerResponse;

public class Info implements ICommand {
    private String user;
    
    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Info;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        ServerResponse serverResponse = new ServerResponse();

        String out="";
        out = out + "Тип коллекции: "+ manager.getClass().getTypeName();
        out = out + "\nКоличесвто элементов коллекции: "+ manager.getSet().size();

        serverResponse.setText(out);
        return serverResponse;
    }
}
