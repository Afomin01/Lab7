package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerRespenseCodes;
import Storable.Route;
import Instruments.ServerResponse;

public class Info implements ICommand {
    private String user;
    
    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Info;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {

        String out="";
        out = out + "Тип коллекции: LinkedHashSet";
        out = out + "\nКоличесвто элементов коллекции: "+ manager.stream().count();
        out = out + "\nТип хранимых элементов: Routes";

        return new ServerResponse(ServerRespenseCodes.TEXT_ONLY, out);
    }
}
