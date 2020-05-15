package Controllers;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import Client.Main;
import Commands.*;
import Instruments.ClientRequest;
import Storable.Coordinates;
import Storable.Location;
import Storable.Route;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class EditWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextFlow enterTip;

    @FXML
    private TextFlow nameTip;

    @FXML
    private TextField nameField;

    @FXML
    private TextFlow coordXTip;

    @FXML
    private TextField coordXField;

    @FXML
    private TextFlow coordYTip;

    @FXML
    private TextField coordYField;

    @FXML
    private TextFlow fromXTip;

    @FXML
    private TextField fromXField;

    @FXML
    private TextFlow fromYTip;

    @FXML
    private TextField fromYField;

    @FXML
    private TextFlow fromNameTip;

    @FXML
    private TextField fromNameField;

    @FXML
    private TextFlow toXTip;

    @FXML
    private TextField toXField;

    @FXML
    private TextFlow toYTip;

    @FXML
    private TextField toYField;

    @FXML
    private TextFlow toNameTip;

    @FXML
    private TextField toNameField;

    @FXML
    private TextFlow distanceTip;

    @FXML
    private TextField distanceField;

    @FXML
    private Button appleBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private TextFlow errorTextFlow;

    @FXML
    private Button deleteBtn;

    private long id;

    @FXML
    void initialize() {//TODO visualize?
        nameTip.getChildren().add(new Text(resources.getString("table.name")));
        coordXTip.getChildren().add(new Text(resources.getString("console.coordX")));
        coordYTip.getChildren().add(new Text(resources.getString("console.coordY")));
        fromXTip.getChildren().add(new Text(resources.getString("console.fromX")));
        fromYTip.getChildren().add(new Text(resources.getString("console.fromY")));
        fromNameTip.getChildren().add(new Text(resources.getString("console.fromName")));
        toXTip.getChildren().add(new Text(resources.getString("console.toX")));
        toYTip.getChildren().add(new Text(resources.getString("console.toY")));
        toNameTip.getChildren().add(new Text(resources.getString("console.toName")));
        distanceTip.getChildren().add(new Text(resources.getString("table.distance")));
        enterTip.getChildren().add(new Text(resources.getString("alerts.change")));


        appleBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean temp = true;
                errorTextFlow.getChildren().clear();
                nameField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                distanceField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                coordYField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                coordXField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                toNameField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                toYField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                toXField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                fromNameField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                fromXField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                fromYField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

                MessageFormat fieldEmpty = new MessageFormat(resources.getString("console.notEmpty"));
                MessageFormat fieldIncorrect = new MessageFormat(resources.getString("console.enterField"));
                if (nameField.getText().isEmpty()) {
                    nameField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    printError(fieldEmpty.format(new Object[]{resources.getString("table.name")}));
                    temp = false;
                }
                if (fromNameField.getText().isEmpty()) {
                    fromNameField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    printError(fieldEmpty.format(new Object[]{resources.getString("console.fromName")}));
                    temp = false;
                }
                if (toNameField.getText().isEmpty()) {
                    toNameField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    printError(fieldEmpty.format(new Object[]{resources.getString("console.toName")}));
                    temp = false;
                }
                try {
                    Double.parseDouble(coordXField.getText());
                } catch (NumberFormatException e) {
                    coordXField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    printError(fieldIncorrect.format(new Object[]{resources.getString("console.coordX"), resources.getString("types.Double")}));
                    temp = false;
                }
                try {
                    Double d = Double.parseDouble(coordYField.getText());
                    if(d<1){
                        temp=false;
                        printError(resources.getString("console.higher")+" -462");
                    }
                } catch (NumberFormatException e) {
                    coordYField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    printError(fieldIncorrect.format(new Object[]{resources.getString("console.coordY"), resources.getString("types.Double")}));
                    temp = false;
                }
                try {
                    Integer.parseInt(fromXField.getText());
                } catch (NumberFormatException e) {
                    fromXField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    printError(fieldIncorrect.format(new Object[]{resources.getString("console.fromX"), resources.getString("types.Integer")}));
                    temp = false;
                }
                try {
                    Long.parseLong(fromYField.getText());
                } catch (NumberFormatException e) {
                    fromYField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    printError(fieldIncorrect.format(new Object[]{resources.getString("console.fromY"), resources.getString("types.Long")}));
                    temp = false;
                }
                try {
                    Integer.parseInt(toXField.getText());
                } catch (NumberFormatException e) {
                    toXField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    printError(fieldIncorrect.format(new Object[]{resources.getString("console.toX"), resources.getString("types.Integer")}));
                    temp = false;
                }
                try {
                    Long.parseLong(toYField.getText());
                } catch (NumberFormatException e) {
                    toYField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    printError(fieldIncorrect.format(new Object[]{resources.getString("console.toY"), resources.getString("types.Long")}));
                    temp = false;
                }
                try {
                    Double d = Double.parseDouble(distanceField.getText());
                    if(d<1){
                        temp=false;
                        printError(resources.getString("console.higher")+" 1");
                    }
                } catch (NumberFormatException e) {
                    distanceField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    printError(fieldIncorrect.format(new Object[]{resources.getString("table.distance"), resources.getString("types.Double")}));
                    temp = false;
                }

                if (temp) {
                    Route route = new Route(id, nameField.getText(), new Coordinates(Double.parseDouble(coordXField.getText()), Double.parseDouble(coordYField.getText())),
                            new Date(), new Location(Integer.parseInt(fromXField.getText()), Long.parseLong(fromYField.getText()), fromNameField.getText()),
                            new Location(Integer.parseInt(toXField.getText()), Long.parseLong(toYField.getText()), toNameField.getText()), Double.parseDouble(distanceField.getText()), Main.login);
                    ICommand command = new UpdateObject(id,route,Main.login);
                    Main.handler.sendRequest(new ClientRequest(command, Main.login, Main.password));
                    Stage stage = (Stage) appleBtn.getScene().getWindow();
                    stage.close();
                }
            }
        });

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) appleBtn.getScene().getWindow();
                stage.close();
            }
        });

        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ButtonType ok = new ButtonType(ResourceBundle.getBundle("MessagesBundle").getString("alerts.delete"), ButtonBar.ButtonData.OK_DONE);
                ButtonType cancel = new ButtonType(ResourceBundle.getBundle("MessagesBundle").getString("alerts.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ok, cancel);
                alert.setTitle(resources.getString("alerts.sureDel"));
                alert.setResizable(false);
                alert.setHeaderText(resources.getString("alerts.sureDel"));
                Optional<ButtonType> result = alert.showAndWait();
                if (result.orElse(cancel) == ok) {
                    ICommand command = new RemoveObject(id,Main.login);
                    Main.handler.sendRequest(new ClientRequest(command, Main.login, Main.password));
                    Stage stage = (Stage) appleBtn.getScene().getWindow();
                    stage.close();
                }
            }
        });
    }
    private void printError(String string){
        Text text = new Text(string+"\n");
        text.setFill(Color.RED);
        errorTextFlow.getChildren().add(text);
    }

    public void setFields(Route route){

        id=route.getId();
        nameField.setText(route.getName());
        coordXField.setText(String.valueOf(route.getCoordinates().getx()));
        coordYField.setText(String.valueOf(route.getCoordinates().gety()));
        fromXField.setText(String.valueOf(route.getFrom().getX()));
        fromYField.setText(String.valueOf(route.getFrom().getY()));
        fromNameField.setText(route.getFrom().getName());
        toXField.setText(String.valueOf(route.getTo().getX()));
        toYField.setText(String.valueOf(route.getTo().getY()));
        toNameField.setText(route.getTo().getName());
        distanceField.setText(String.valueOf(route.getDistance()));
    }
}

