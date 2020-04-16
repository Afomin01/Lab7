
import Instruments.ServerRespenseCodes;
import Instruments.ServerResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class SocketConnected {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private ArrayList<String> history = new ArrayList<>();
    private String login="%";
    private boolean alive = true;
    private long id;

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login){
        if(login.equals("%")) this.login=login;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public long getId() {
        return id;
    }

    public void addHistory(String name){
        if(history.size()<7) history.add(name);
        else {
            for (int i=1;i<6;i++) history.set(i, history.get(i + 1));
            history.set(6,name);
        }
    }

    public String getHistory(){
        String out = "Последние введенные команды:\n";
        for (String s : history) out = out + s + "\n";
        return out;
    }

    public SocketConnected(Socket socket, long id) throws SocketHandlerException {
        this.socket = socket;
        //id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.id=id;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e){
            Main.log.severe("An error occurred while creating the output stream.");
            throw new SocketHandlerException();
        }
    }
    public void delete(){
        try {
            outputStream.close();
            inputStream.close();
        }catch (IOException ignored){}
    }
}
