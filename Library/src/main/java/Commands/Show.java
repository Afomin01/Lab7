package Commands;

import Instruments.ServerResponse;
import Storable.Coordinates;
import Storable.Route;

import java.util.*;
import java.util.stream.Collectors;

public class Show implements ICommand {

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Show;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        Comparator<Route> compareRoute  = new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                double temp1 = o1.getCoordinates().getx()+o1.getCoordinates().gety();
                double temp2 = o2.getCoordinates().getx()+o2.getCoordinates().gety();
                return (int) (temp1-temp2);
            }
        };
        ServerResponse serverResponse = new ServerResponse("Элементы в коллекции:\n");
        set.stream().sorted(compareRoute).collect(Collectors.toList()).forEach(r->serverResponse.addText("\n"+r.toString()+"\n"));

        return serverResponse;
    }
}