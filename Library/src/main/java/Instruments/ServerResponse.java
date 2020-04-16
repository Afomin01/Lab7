package Instruments;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    private String additionalInfo = "";
    private ServerRespenseCodes code;
    private boolean access = false;

    public ServerResponse(ServerRespenseCodes code) {
        this.code = code;
    }

    public ServerResponse(ServerRespenseCodes code, String additionalInfo) {
        this.additionalInfo = additionalInfo;
        this.code = code;
    }

    public boolean isAccess() {
        return access;
    }
    public void setAccess(boolean access) {
        this.access = access;
    }
    public void setCode(ServerRespenseCodes code) {
        this.code = code;
    }
    public ServerRespenseCodes getCode() {
        return code;
    }
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    public void addText(String text){
        this.additionalInfo = this.additionalInfo + "\n" + text;
    }
}
