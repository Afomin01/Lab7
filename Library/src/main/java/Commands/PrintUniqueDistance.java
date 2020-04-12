package Commands;
import Instruments.ICollectionManager;
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
        ServerResponse serverResponse = new ServerResponse();
        String text = "";
        for(Route r: manager.stream().filter(r-> manager.stream().filter(c->c.getDistance()==r.getDistance()).count()==1).collect(Collectors.toSet())){
            text = text + r.getDistance() + "\n";
        }
        if (text.equals(""))serverResponse.setText("Уникальных значений поля distance в коллекции не обнаружено");
        else serverResponse.setText("Уникальные значения поля distance:\n"+text);
        return serverResponse;
    }
}
