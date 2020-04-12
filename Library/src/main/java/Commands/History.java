package Commands;
import Instruments.ICollectionManager;
import Storable.Route;
import Instruments.ServerResponse;

public class History implements ICommand {
    private String user;

    public History(String user) {
        this.user = user;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.History;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        return new ServerResponse();
    }
}
