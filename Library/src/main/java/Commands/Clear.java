package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.stream.Collectors;

public class Clear implements ICommand {
    private String user;

    public Clear(String user) {
        this.user = user;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Clear;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {

        ServerResponse serverResponse = null;
        switch (manager.removeAll(manager.stream().filter(r-> r.getOwner().equals(user)).collect(Collectors.toSet()),user)) {
            case OK:
                serverResponse = new ServerResponse(ServerRespenseCodes.CLEAR_OK);
                break;
            case NO_CHANGES:
                serverResponse = new ServerResponse(ServerRespenseCodes.NO_CHANGES);
                break;
            case SQL_ERROR:
                serverResponse = new ServerResponse(ServerRespenseCodes.SQL_ERROR);
                break;
            case UNKNOWN_ERROR:
                serverResponse = new ServerResponse(ServerRespenseCodes.ERROR);
                break;
        }
        return serverResponse;
    }
}
