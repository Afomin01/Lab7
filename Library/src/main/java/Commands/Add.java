package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.Date;

public class Add implements ICommand {
    private Route elementToAdd;
    private String owner;

    public Add(Route elementToAdd,String owner){
        this.elementToAdd = elementToAdd;
        this.owner=owner;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Add;
    }


    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {

        elementToAdd.setCreationDate(new Date());

        ServerResponse serverResponse = null;
        switch (manager.add(elementToAdd)){
            case OK:
                serverResponse = new ServerResponse(ServerResponseCodes.ADD_OK);
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
