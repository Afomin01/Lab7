package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.Date;

public class UpdateId implements ICommand {
    private Route elementToAdd;
    private String user;
    private long id;

    public UpdateId(long id, Route elementToAdd, String user){
        this.elementToAdd = elementToAdd;
        this.id=id;
        this.user=user;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Update;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {

        elementToAdd.setCreationDate(new Date());
        ServerResponse serverResponse = null;
        switch (manager.update(id,elementToAdd,user)){
            case OK:
                serverResponse = new ServerResponse(ServerRespenseCodes.UPDATE_OK);
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
