package Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ColourPickersController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox pickersVbox;

    @FXML
    private Button applyBtn;

    @FXML
    private Button cancelBtn;

    private Map<String, Color> userColors;
    private ArrayList<ColorPicker> colorPickers = new ArrayList<>();
    private VisualizeWindowController visualizeWindowController;

    public void setVisualizeWindowController(VisualizeWindowController visualizeWindowController) {
        this.visualizeWindowController = visualizeWindowController;
    }

    public void setUserColors(Map<String, Color> userColors) {
        this.userColors = userColors;
        for(String s : userColors.keySet()){
            ColorPicker colorPicker = new ColorPicker();
            colorPicker.setValue(userColors.get(s));
            colorPicker.setPromptText(s);
            colorPickers.add(colorPicker);
            Label label = new Label(s);
            label.setMinSize(50,10);
            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.getChildren().addAll(label,colorPicker);
            pickersVbox.getChildren().add(hBox);
        }
    }

    @FXML
    void initialize() {
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) applyBtn.getScene().getWindow();
                stage.close();
            }
        });

        applyBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for(ColorPicker picker : colorPickers){
                    userColors.put(picker.getPromptText(), picker.getValue());
                    visualizeWindowController.recolor();
                    Stage stage = (Stage) applyBtn.getScene().getWindow();
                    stage.close();
                }
            }
        });
    }
}
