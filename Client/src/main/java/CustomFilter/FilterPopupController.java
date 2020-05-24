package CustomFilter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import Controllers.TableTabController;
import Storable.Route;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FilterPopupController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox useCustomCheck;

    @FXML
    private ComboBox<?> filterTypeComboBox1;

    @FXML
    private TextField filterTypeValue1;

    @FXML
    private RadioButton notUseSecondV;

    @FXML
    private ToggleGroup group;

    @FXML
    private RadioButton andSecondV;

    @FXML
    private RadioButton orSecondV;

    @FXML
    private ComboBox<?> filterTypeComboBox2;

    @FXML
    private TextField filterTypeValue2;

    @FXML
    private TextField searchField;

    @FXML
    private VBox dataVbox;

    @FXML
    private Button applyBtn;

    @FXML
    private Button removeFiltersBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private CheckBox allChecker;


    private boolean useCustomFilters=false;
    private TableColumn<Route, ?> tableColumn;
    private TableTabController tableController;
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    public void setTableColumn(TableColumn<Route, ?> tableColumn) {
        this.tableColumn = tableColumn;
    }

    public void setTableController(TableTabController tableController) {
        this.tableController = tableController;
    }




    @FXML
    void initialize() {
        useCustomCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    useCustomFilters = true;
                    filterTypeComboBox1.setDisable(false);
                    filterTypeComboBox2.setDisable(false);
                    filterTypeValue1.setDisable(false);
                    filterTypeValue2.setDisable(false);
                    orSecondV.setDisable(false);
                    andSecondV.setDisable(false);
                    notUseSecondV.setDisable(false);
                }else{
                    useCustomFilters = false;
                    filterTypeComboBox1.setDisable(true);
                    filterTypeComboBox2.setDisable(true);
                    filterTypeValue1.setDisable(true);
                    filterTypeValue2.setDisable(true);
                    orSecondV.setDisable(true);
                    andSecondV.setDisable(true);
                    notUseSecondV.setDisable(true);
                }
            }
        });

        allChecker.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                for(CheckBox checkBox : checkBoxes){
                    checkBox.setSelected(newValue);
                }
            }
        });

        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                dataVbox.getChildren().clear();
                for(CheckBox checkBox : checkBoxes){
                    if(checkBox.getText().matches(".*"+newValue+".*")) dataVbox.getChildren().add(checkBox);
                }
            }
        });

        applyBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                List<String> checked = checkBoxes.stream().filter(CheckBox::isSelected).map(Labeled::getText).collect(Collectors.toList());
                Predicate<String> predicate = new Predicate() {
                    @Override
                    public boolean test(Object o) {
                        String s = (String) o;
                        if(checked.stream().anyMatch(str->str.equals(s))) return false;
                        else return true;
                    }
                };
                tableController.addFilter(tableColumn, predicate);
                Stage stage = (Stage) applyBtn.getScene().getWindow();
                stage.close();
            }
        });

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) applyBtn.getScene().getWindow();
                stage.close();
            }
        });
        removeFiltersBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tableController.deleteFilter(tableColumn);
                Stage stage = (Stage) applyBtn.getScene().getWindow();
                stage.close();
            }
        });
    }

    public void addCheckBoxes(Set<String> set) {
        for(String s : set) {
            CheckBox checkBox = new CheckBox(s);
            checkBox.setSelected(true);
            checkBoxes.add(checkBox);
            dataVbox.getChildren().add(checkBox);
        }
    }
}
