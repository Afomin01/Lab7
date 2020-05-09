package Controllers;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;

import Client.CommandFactory;
import Client.Main;
import Commands.ICommand;
import Instruments.ClientRequest;
import Instruments.ServerResponse;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class CommandsTabController {
    private LinkedList<Integer> q = new LinkedList<>();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<?> commandsComboBox;

    @FXML
    private Button executeBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private TextField firstArgField;

    @FXML
    private Text enterFieldsTip;

    @FXML
    private VBox elementHideVbox;

    @FXML
    private TextField nameField;

    @FXML
    private TextField coordsXField;

    @FXML
    private TextField coordsYField;

    @FXML
    private TextField fromXField;

    @FXML
    private TextField fromYField;

    @FXML
    private TextField fromNameField;

    @FXML
    private TextField toXField;

    @FXML
    private TextField toYField;

    @FXML
    private TextField toNameField;

    @FXML
    private TextField distanceField;

    @FXML
    private TextFlow argsErrorText;

    @FXML
    private ScrollPane pane;

    @FXML
    private TextArea console;

    @FXML
    void initialize() {
        console.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()== KeyCode.ENTER){
                    int y = console.getText().split("\n").length;
                    String s = Arrays.asList(console.getText().split("\n")).get(y-1);
                    ICommand command = new CommandFactory().getCommand(s.trim().split(" "), console, Main.login);
                    Main.handler.sendRequest(new ClientRequest(command,Main.login,Main.password));
                    System.out.println(Main.password+" "+Main.login);
                }
            }
        });
    }
    public void displayServerResponse(ServerResponse serverResponse){
        console.setText(console.getText() + serverResponse.getAdditionalInfo());
        console.end();
    }
}
