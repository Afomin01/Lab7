package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;

public class CountGreaterThanDistance implements ICommand {

    public CountGreaterThanDistance(double distance) {
        this.distance = distance;
    }

    private double distance;

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Count_Greater_Than_Distance;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        long i = manager.stream().filter(r -> r.getDistance() >= distance).count();
        return new ServerResponse(ServerRespenseCodes.SEARCH_OK, String.valueOf(i));
    }
}
