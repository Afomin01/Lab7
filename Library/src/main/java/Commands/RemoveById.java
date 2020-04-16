package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.stream.Stream;

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
                serverResponse = new ServerResponse(ServerRespenseCodes.DELETE_OK);
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
