package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;
import java.util.stream.Collectors;

public class PrintUniqueDistance implements ICommand {

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Print_Unique_Distance;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        ServerResponse serverResponse = new ServerResponse();
        String text = "";
        for(Route r: set.stream().filter(r->set.stream().filter(c->c.getDistance()==r.getDistance()).count()==1).collect(Collectors.toSet())){
            text = text + r.getDistance() + "\n";
        }
        if (text.equals(""))serverResponse.setText("Уникальных значений поля distance в коллекции не обнаружено");
        else serverResponse.setText("Уникальные значения поля distance:\n"+text);
        return serverResponse;
    }
}
