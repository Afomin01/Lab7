package Exceptions;

public class EOFCommandGetException extends Exception {
    public EOFCommandGetException() {
        super("Введен специальный символ. Завершение работы...");
    }
}
