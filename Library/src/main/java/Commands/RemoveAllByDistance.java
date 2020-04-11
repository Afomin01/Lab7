package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoveAllByDistance implements ICommand {

    private double distance;
    public RemoveAllByDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Remove_All_By_Distance;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        ServerResponse serverResponse = new ServerResponse();
        Stream<Route> stream = set.stream();
        if (set.removeAll(stream.filter(r -> r.getDistance()==distance).collect(Collectors.toSet()))) serverResponse.setText("Удалены все элементы значение поля distance которых "+distance);
        else if (set.stream().noneMatch(r -> r.getDistance() == distance)) serverResponse.setText("Коллекция не изменилась так как в ней не обнаружено элементов со значением поля distance "+distance);
        else serverResponse.setText("Коллекция не была изменена");
        return serverResponse;
    }
}
