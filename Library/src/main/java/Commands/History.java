package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;

public class History implements ICommand {

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.History;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        ServerResponse serverResponse = new ServerResponse();
        return serverResponse;
    }
}
