import java.util.ArrayList;

public class HistoryHandler {
    private String login;
    private ArrayList<String> history = new ArrayList<>();

    public HistoryHandler(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
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

}
