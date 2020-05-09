package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.stream.Collectors;

public class GetTableItems implements ICommand {

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Get_Table_Items;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        ServerResponse serverResponse = new ServerResponse(ServerResponseCodes.SET_ONLY);
        serverResponse.setSet(manager.stream().collect(Collectors.toList()));
        return serverResponse;
    }
}
