package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;

public class Help implements ICommand {

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Help;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        String out = "Информация по доступным командам:\n";
        for ( EAvailableCommands c : EAvailableCommands.values()) {
            out =out+c.getCommandInfo()+"\n";
        }

        return new ServerResponse(out);
    }
}
