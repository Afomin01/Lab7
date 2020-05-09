package Commands;
import Instruments.ICollectionManager;
import Instruments.ServerResponseCodes;
import Storable.Route;
import Instruments.ServerResponse;

public class Help implements ICommand {

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Help;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        String out = "Информация по доступным командам:\n";
        for ( EAvailableCommands c : EAvailableCommands.values()) {
            if(!c.equals(EAvailableCommands.Get_Table_Items)) out =out+c.getCommandInfo()+"\n";
        }

        return new ServerResponse(ServerResponseCodes.HELP,out);
    }
}
