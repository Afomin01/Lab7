public class SocketHandlerException extends Exception {
    SocketHandlerException(){
        super("Socket was shutted down");
    }
}
