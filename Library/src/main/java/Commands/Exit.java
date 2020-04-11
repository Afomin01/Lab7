package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;

public class Exit implements ICommand {

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Exit;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        ServerResponse serverResponse = new ServerResponse("Завершение работы. Изменения не сохранены.", true);
        return serverResponse;
    }
}
