package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.stream.Stream;

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
        ServerResponse serverResponse = new ServerResponse();
        Stream<Route> stream = manager.stream();
        long i = stream.filter(r -> r.getDistance() >= distance).count();
        serverResponse.setText("Всего "+i+" элементов");
        return serverResponse;
    }
}
