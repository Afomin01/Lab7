package Instruments;

import Storable.Route;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServerResponse implements Serializable {
    private String additionalInfo = "";
    private ServerRespenseCodes code;
    private ArrayList<ServerResponse> scriptReport = new ArrayList<>();
    private boolean access = false;
    private List<Route> collectionSorted;

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

    public ArrayList<ServerResponse> getScriptReport() {
        return scriptReport;
    }

    public void setScriptReport(ArrayList<ServerResponse> scriptReport) {
        this.scriptReport = scriptReport;
    }


}
