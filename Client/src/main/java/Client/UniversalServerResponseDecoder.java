package Client;

import Instruments.ServerResponse;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class UniversalServerResponseDecoder {
    public static String decodeResponse(ServerResponse response){
        String out="";
        ResourceBundle resourceBundle = ResourceBundle.getBundle("MessagesBundle", Locale.getDefault());
        switch (response.getCode()){
            case SEARCH_OK:
                out=resourceBundle.getString("response.search")+"\n"+response.getAdditionalInfo();
                break;
            case COUNT:
                out=resourceBundle.getString("response.count")+"\n"+response.getAdditionalInfo();
                break;
            case SEARCH_NOT_FOUND:
                out=resourceBundle.getString("response.nothingFound");
                break;
            case SQL_ERROR:
            case ERROR:
                out=resourceBundle.getString("response.error");
                break;
            case ADD_OK:
                out=resourceBundle.getString("response.addOK");
                break;
            case NO_CHANGES:
                out=resourceBundle.getString("response.noChange");
                break;
            case DELETE_OK:
                out=resourceBundle.getString("response.delOK");
                break;
            case CLEAR_OK:
                out=resourceBundle.getString("response.clear");
                break;
            case UPDATE_OK:
                out=resourceBundle.getString("response.update");
                break;
            case SCRIPT_RESULT:
                System.out.println("sdgajkgksgljkakg");
                for (ServerResponse r : response.getScriptReport()){
                    out=out+decodeResponse(r);
                    System.out.println(out);
                }
                break;
            case SERVER_FATAL_ERROR:
                out=resourceBundle.getString("response.serverFatal");//TODO exit
                break;
            case HISTORY:
                out=resourceBundle.getString("response.history")+"\n"+response.getAdditionalInfo();
                break;
            case EXIT:
                out=resourceBundle.getString("response.exit");//TODO exit
                break;
            case SURPRISE_NOT_CORRECT_LOGIN_OR_PASSWORD:
                out=resourceBundle.getString("response.surprise");
                break;
            case SCRIPT_REC:
                out=resourceBundle.getString("response.scriptRec")+" "+response.getAdditionalInfo();
                break;
            case SCRIPT_COMMAND_ERR:
                out=resourceBundle.getString("response.scriptErr")+" "+response.getAdditionalInfo();
                break;
            case SCRIPT_FILE_ERR:
                out=resourceBundle.getString("response.scriptFileErr");
                break;
            case SHOW:
                out=resourceBundle.getString("response.show")+"\n"+response.getAdditionalInfo();
                break;
            case INFO:
                out=resourceBundle.getString("response.info")
                        +"\n"+ resourceBundle.getString("response.info.type") +" "+ Arrays.asList(response.getAdditionalInfo().trim().split(" ")).get(0)
                        +"\n"+ resourceBundle.getString("response.info.count") +" "+ Arrays.asList(response.getAdditionalInfo().trim().split(" ")).get(1)
                        +"\n"+ resourceBundle.getString("response.info.storable") +" "+ Arrays.asList(response.getAdditionalInfo().trim().split(" ")).get(2);
                break;
            case HELP:
                //TODO
                break;
        }
        return out+"\n";
    }
}
