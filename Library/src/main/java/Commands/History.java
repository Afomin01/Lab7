package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;

public class History implements ICommand {
    private String user;

    public History(String user) {
        this.user = user;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.History;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        return new ServerResponse();
    }
}
