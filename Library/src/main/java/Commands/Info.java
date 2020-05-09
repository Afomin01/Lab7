package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponseCodes;
import Storable.Route;
import Instruments.ServerResponse;

public class Info implements ICommand {
    private String user;
    
    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Info;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {

        String out="";
        out = out + "LinkedHashSet ";
        out = out + manager.stream().count();
        out = out + " Route";

        return new ServerResponse(ServerResponseCodes.INFO, out);
    }
}
