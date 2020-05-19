package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Instruments.ServerResponseCodes;
import Storable.Route;

public class RemoveObject implements ICommand {
    private String user;

    public RemoveObject(long id, String user) {
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
                serverResponse = new ServerResponse(ServerResponseCodes.REMOVE_ITEM_BY_ID);
                break;
            case NO_CHANGES:
                serverResponse = new ServerResponse(ServerResponseCodes.CHANGE_FIELDS_NO_RIGHTS);
                break;
            case SQL_ERROR:
            case UNKNOWN_ERROR:
                serverResponse = new ServerResponse(ServerResponseCodes.CHANGE_FIELDS_ERROR);
                break;
        }
        return serverResponse;
    }
}
