package CustomFilter;

import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import Client.Main;
import Controllers.TableTabController;
import Storable.Route;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FilterPopupController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox useCustomCheck;

    @FXML
    private ComboBox<String> filterTypeComboBox1;

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
    private ComboBox<String> filterTypeComboBox2;

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

    @FXML
    private Button sortAZ;

    @FXML
    private Button sortZA;

    private boolean useCustomFilters=false;
    private TableColumn<Route, ?> tableColumn;
    private TableTabController tableController;
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private ArrayList<Object> values = new ArrayList<>();
    private HashMap<Object, CheckBox> checkBoxHashMap = new HashMap<>();
    private EFilterTypes filterType;
    private Button button;
    private int firstComboIndex = -1;
    private int secondComboIndex = -1;
    ArrayList<String> options;

    public void setButton(Button button) {
        this.button = button;
    }

    public void setTableColumn(TableColumn<Route, ?> tableColumn) {
        this.tableColumn = tableColumn;
    }

    public void setTableController(TableTabController tableController) {
        this.tableController = tableController;
    }




    @FXML
    void initialize() {
        sortAZ.setOnAction(event -> {
            tableController.addSort(tableColumn,true);
            Stage stage = (Stage) applyBtn.getScene().getWindow();
            stage.close();
        });

        sortZA.setOnAction(event -> {
            tableController.addSort(tableColumn,false);
            Stage stage = (Stage) applyBtn.getScene().getWindow();
            stage.close();
        });

        options = new ArrayList<>(Arrays.asList(
                resources.getString("filter.lower"),
                resources.getString("filter.bigger"),
                resources.getString("filter.equal"),
                resources.getString("filter.notEqual"),
                resources.getString("filter.lowerEqual"),
                resources.getString("filter.biggerEqual"),
                resources.getString("filter.contains"),
                resources.getString("filter.notContains"),
                resources.getString("filter.starts"),
                resources.getString("filter.notStarts"),
                resources.getString("filter.ends"),
                resources.getString("filter.notEnds")));
        filterTypeComboBox1.setItems(FXCollections.observableArrayList(options));
        filterTypeComboBox2.setItems(FXCollections.observableArrayList(options));


        useCustomCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    useCustomFilters = true;
                    filterTypeComboBox1.setDisable(false);
                    filterTypeComboBox2.setDisable(false);
                    filterTypeValue1.setDisable(false);
                    filterTypeValue2.setDisable(false);
                    orSecondV.setDisable(false);
                    andSecondV.setDisable(false);
                    notUseSecondV.setDisable(false);
                } else {
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
                for (CheckBox checkBox : checkBoxes) {
                    checkBox.setSelected(newValue);
                }
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            dataVbox.getChildren().clear();
            for (CheckBox checkBox : checkBoxes) {
                if (checkBox.getText().matches(".*" + newValue + ".*")) dataVbox.getChildren().add(checkBox);
            }
        });

        applyBtn.setOnAction(event -> {
            List<Object> filteredList = checkBoxHashMap.entrySet().stream().filter(entry -> entry.getValue().isSelected()).map(Map.Entry::getKey).collect(Collectors.toList());
            Predicate predicate = null;
            if (!useCustomFilters) {
                switch (filterType) {
                    case STRING:
                    case NUMBER:
                        predicate = new Predicate() {
                            @Override
                            public boolean test(Object o) {
                                return filteredList.stream().map(Object::toString).noneMatch(str -> str.equals(o.toString()));
                            }
                        };
                        break;
                    case DATE:
                        predicate = new Predicate() {
                            @Override
                            public boolean test(Object o) {
                                return filteredList.stream().noneMatch(str -> str.equals(o));
                            }
                        };
                        break;
                }
            } else {
                filterTypeValue1.setBorder(Border.EMPTY);
                filterTypeValue2.setBorder(Border.EMPTY);
                if (filterType.equals(EFilterTypes.NUMBER)) {
                    try {
                        Double.parseDouble(filterTypeValue1.getText());
                    } catch (NumberFormatException e) {
                        addAlert(resources.getString("alerts.enterDec"));
                        filterTypeValue1.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    }
                    if (!notUseSecondV.isSelected()) {
                        try {
                            Double.parseDouble(filterTypeValue2.getText());
                        } catch (NumberFormatException e) {
                            addAlert(resources.getString("alerts.enterDec"));
                            filterTypeValue2.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        }
                    }
                }
                String value = new String(filterTypeValue1.getText());
                int op = options.indexOf(filterTypeComboBox1.getSelectionModel().getSelectedItem());
                Predicate predicate1 = getPredicate(op, value);
                if (!notUseSecondV.isSelected()) {
                    String value2 = new String(filterTypeValue2.getText());
                    int op2 = options.indexOf(filterTypeComboBox2.getSelectionModel().getSelectedItem());
                    Predicate predicate2 = getPredicate(op2, value2);
                    if (orSecondV.isSelected()) {
                        predicate = new Predicate() {
                            @Override
                            public boolean test(Object o) {
                                return (!predicate1.test(o) && !predicate2.test(o)) || filteredList.stream().map(Object::toString).noneMatch(str -> str.equals(o.toString()));
                            }
                        };
                    } else if (andSecondV.isSelected()) {
                        predicate = o -> (!predicate1.test(o) || !predicate2.test(o)) || filteredList.stream().map(Object::toString).noneMatch(str -> str.equals(o.toString()));
                    }
                } else
                    predicate = o -> !predicate1.test(o) || filteredList.stream().map(Object::toString).noneMatch(str -> str.equals(o.toString()));

            }
            tableController.addFilter(tableColumn, predicate);
            button.setStyle("-fx-background-image: url('/filter-icon-active.png'); -fx-background-size: 15px; -fx-background-repeat: no-repeat; -fx-background-position: 50%; -fx-background-color: transparent; -fx-border-color: transparent;");
            Stage stage = (Stage) applyBtn.getScene().getWindow();
            stage.close();
        });

        cancelBtn.setOnAction(event -> {
            Stage stage = (Stage) applyBtn.getScene().getWindow();
            stage.close();
        });
        removeFiltersBtn.setOnAction(event -> {
            tableController.deleteFilter(tableColumn);
            button.setStyle("-fx-background-image: url('/filter-icon.png'); -fx-background-size: 15px; -fx-background-repeat: no-repeat; -fx-background-position: 50%; -fx-background-color: transparent; -fx-border-color: transparent;");
            Stage stage = (Stage) applyBtn.getScene().getWindow();
            stage.close();
        });
    }

    private Predicate getPredicate(int op, String value){
        Predicate predicate = null;
        if(filterType.equals(EFilterTypes.NUMBER)) {
            switch (op) {
                case 0:
                    predicate = o -> Double.compare(Double.parseDouble(value), Double.parseDouble(o.toString())) > 0;
                    break;
                case 1:
                    predicate = o -> Double.compare(Double.parseDouble(value), Double.parseDouble(o.toString())) < 0;
                    break;
                case 4:
                    predicate = o -> Double.compare(Double.parseDouble(value), Double.parseDouble(o.toString())) >= 0;
                    break;
                case 5:
                    predicate = o -> Double.compare(Double.parseDouble(value), Double.parseDouble(o.toString())) <= 0;
                    break;
            }
        }else{
            switch (op) {
                case 0:
                    predicate = o -> o.toString().compareTo(value)<0;
                    break;
                case 1:
                    predicate = o -> o.toString().compareTo(value)>0;
                    break;
                case 4:
                    predicate = o -> o.toString().compareTo(value)<=0;
                    break;
                case 5:
                    predicate = o -> o.toString().compareTo(value)>=0;
                    break;
            }
        }
        switch (op){
            case 2:
                predicate = o -> o.toString().equals(value);
                break;
            case 3:
                predicate = o -> !o.toString().equals(value);
                break;
            case 6:
                predicate = o -> o.toString().matches(".*"+value+".*");
                break;
            case 7:
                predicate = o -> !o.toString().matches(".*"+value+".*");
                break;
            case 8:
                predicate = o -> o.toString().matches(value+".*");
                break;
            case 9:
                predicate = o -> !o.toString().matches(value+".*");
                break;
            case 10:
                predicate = o -> o.toString().matches(".*"+value);
                break;
            case 11:
                predicate = o -> !o.toString().matches(".*"+value);
                break;
        }
        return predicate;
    }

    private void addAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setResizable(false);
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void addCheckBoxes(Set<Object> set, Set<Object> selected) {
        if(set.size()>0) {
            Object o = set.iterator().next();
            if(o instanceof Number) filterType = EFilterTypes.NUMBER;
            else if(o instanceof Date) filterType = EFilterTypes.DATE;
            else filterType = EFilterTypes.STRING;
            for (Object s : set) {
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(selected.stream().anyMatch(e->e.toString().equals(s.toString())));
                switch (filterType){
                    case STRING:
                        checkBox.setText(s.toString());
                        break;
                    case NUMBER:
                        checkBox.setText(NumberFormat.getInstance().format(s));
                        break;
                    case DATE:
                        checkBox.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(s));
                        break;
                }
                checkBoxes.add(checkBox);
                checkBoxHashMap.put(s, checkBox);
                dataVbox.getChildren().add(checkBox);
            }
            values= new ArrayList<>(set);
        }
    }
}
