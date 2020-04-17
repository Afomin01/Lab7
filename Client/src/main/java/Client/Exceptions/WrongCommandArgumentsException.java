package Client.Exceptions;

import Commands.EAvailableCommands;

public class WrongCommandArgumentsException extends Exception{

    public WrongCommandArgumentsException(EAvailableCommands command) {
        super("Неверный формат команды " + command.toString()+". "+ command.getArguments());
    }
}
