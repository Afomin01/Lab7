package Controllers;

import Client.Main;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class AuthWindowController {
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
    private Text loginFail;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private TextField mailField;

    @FXML
    void initialize() {
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
        });

        ruFlag.setOnMouseClicked(event -> changeLocale(new Locale("ru","RU")));
        horFlag.setOnMouseClicked(event -> changeLocale(new Locale("hr","HR")));
        spFlag.setOnMouseClicked(event -> changeLocale(new Locale("es","EC")));
        trFlag.setOnMouseClicked(event -> changeLocale(new Locale("tr","TR")));
        usFlag.setOnMouseClicked(event -> changeLocale(new Locale("en","US")));
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
}


