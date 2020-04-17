package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.Date;

public class AddIfMax implements ICommand {

    private Route elementToAdd;
    String owner;

    public AddIfMax(Route elementToAdd, String owner){
        this.elementToAdd = elementToAdd;
        this.owner=owner;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Add_If_Max;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {

        elementToAdd.setCreationDate(new Date());

        ServerResponse serverResponse = null;
        if(elementToAdd.compareTo(manager.stream().max(Route::compareTo).get()) > 0){
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
        }
        else serverResponse = new ServerResponse(ServerRespenseCodes.NO_CHANGES);

        return serverResponse;
    }
}
