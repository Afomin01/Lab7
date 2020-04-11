package Instruments;

import Commands.ICommand;

import java.io.Serializable;

public class ClientRequest implements Serializable {
    private ICommand command;
    private String login;
    private String password;

    public ClientRequest(ICommand command, String login, String password) {
        this.command = command;
        this.login = login;
        this.password = password;
    }

    public ICommand getCommand() {
        return command;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
