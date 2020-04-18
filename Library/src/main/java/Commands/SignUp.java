package Commands;
import Instruments.ICollectionManager;
import Storable.Route;
import Instruments.ServerResponse;

//fake class only for authorisation
public class SignUp implements ICommand {
    private String salt;
    private String email;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
