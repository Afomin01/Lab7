package Controllers;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SettingsWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text languageText;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private Text themeText;

    @FXML
    private ComboBox<?> themeComboBox;

    @FXML
    private Button applyBtn;

    @FXML
    private Button cancelBtn;
    private MainWindowController mainWindowController;
    private Locale newLocale = null;

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    @FXML
    void initialize() {
        languageComboBox.setItems(FXCollections.observableArrayList(
                new Locale("ru").getDisplayLanguage(new Locale("ru")),
                new Locale("en").getDisplayLanguage(new Locale("en")),
                new Locale("es").getDisplayLanguage(new Locale("es")),
                new Locale("hr").getDisplayLanguage(new Locale("hr")),
                new Locale("tr").getDisplayLanguage(new Locale("tr"))));
        if(Locale.getDefault().getLanguage().equals(new Locale("ru").getLanguage()))languageComboBox.getSelectionModel().select(0);
        if(Locale.getDefault().getLanguage().equals(new Locale("en").getLanguage()))languageComboBox.getSelectionModel().select(1);
        if(Locale.getDefault().getLanguage().equals(new Locale("es").getLanguage()))languageComboBox.getSelectionModel().select(2);
        if(Locale.getDefault().getLanguage().equals(new Locale("hr").getLanguage()))languageComboBox.getSelectionModel().select(3);
        if(Locale.getDefault().getLanguage().equals(new Locale("tr").getLanguage()))languageComboBox.getSelectionModel().select(4);

        languageComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                applyBtn.setDisable(false);
                if(newValue.equals(languageComboBox.getItems().get(0))) newLocale = new Locale("ru","RU");
                if(newValue.equals(languageComboBox.getItems().get(1))) newLocale = new Locale("en","US");
                if(newValue.equals(languageComboBox.getItems().get(2))) newLocale = new Locale("es","EC");
                if(newValue.equals(languageComboBox.getItems().get(3))) newLocale = new Locale("hr","HR");
                if(newValue.equals(languageComboBox.getItems().get(4))) newLocale = new Locale("tr","TR");
            }
        });

        applyBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(newLocale!=null) {
                    Locale.setDefault(newLocale);
                    mainWindowController.changeLanguage(newLocale);
                    Stage stage = (Stage) applyBtn.getScene().getWindow();
                    stage.close();
                }
            }
        });
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) applyBtn.getScene().getWindow();
                stage.close();
            }
        });
    }
}
