package Controllers;

import Client.Main;
import Client.Utils.EThemes;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class AuthWindowController {
    @FXML
    private ResourceBundle resources;
    @FXML
    private ImageView ruFlag;
    @FXML
    private ImageView spFlag;
    @FXML
    private ImageView horFlag;
    @FXML
    private ImageView trFlag;
    @FXML
    private ImageView usFlag;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button logInBtn;
    @FXML
    private Button signUpBtn;
    @FXML
    private Circle serverPoint;
    @FXML
    private ImageView refreshStatus;
    @FXML
    private ImageView serverSettings;
    @FXML
    private Text loginFail;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private TextField mailField;
    @FXML
    private Text serverLabel;
    @FXML
    private AnchorPane mainAnchor;
    @FXML
    private Text authLabel;
    @FXML
    private AnchorPane backAnchor;


    @FXML
    void initialize() {
/*        Preferences preferences = Preferences.userRoot();
        if(!preferences.get("theme", EThemes.DEFAULT.toString()).equals(EThemes.DEFAULT.toString())){
            String textColor = EThemes.valueOf(preferences.get("theme", "default")).textColor;
            String secondaryColour = EThemes.valueOf(preferences.get("theme", "default")).secondaryColour;
            String additionalColour = EThemes.valueOf(preferences.get("theme", "default")).additionalColour;
            String mainColor = EThemes.valueOf(preferences.get("theme", "default")).mainColor;
            serverLabel.setFill(Paint.valueOf(textColor));
            authLabel.setFill(Paint.valueOf(textColor));
            mailField.setStyle("-fx-background-color: "+secondaryColour + "; -fx-text-inner-color: "+textColor);
            passwordField.setStyle("-fx-background-color: "+secondaryColour+ "; -fx-text-inner-color: "+textColor);
            loginField.setStyle("-fx-background-color: "+secondaryColour+ "; -fx-text-inner-color: "+textColor);
            repeatPasswordField.setStyle("-fx-background-color: "+secondaryColour+ "; -fx-text-inner-color: "+textColor);
            mainAnchor.setStyle("-fx-background-color: "+mainColor);
            backAnchor.setStyle("-fx-background-color: "+additionalColour);
            loginFail.setFill(Paint.valueOf(textColor));
        }*/
        if(Main.isConnected()){
            serverPoint.setFill(Color.GREEN);
            logInBtn.setDisable(false);
            signUpBtn.setDisable(false);
        }
        else {
            serverPoint.setFill(Color.RED);
            logInBtn.setDisable(true);
            signUpBtn.setDisable(true);
        }

        refreshStatus.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                reconnect();
            }
        });
        serverSettings.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle"));

                    Parent root = fxmlLoader.load(Main.class.getResource("/ServerSettingsPopup.fxml").openStream());
                    ((ServerSettingsPopupController)fxmlLoader.getController()).setAuthWindowController(AuthWindowController.this);
                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(Main.stage.getScene().getWindow());
                    stage.setTitle(resources.getString("settings.serverSettingsTip"));
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                }catch (Exception e){}
            }
        });

        ruFlag.setOnMouseClicked(event -> {
            changeLocale(new Locale("ru","RU"));
            changeLanguage();
        });
        horFlag.setOnMouseClicked(event -> {
            changeLocale(new Locale("hr","HR"));
            changeLanguage();
        });
        spFlag.setOnMouseClicked(event -> {
            changeLocale(new Locale("es","EC"));
            changeLanguage();
        });
        trFlag.setOnMouseClicked(event -> {
            changeLocale(new Locale("tr","TR"));
            changeLanguage();
        });
        usFlag.setOnMouseClicked(event -> {
            changeLocale(new Locale("en","US"));
            changeLanguage();
        });

        logInBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(loginField.getText().isEmpty() || passwordField.getText().isEmpty()){
                    loginFail.setText(ResourceBundle.getBundle("MessagesBundle").getString("login.empty"));
                    passwordField.clear();
                }else {
                    switch (Main.logIn(loginField.getText(), passwordField.getText(),false)){
                        case ERROR:
                        case SQL_ERROR:
                            loginFail.setText(ResourceBundle.getBundle("MessagesBundle").getString("login.error"));
                            passwordField.clear();
                            break;
                        case INCORRECT_LOG_IN:
                            loginFail.setText(ResourceBundle.getBundle("MessagesBundle").getString("login.incorrect"));
                            passwordField.clear();
                            break;
                        case AUTHORISED:
                            Main.openMainWindow();
                            break;
                    }
                }
            }
        });

        signUpBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if((loginField.getText().isEmpty() || passwordField.getText().isEmpty()) && !repeatPasswordField.isDisable()){
                    loginFail.setText(ResourceBundle.getBundle("MessagesBundle").getString("login.empty"));
                    passwordField.clear();
                }else if(repeatPasswordField.isDisable()){
                    repeatPasswordField.setDisable(false);
                    mailField.setDisable(false);
                }else {
                    if(passwordField.getText().length()<5 || !passwordField.getText().matches(".*\\d+.*") || !passwordField.getText().matches(".*[A-Z].*")){
                        loginFail.setText(ResourceBundle.getBundle("MessagesBundle").getString("login.passwordIncorrect"));
                        passwordField.clear();
                        repeatPasswordField.clear();
                    }else if(!repeatPasswordField.getText().equals(passwordField.getText())){
                        loginFail.setText(ResourceBundle.getBundle("MessagesBundle").getString("login.passwordDismatch"));
                        passwordField.clear();
                        repeatPasswordField.clear();
                    } else {
                        switch (Main.signUp(loginField.getText(), passwordField.getText(), mailField.getText())) {
                            case ERROR:
                            case SQL_ERROR:
                                loginFail.setText(ResourceBundle.getBundle("MessagesBundle").getString("login.error"));
                                passwordField.clear();
                                repeatPasswordField.clear();
                                break;
                            case INCORRECT_LOG_IN:
                                loginFail.setText(ResourceBundle.getBundle("MessagesBundle").getString("login.exists"));
                                loginField.clear();
                                passwordField.clear();
                                repeatPasswordField.clear();
                                break;
                            case AUTHORISED:
                                Main.openMainWindow();
                                break;
                        }
                    }
                }
            }
        });
    }

    public void reconnect(){
        if(Main.connectServer()) {
            serverPoint.setFill(Color.GREEN);
            logInBtn.setDisable(false);
            signUpBtn.setDisable(false);
        }
        else {
            serverPoint.setFill(Color.RED);
            logInBtn.setDisable(true);
            signUpBtn.setDisable(true);
        }
    }

    private void changeLocale(Locale locale){
        try {
            Locale.setDefault(locale);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

            Parent root = fxmlLoader.load(this.getClass().getResource("/AuthWindow.fxml").openStream());
            Scene scene = new Scene(root);

            Main.stage.setScene(scene);

        }catch (Exception ignored){
        }
    }

    private void changeLanguage(){
        Preferences preferences = Preferences.userRoot();
        preferences.put("language", Locale.getDefault().getLanguage());
        preferences.put("country", Locale.getDefault().getCountry());
        preferences.put("theme", "default");
    }
}


