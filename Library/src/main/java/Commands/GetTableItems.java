package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.Collections;
import java.util.stream.Collectors;

public class GetTableItems implements ICommand {

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Get_Table_Items;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        ServerResponse serverResponse = new ServerResponse(ServerRespenseCodes.SET_ONLY);
        serverResponse.setSet(manager.stream().collect(Collectors.toList()));
        return serverResponse;
    }
}
