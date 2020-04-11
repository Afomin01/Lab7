package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.io.Serializable;
import java.util.Set;

public interface ICommand extends Serializable {
    EAvailableCommands getCommandEnum();
    ServerResponse execute(Set<Route> set);
}