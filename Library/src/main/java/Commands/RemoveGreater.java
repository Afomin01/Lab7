package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoveGreater implements ICommand {
    private String user;

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    private Route route;

    public RemoveGreater(Route route, String user) {
        this.route = route;
        this.user=user;
    }


    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Remove_Greater;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        ServerResponse serverResponse = new ServerResponse();
        Stream<Route> stream = set.stream();
        Set<Route> temp = stream.filter(r -> (r.compareTo(route) > 0 && r.getOwner().equals(user))).collect(Collectors.toSet());

        if (set.removeAll(temp)) serverResponse.setText("Из коллекции удалено "+temp.size()+" элементов");
        else serverResponse.setText("Коллекция не изменилась");
        return serverResponse;
    }
}
