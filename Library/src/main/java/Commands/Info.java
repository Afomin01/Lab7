package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;

public class Info implements ICommand {
    private String user;
    
    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Info;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        ServerResponse serverResponse = new ServerResponse();

        String out="";
        out = out + "Тип коллекции: "+set.getClass().getTypeName();
        out = out + "\nКоличесвто элементов коллекции: "+set.size();

        serverResponse.setText(out);
        return serverResponse;
    }
}
