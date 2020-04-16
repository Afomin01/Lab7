package Commands;
import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Storable.Route;
import Instruments.ServerResponse;

import java.util.stream.Collectors;

public class PrintUniqueDistance implements ICommand {

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Print_Unique_Distance;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        ServerResponse serverResponse = null;
        String text = "";
        int count=0;
        for(Route r: manager.stream().filter(r-> manager.stream().filter(c->c.getDistance()==r.getDistance()).count()==1).collect(Collectors.toSet())){
            text = text + r.getDistance() + "\n";
            count++;
        }
        if (count==0)serverResponse = new ServerResponse(ServerRespenseCodes.SEARCH_NOT_FOUND);
        else serverResponse = new ServerResponse(ServerRespenseCodes.SEARCH_OK,text);
        return serverResponse;
    }
}
