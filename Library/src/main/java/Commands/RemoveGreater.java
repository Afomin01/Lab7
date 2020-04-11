package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoveGreater implements ICommand {
    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    private Route route;

    public RemoveGreater(Route route) {
        this.route = route;
    }


    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Remove_Greater;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        ServerResponse serverResponse = new ServerResponse();
        Stream<Route> stream = set.stream();
        long count = set.stream().filter(r -> r.compareTo(route) > 0).count();
        if (set.removeAll(stream.filter(r -> r.compareTo(route) > 0).collect(Collectors.toSet()))) serverResponse.setText("Из коллекции удалено "+count+" элементов");
        else serverResponse.setText("Коллекция не изменилась");
        return serverResponse;
    }
}
