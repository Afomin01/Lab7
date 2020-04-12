package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.stream.Collectors;

public class Clear implements ICommand {
    private String user;

    public Clear(String user) {
        this.user = user;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Clear;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {

        ServerResponse serverResponse = new ServerResponse();
        if(manager.removeAll(manager.stream().filter(r-> r.getOwner().equals(user)).collect(Collectors.toSet()))) serverResponse.setText("Все Ваши элементы удалены из коллекции");
        else serverResponse.setText("В коллекции не обнаружено элементов, пренадлежащих Вам.");

        return serverResponse;
    }
}
