package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.Date;

public class Add implements ICommand {
    private Route elementToAdd;

    public Add(Route elementToAdd){
        this.elementToAdd = elementToAdd;
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
                serverResponse = new ServerResponse(ServerRespenseCodes.ADD_OK);
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
