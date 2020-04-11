package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;

public class Clear implements ICommand {

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Clear;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {

        ServerResponse serverResponse = new ServerResponse();
        if(set.isEmpty()) serverResponse.setText("Коллекция уже пуста");
        else{
            set.clear();
            serverResponse.setText("Коллекция очищена");
        }

        return serverResponse;
    }
}
