package Client.Exceptions;

public class EOFElementCreationException extends Exception {
    public EOFElementCreationException() {
        super("Введен специальный символ. Отмена выполнения команды.");
    }
}
