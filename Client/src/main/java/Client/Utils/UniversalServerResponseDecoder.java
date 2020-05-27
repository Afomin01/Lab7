package Client.Utils;

import Client.Main;
import Commands.EAvailableCommands;
import Instruments.ServerResponse;
import javafx.scene.control.Alert;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public abstract class UniversalServerResponseDecoder {
    public static String decodeResponse(ServerResponse response) {
        String out = "";
        ResourceBundle resourceBundle = ResourceBundle.getBundle("MessagesBundle", Locale.getDefault());
        Preferences preferences = Preferences.userRoot();
        switch (response.getCode()) {
            case SEARCH_OK:
                out = resourceBundle.getString("response.search") + "\n" + response.getAdditionalInfo();
                break;
            case COUNT:
                out = resourceBundle.getString("response.count") + "\n" + response.getAdditionalInfo();
                break;
            case SEARCH_NOT_FOUND:
                out = resourceBundle.getString("response.nothingFound");
                break;
            case SQL_ERROR:
            case ERROR:
                out = resourceBundle.getString("response.error");
                break;
            case ADD_OK:
                out = resourceBundle.getString("response.addOK");
                break;
            case NO_CHANGES:
                out = resourceBundle.getString("response.noChange");
                break;
            case DELETE_OK:
                out = resourceBundle.getString("response.delOK");
                break;
            case CLEAR_OK:
                out = resourceBundle.getString("response.clear");
                break;
            case UPDATE_OK:
                out = resourceBundle.getString("response.update");
                break;
            case SCRIPT_RESULT:
                for (ServerResponse r : response.getScriptReport()) {
                    out = out + decodeResponse(r);
                }
                break;
            case SERVER_FATAL_ERROR:
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle(resourceBundle.getString("alerts.exit"));
                alert1.setResizable(false);
                alert1.setHeaderText("");
                alert1.setContentText(resourceBundle.getString("response.serverFatal"));
                alert1.showAndWait();
                Main.exitUser();
                break;
            case HISTORY:
                out = resourceBundle.getString("response.history") + "\n" + response.getAdditionalInfo();
                break;
            case EXIT:
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(resourceBundle.getString("alerts.exit"));
                alert.setResizable(false);
                alert.setHeaderText("");
                alert.setContentText(resourceBundle.getString("response.exit"));
                alert.showAndWait();
                Main.exitUser();
                break;
            case SURPRISE_NOT_CORRECT_LOGIN_OR_PASSWORD:
                out = resourceBundle.getString("response.surprise");
                break;
            case SCRIPT_REC:
                out = resourceBundle.getString("response.scriptRec") + " " + response.getAdditionalInfo();
                break;
            case SCRIPT_COMMAND_ERR:
                out = resourceBundle.getString("response.scriptErr") + " " + response.getAdditionalInfo();
                break;
            case SCRIPT_FILE_ERR:
                out = resourceBundle.getString("response.scriptFileErr");
                break;
            case SHOW:
                out = resourceBundle.getString("response.show") + "\n" + response.getAdditionalInfo();
                break;
            case INFO:
                out = resourceBundle.getString("response.info")
                        + "\n" + resourceBundle.getString("response.info.type") + " " + Arrays.asList(response.getAdditionalInfo().trim().split(" ")).get(0)
                        + "\n" + resourceBundle.getString("response.info.count") + " " + Arrays.asList(response.getAdditionalInfo().trim().split(" ")).get(1)
                        + "\n" + resourceBundle.getString("response.info.storable") + " " + Arrays.asList(response.getAdditionalInfo().trim().split(" ")).get(2);
                break;
            case HELP:
                MessageFormat messageFormat = new MessageFormat(resourceBundle.getString("commands.pattern"));
                out += resourceBundle.getString("commands.help.tip") + "\n\n";
                for (EAvailableCommands e : EAvailableCommands.values()) {
                    if (!e.equals(EAvailableCommands.Not_A_Command)) {
                        out += messageFormat.format(
                                new Object[]{resourceBundle.getString(e.localizedResourceBundleName),
                                        e.toString(),
                                        String.join(", ", Arrays.stream(e.args.split(",")).map(resourceBundle::getString).collect(Collectors.toList())),
                                        resourceBundle.getString(e.localizedResourceBundleDiscription)}) + "\n";
                    }
                }
                break;
            case CHANGE_FIELDS_OK:
                out = resourceBundle.getString("response.update");
                break;
            case CHANGE_FIELDS_ERROR:
                out = resourceBundle.getString("response.error");
                break;
            case CHANGE_FIELDS_NO_RIGHTS:
                out = resourceBundle.getString("response.noChange");
                break;
            case REMOVE_ITEM_BY_ID:
                out = resourceBundle.getString("alerts.deleted");
                break;
        }
        return out + "\n";
    }
}
