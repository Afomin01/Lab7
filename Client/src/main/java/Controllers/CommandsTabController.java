package Controllers;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

import Client.CommandFactory;
import Client.Main;
import Client.UniversalServerResponseDecoder;
import Commands.*;
import Instruments.ClientRequest;
import Instruments.ServerResponse;
import Storable.Coordinates;
import Storable.Location;
import Storable.Route;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

public class CommandsTabController {
    private LinkedList<Integer> q = new LinkedList<>();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<String> commandsComboBox;

    @FXML
    private Button executeBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private TextField firstArgField;

    @FXML
    private TextFlow enterFieldsTip;

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
    private Button scriptChooser;
    private boolean consoleRequest=true;
    private boolean makingElement=false;
    private File file;
    private CommandFactory commandFactory = new CommandFactory();
    private String currentCommand;
    private int routeArgumentCount = 0;

    @FXML
    void initialize() {//TODO add listeners for fields
        enterFieldsTip.getChildren().add(new Text(resources.getString("console.elementTip")));
        enterFieldsTip.setVisible(false);
        commandsComboBox.setItems(FXCollections.observableArrayList(Arrays.asList(resources.getString("commands.name.add"),
                resources.getString("commands.name.update"),
                resources.getString("commands.name.removegreater"),
                resources.getString("commands.name.addifmax"),
                resources.getString("commands.name.clear"),
                resources.getString("commands.name.countgreaterthandistacne"),
                resources.getString("commands.name.executescript"),
                resources.getString("commands.name.exit"),
                resources.getString("commands.name.help"),
                resources.getString("commands.name.history"),
                resources.getString("commands.name.info"),
                resources.getString("commands.name.printuniquedistance"),
                resources.getString("commands.name.removeid"),
                resources.getString("commands.name.allbydistance"),
                resources.getString("commands.name.show"))));
        console.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.ENTER){
                ICommand command = null;
                MessageFormat enterFormat = new MessageFormat(resources.getString("console.enterField"));
                MessageFormat incorrectNum = new MessageFormat(resources.getString("console.incorrectIn"));
                if(!makingElement) {
                    consoleRequest = true;
                    int y = console.getText().split("\n").length;
                    try {
                        currentCommand = Arrays.asList(console.getText().split("\n")).get(y - 1);
                        if (Arrays.asList(currentCommand.trim().split(" ")).get(0).equals("add") || Arrays.asList(currentCommand.trim().split(" ")).get(0).equals("add_if_max") || Arrays.asList(currentCommand.trim().split(" ")).get(0).equals("update") || Arrays.asList(currentCommand.trim().split(" ")).get(0).equals("remove_greater")) {
                            makingElement = true;
                            if (Arrays.asList(currentCommand.trim().split(" ")).get(0).equals("update")) {
                                routeArgumentCount = 0;
                                consolePrint(enterFormat.format(new Object[]{resources.getString("table.id"),resources.getString("types.Long")}));
                            }
                            else {
                                routeArgumentCount = 1;
                                consolePrint(enterFormat.format(new Object[]{resources.getString("table.name"), resources.getString("types.String")}));
                            }
                            command = null;
                        } else
                            command = new CommandFactory().getCommand(currentCommand.trim().split(" "), console, Main.login);
                        if (command != null)
                            Main.handler.sendRequest(new ClientRequest(command, Main.login, Main.password));
                    }catch (ArrayIndexOutOfBoundsException e){
                        console.end();
                    }
                }else {
                    try {
                        String s = Arrays.asList(console.getText().split("\n")).get(console.getText().split("\n").length-1);
                        switch (routeArgumentCount) {
                            case 0:
                                try {
                                    Long id = Long.parseLong(s);
                                    currentCommand += " " + s;
                                    routeArgumentCount++;
                                    consolePrint(enterFormat.format(new Object[]{resources.getString("table.name"), resources.getString("types.String")}));
                                } catch (NumberFormatException e) {
                                    consolePrint(incorrectNum.format(new Object[]{resources.getString("types.Long")}));
                                }
                                break;
                            case 1:
                                if (!s.isEmpty() && !s.matches(".*:")) {
                                    currentCommand += " " + s;
                                    routeArgumentCount++;
                                    consolePrint(enterFormat.format(new Object[]{resources.getString("console.coordX"), resources.getString("types.Double")}));
                                } else {
                                    consolePrint(resources.getString("console.notEmpty.console"));
                                }
                                break;
                            case 2:
                                try {
                                    Double d = Double.parseDouble(s);
                                    currentCommand += " " + s;
                                    routeArgumentCount++;
                                    consolePrint(enterFormat.format(new Object[]{resources.getString("console.coordY"), resources.getString("types.Double")}));
                                } catch (NumberFormatException e) {
                                    consolePrint(incorrectNum.format(new Object[]{resources.getString("types.Double")}));
                                }
                                break;
                            case 3:
                                try {
                                    Double d = Double.parseDouble(s);
                                    currentCommand += " " + s;
                                    routeArgumentCount++;
                                    consolePrint(enterFormat.format(new Object[]{resources.getString("console.fromX"), resources.getString("types.Integer")}));
                                } catch (NumberFormatException e) {
                                    consolePrint(incorrectNum.format(new Object[]{resources.getString("types.Double")}));
                                }
                                break;
                            case 4:
                                try {
                                    Integer d = Integer.parseInt(s);
                                    currentCommand += " " + s;
                                    routeArgumentCount++;
                                    consolePrint(enterFormat.format(new Object[]{resources.getString("console.fromY"), resources.getString("types.Long")}));
                                } catch (NumberFormatException e) {
                                    consolePrint(incorrectNum.format(new Object[]{resources.getString("types.Integer")}));
                                }
                                break;
                            case 5:
                                try {
                                    Long d = Long.parseLong(s);
                                    currentCommand += " " + s;
                                    routeArgumentCount++;
                                    consolePrint(enterFormat.format(new Object[]{resources.getString("console.fromName"), resources.getString("types.String")}));
                                } catch (NumberFormatException e) {
                                    consolePrint(incorrectNum.format(new Object[]{resources.getString("types.Long")}));
                                }
                                break;
                            case 6:
                                if (!s.isEmpty() && !s.matches(".*:")) {
                                    currentCommand += " " + s;
                                    routeArgumentCount++;
                                    consolePrint(enterFormat.format(new Object[]{resources.getString("console.toX"), resources.getString("types.Integer")}));
                                } else {
                                    consolePrint(resources.getString("console.notEmpty.console"));
                                }
                                break;
                            case 7:
                                try {
                                    Integer d = Integer.parseInt(s);
                                    currentCommand += " " + s;
                                    routeArgumentCount++;
                                    consolePrint(enterFormat.format(new Object[]{resources.getString("console.toY"), resources.getString("types.Long")}));
                                } catch (NumberFormatException e) {
                                    consolePrint(incorrectNum.format(new Object[]{resources.getString("types.Integer")}));
                                }
                                break;
                            case 8:
                                try {
                                    Long d = Long.parseLong(s);
                                    currentCommand += " " + s;
                                    routeArgumentCount++;
                                    consolePrint(enterFormat.format(new Object[]{resources.getString("console.toName"), resources.getString("types.String")}));
                                } catch (NumberFormatException e) {
                                    consolePrint(incorrectNum.format(new Object[]{resources.getString("types.Long")}));
                                }
                                break;
                            case 9:
                                if (!s.isEmpty() && !s.matches(".*:")) {
                                    currentCommand += " " + s;
                                    routeArgumentCount++;
                                    consolePrint(enterFormat.format(new Object[]{resources.getString("table.distance"), resources.getString("types.Double")}));
                                } else {
                                    consolePrint(resources.getString("console.notEmpty.console"));
                                }
                                break;
                            case 10:
                                try {
                                    Double d = Double.parseDouble(s);
                                    currentCommand += " " + s;
                                    routeArgumentCount++;
                                    command = new CommandFactory().getCommand(currentCommand.trim().split(" "), console, Main.login);
                                    makingElement = false;
                                    Main.handler.sendRequest(new ClientRequest(command, Main.login, Main.password));
                                } catch (NumberFormatException e) {
                                    consolePrint(incorrectNum.format(new Object[]{resources.getString("types.Double")}));
                                }
                                break;
                        }
                    }catch (ArrayIndexOutOfBoundsException e){
                        console.end();
                    }
                }
            }
        });
        executeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
        clearBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pane.setContent(new Text());
            }
        });
        commandsComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String o, String n) {
                executeBtn.setDisable(false);
                argsErrorText.getChildren().clear();
                argsErrorText.getChildren().clear();
                nameField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                distanceField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                coordsYField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                coordsXField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                toNameField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                toYField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                toXField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                fromNameField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                fromXField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                fromYField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                firstArgField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

                if(n.equals(commandsComboBox.getItems().get(0)) || n.equals(commandsComboBox.getItems().get(2)) || n.equals(commandsComboBox.getItems().get(3))){
                    elementHideVbox.setVisible(true);
                    enterFieldsTip.setVisible(true);
                    firstArgField.setVisible(false);
                    scriptChooser.setVisible(false);
                }else if(n.equals(commandsComboBox.getItems().get(1))){
                    elementHideVbox.setVisible(true);
                    enterFieldsTip.setVisible(true);
                    firstArgField.setVisible(true);
                    scriptChooser.setVisible(false);
                }else if(n.equals(commandsComboBox.getItems().get(5)) || n.equals(commandsComboBox.getItems().get(12)) || n.equals(commandsComboBox.getItems().get(13))){
                    elementHideVbox.setVisible(false);
                    enterFieldsTip.setVisible(false);
                    firstArgField.setVisible(true);
                    scriptChooser.setVisible(false);
                }else if(n.equals(commandsComboBox.getItems().get(6))){
                    scriptChooser.setVisible(false);//:(
                    elementHideVbox.setVisible(false);
                    enterFieldsTip.setVisible(false);
                    firstArgField.setVisible(true);
                }else{
                    scriptChooser.setVisible(false);
                    elementHideVbox.setVisible(false);
                    enterFieldsTip.setVisible(false);
                    firstArgField.setVisible(false);
                }
            }
        });
        scriptChooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("txt files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);

                file = fileChooser.showOpenDialog(Main.stage);
                if (!file.exists()) file=null;
                if (!file.canRead() || !file.canWrite()) file=null;
            }
        });
        executeBtn.setOnAction(new EventHandler<ActionEvent>() {
            MessageFormat fieldEmpty = new MessageFormat(resources.getString("console.notEmpty"));
            MessageFormat fieldIncorrect = new MessageFormat(resources.getString("console.enterField"));
            MessageFormat incorrectIn = new MessageFormat(resources.getString("console.incorrectIn"));
            @Override
            public void handle(ActionEvent event) {
                boolean temp = true;
                argsErrorText.getChildren().clear();
                nameField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                distanceField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                coordsYField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                coordsXField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                toNameField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                toYField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                toXField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                fromNameField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                fromXField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                fromYField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                firstArgField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

                if (commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(0)) || commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(2)) || commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(3)) || commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(1))) {
                    if (nameField.getText().isEmpty()) {
                        nameField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        errorComboBoxPrint(fieldEmpty.format(new Object[]{resources.getString("table.name")}));
                        temp = false;
                    }
                    if (fromNameField.getText().isEmpty()) {
                        fromNameField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        errorComboBoxPrint(fieldEmpty.format(new Object[]{resources.getString("console.fromName")}));
                        temp = false;
                    }
                    if (toNameField.getText().isEmpty()) {
                        toNameField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        errorComboBoxPrint(fieldEmpty.format(new Object[]{resources.getString("console.toName")}));
                        temp = false;
                    }
                    if (commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(1))) {
                        try {
                            Long.parseLong(firstArgField.getText());
                        } catch (NumberFormatException e) {
                            firstArgField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            errorComboBoxPrint(fieldIncorrect.format(new Object[]{resources.getString("table.id"), resources.getString("types.Long")}));
                            temp = false;
                        }
                    }
                    try {
                        Double.parseDouble(coordsXField.getText());
                    } catch (NumberFormatException e) {
                        coordsXField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        errorComboBoxPrint(fieldIncorrect.format(new Object[]{resources.getString("console.coordX"), resources.getString("types.Double")}));
                        temp = false;
                    }
                    try {
                        Double.parseDouble(coordsYField.getText());
                    } catch (NumberFormatException e) {
                        coordsYField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        errorComboBoxPrint(fieldIncorrect.format(new Object[]{resources.getString("console.coordY"), resources.getString("types.Double")}));
                        temp = false;
                    }
                    try {
                        Integer.parseInt(fromXField.getText());
                    } catch (NumberFormatException e) {
                        fromXField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        errorComboBoxPrint(fieldIncorrect.format(new Object[]{resources.getString("console.fromX"), resources.getString("types.Integer")}));
                        temp = false;
                    }
                    try {
                        Long.parseLong(fromYField.getText());
                    } catch (NumberFormatException e) {
                        fromYField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        errorComboBoxPrint(fieldIncorrect.format(new Object[]{resources.getString("console.fromY"), resources.getString("types.Long")}));
                        temp = false;
                    }
                    try {
                        Integer.parseInt(toXField.getText());
                    } catch (NumberFormatException e) {
                        toXField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        errorComboBoxPrint(fieldIncorrect.format(new Object[]{resources.getString("console.toX"), resources.getString("types.Integer")}));
                        temp = false;
                    }
                    try {
                        Long.parseLong(toYField.getText());
                    } catch (NumberFormatException e) {
                        toYField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        errorComboBoxPrint(fieldIncorrect.format(new Object[]{resources.getString("console.toY"), resources.getString("types.Long")}));
                        temp = false;
                    }
                    try {
                        Double.parseDouble(distanceField.getText());
                    } catch (NumberFormatException e) {
                        distanceField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        errorComboBoxPrint(fieldIncorrect.format(new Object[]{resources.getString("table.distance"), resources.getString("types.Double")}));
                        temp = false;
                    }

                    if (temp) {
                        Route route = new Route(0, nameField.getText(), new Coordinates(Double.parseDouble(coordsXField.getText()), Double.parseDouble(coordsYField.getText())),
                                new Date(), new Location(Integer.parseInt(fromXField.getText()), Long.parseLong(fromYField.getText()), fromNameField.getText()),
                                new Location(Integer.parseInt(toXField.getText()), Long.parseLong(toYField.getText()), toNameField.getText()), Double.parseDouble(distanceField.getText()), Main.login);
                        ICommand command = null;
                        if (commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(0)))
                            command = new Add(route, Main.login);
                        if (commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(1)))
                            command = new UpdateId(Long.parseLong(firstArgField.getText()), route, Main.login);
                        if (commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(2)))
                            command = new RemoveGreater(route, Main.login);
                        if (commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(3)))
                            command = new AddIfMax(route, Main.login);
                        consoleRequest = false;
                        Main.handler.sendRequest(new ClientRequest(command, Main.login, Main.password));
                    }
                } else if (commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(5)) || commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(13))) {
                    try{
                        Double distance = Double.parseDouble(firstArgField.getText());
                        consoleRequest = false;
                        if(commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(5)))Main.handler.sendRequest(new ClientRequest(new CountGreaterThanDistance(distance), Main.login, Main.password));
                        else Main.handler.sendRequest(new ClientRequest(new RemoveAllByDistance(distance,Main.login), Main.login, Main.password));
                    }catch (NumberFormatException e){
                        errorComboBoxPrint(incorrectIn.format(new Object[]{resources.getString("types.Double")}));
                        firstArgField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    }
                } else if (commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(6))) {
                    /*if(file!=null){
                        consoleRequest = false;
                        Main.handler.sendRequest(new ClientRequest(new ExecuteScript(file,Main.login),Main.login,Main.password));
                    }else argsErrorText.getChildren().add(new Text(resources.getString("commands.noFile")));*/
                    if(firstArgField.getText().matches(".*\\.txt")) {
                        consoleRequest = false;
                        Main.handler.sendRequest(new ClientRequest(new ExecuteScript(firstArgField.getText(),Main.login),Main.login,Main.password));
                    }
                    else {
                        argsErrorText.getChildren().add(new Text(resources.getString("commands.noFile")));
                        firstArgField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    }
                } else if(commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(12))){
                    try{
                        long id = Long.parseLong(firstArgField.getText());
                        consoleRequest = false;
                        Main.handler.sendRequest(new ClientRequest(new RemoveById(id,Main.login), Main.login, Main.password));
                    }catch (NumberFormatException e){
                        errorComboBoxPrint(incorrectIn.format(new Object[]{resources.getString("types.Long")}));
                        firstArgField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    }
                } else {
                    consoleRequest = false;
                    if(commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(4)))Main.handler.sendRequest(new ClientRequest(new Clear(Main.login), Main.login, Main.password));
                    else if(commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(7)))Main.handler.sendRequest(new ClientRequest(new Exit(Main.login), Main.login, Main.password));
                    else if(commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(8)))Main.handler.sendRequest(new ClientRequest(new Help(), Main.login, Main.password));
                    else if(commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(9)))Main.handler.sendRequest(new ClientRequest(new History(Main.login), Main.login, Main.password));
                    else if(commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(10)))Main.handler.sendRequest(new ClientRequest(new Info(), Main.login, Main.password));
                    else if(commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(11)))Main.handler.sendRequest(new ClientRequest(new PrintUniqueDistance(), Main.login, Main.password));
                    else if(commandsComboBox.getSelectionModel().getSelectedItem().equals(commandsComboBox.getItems().get(14)))Main.handler.sendRequest(new ClientRequest(new Show(), Main.login, Main.password));
                }
            }
        });
    }
    public void displayServerResponse(ServerResponse serverResponse){
        if(consoleRequest) {
            console.setText(console.getText() + UniversalServerResponseDecoder.decodeResponse(serverResponse) + "\n");
            console.end();
        }else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Text text = new Text(UniversalServerResponseDecoder.decodeResponse(serverResponse));
                    text.setFont(Font.font (text.getFont().getFamily(), 14));
                    pane.setContent(text);
                }
            });
        }
    }
    public void consolePrint(String string){
        console.setText(console.getText()+"\n"+string);
        console.end();
    }
    public void errorComboBoxPrint(String string){
        Text text = new Text(string+"\n");
        text.setFill(Color.RED);
        argsErrorText.getChildren().add(text);
    }
}
