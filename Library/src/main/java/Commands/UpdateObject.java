package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Instruments.ServerResponseCodes;
import Storable.Route;

import java.util.Date;

public class UpdateObject implements ICommand {
    private Route elementToAdd;
    private String user;
    private long id;

    public UpdateObject(long id, Route elementToAdd, String user){
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
                serverResponse = new ServerResponse(ServerResponseCodes.CHANGE_FIELDS_OK);
                serverResponse.setRoute(elementToAdd);
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
