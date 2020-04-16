package Commands;
import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Storable.Route;
import Instruments.ServerResponse;

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
    public ServerResponse execute(ICollectionManager<Route> manager) {
        return new ServerResponse(ServerRespenseCodes.EXIT);
    }
}
