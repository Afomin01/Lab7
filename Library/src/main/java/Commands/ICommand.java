package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Storable.Route;

import java.io.Serializable;


public interface ICommand extends Serializable {
    EAvailableCommands getCommandEnum();
    ServerResponse execute(ICollectionManager<Route> manager);
}