package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.stream.Collectors;

public class RemoveGreater implements ICommand {
    private String user;

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    private Route route;

    public RemoveGreater(Route route, String user) {
        this.route = route;
        this.user=user;
    }


    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Remove_Greater;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        ServerResponse serverResponse = null;

        switch (manager.removeAll(manager.stream().filter(r -> (r.compareTo(route) > 0 && r.getOwner().equals(user))).collect(Collectors.toSet()), user)){
            case OK:
                serverResponse = new ServerResponse(ServerResponseCodes.DELETE_OK);
                break;
            case NO_CHANGES:
                serverResponse = new ServerResponse(ServerResponseCodes.NO_CHANGES);
                break;
            case SQL_ERROR:
                serverResponse = new ServerResponse(ServerResponseCodes.SQL_ERROR);
                break;
            case UNKNOWN_ERROR:
                serverResponse = new ServerResponse(ServerResponseCodes.ERROR);
                break;
        }
        return serverResponse;
    }
}
