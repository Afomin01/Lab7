package Client.Exceptions;

import Commands.EAvailableCommands;

public class WrongCommandArgumentsException extends Exception{

    public WrongCommandArgumentsException(EAvailableCommands command) {
        super(command.toString()+". "+ command.getArguments());
    }
}
