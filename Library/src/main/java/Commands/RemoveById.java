package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponseCodes;
import Instruments.ServerResponse;
import Storable.Route;

public class RemoveById implements ICommand {
    private String user;

    public RemoveById(long id, String user) {
        this.id = id;
        this.user=user;
    }

    private long id;

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Remove_By_Id;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        ServerResponse serverResponse = null;

        switch (manager.removeIf(r->r.getId()==id,user)){
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
