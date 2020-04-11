package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;

public class Exit implements ICommand {
    private String user;

    public Exit(String user) {
        this.user = user;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Exit;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        return new ServerResponse("Завершение работы. Изменения не сохранены.", true);
    }
}
