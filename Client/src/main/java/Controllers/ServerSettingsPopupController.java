package Controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import Client.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ServerSettingsPopupController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private Button applyBtn;

    @FXML
    private Button cancelBtn;

    private AuthWindowController authWindowController;

    public void setAuthWindowController(AuthWindowController authWindowController) {
        this.authWindowController = authWindowController;
    }

    @FXML
    void initialize() {
        Preferences preferences = Preferences.userRoot();
        hostField.setText(preferences.get("host", "localhost"));
        portField.setText(String.valueOf(preferences.getInt("port",4004)));
        applyBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    int i = Integer.parseInt(portField.getText());
                    if(i>1025 && i<65535) {
                        preferences.putInt("port",i);
                        preferences.put("host", hostField.getText());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("");
                        alert.setResizable(false);
                        alert.setHeaderText("");
                        alert.setContentText(resources.getString("settings.newServerOK"));
                        alert.showAndWait();
                        authWindowController.reconnect();
                        ((Stage) cancelBtn.getScene().getWindow()).close();
                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("");
                        alert.setResizable(false);
                        alert.setHeaderText("");
                        alert.setContentText(resources.getString("settings.invalidPort"));
                        alert.showAndWait();
                    }
                }catch (NumberFormatException e){

                }
            }
        });

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Stage) cancelBtn.getScene().getWindow()).close();
            }
        });

    }
}
