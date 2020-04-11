package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;
//fake class only for authorisation
public class SignUp implements ICommand {
    @Override
    public EAvailableCommands getCommandEnum() {
        return null;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        return null;
    }
}
