package Commands;
import Instruments.ICollectionManager;
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
            out =out+c.getCommandInfo()+"\n";
        }

        return new ServerResponse(out);
    }
}
