package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;
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
    public ServerResponse execute(Set<Route> set) {

        ServerResponse serverResponse = new ServerResponse();
        if(set.isEmpty()) serverResponse.setText("Коллекция уже пуста");
        else{
            set.removeAll(set.stream().filter(r-> r.getOwner().equals(user)).collect(Collectors.toSet()));
            serverResponse.setText("Все Ваши элементы удалены из коллекции");
        }

        return serverResponse;
    }
}
