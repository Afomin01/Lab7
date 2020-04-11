package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoveAllByDistance implements ICommand {
    private double distance;
    private String user;

    public RemoveAllByDistance(double distance, String user) {
        this.distance = distance;
        this.user=user;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Remove_All_By_Distance;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        ServerResponse serverResponse = new ServerResponse();
        Stream<Route> stream = set.stream();
        if (set.removeAll(stream.filter(r -> (r.getDistance()==distance && r.getOwner().equals(user))).collect(Collectors.toSet()))) serverResponse.setText("Удалены все элементы значение поля distance которых "+distance);
        else serverResponse.setText("Коллекция не была изменена");
        return serverResponse;
    }
}
