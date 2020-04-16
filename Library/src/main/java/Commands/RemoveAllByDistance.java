package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoveAllByDistance implements ICommand {
    private double distance;
    private String user;

    public RemoveAllByDistance(double distance, String user) {
        this.distance = distance;
        this.user=user;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Remove_All_By_Distance;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        ServerResponse serverResponse = null;

        switch (manager.removeAll(manager.stream().filter(r -> (r.getDistance()==distance && r.getOwner().equals(user))).collect(Collectors.toSet()),user)){
            case OK:
                serverResponse = new ServerResponse(ServerRespenseCodes.DELETE_OK);
                break;
            case NO_CHANGES:
                serverResponse = new ServerResponse(ServerRespenseCodes.NO_CHANGES);
                break;
            case SQL_ERROR:
                serverResponse = new ServerResponse(ServerRespenseCodes.SQL_ERROR);
                break;
            case UNKNOWN_ERROR:
                serverResponse = new ServerResponse(ServerRespenseCodes.ERROR);
                break;
        }
        return serverResponse;
    }
}
