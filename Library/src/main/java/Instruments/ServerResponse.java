package Instruments;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    private String text="";
    private boolean shutdown = false;
    private boolean access = true;

    public ServerResponse(String text) {
        this.text = text;
    }

    public ServerResponse(String text, boolean shutdown) {
        this.text = text;
        this.shutdown = shutdown;
    }

    public ServerResponse() {

    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addText(String text){
        this.text = this.text + "\n" + text;
    }
}
