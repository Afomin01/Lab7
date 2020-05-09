package Instruments;

import Storable.Route;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServerResponse implements Serializable {
    private String additionalInfo = "";
    private ServerResponseCodes code;
    private ArrayList<ServerResponse> scriptReport = new ArrayList<>();
    private boolean access = false;
    private ArrayList<Route> set = new ArrayList<>();
    private Route route;

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setSet(List<Route> list) {
        this.set.addAll(list);
    }

    public ArrayList<Route> getSet() {
        return set;
    }

    public ServerResponse(ServerResponseCodes code) {
        this.code = code;
    }

    public ServerResponse(ServerResponseCodes code, String additionalInfo) {
        this.additionalInfo = additionalInfo;
        this.code = code;
    }

    public boolean isAccess() {
        return access;
    }
    public void setAccess(boolean access) {
        this.access = access;
    }

    public ServerResponseCodes getCode() {
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
