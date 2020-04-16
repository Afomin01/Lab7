package Commands;
import Instruments.ICollectionManager;
import Storable.Route;
import Instruments.ServerResponse;

//fake class only for authorisation
public class SignUp implements ICommand {
    private String salt;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return null;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        return null;
    }
}
