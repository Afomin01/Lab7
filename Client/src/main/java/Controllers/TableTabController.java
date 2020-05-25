package Controllers;

import Client.Main;
import Client.Utils.EThemes;
import Commands.RemoveObject;
import CustomFilter.FilterPopupController;
import Instruments.ClientRequest;
import Storable.Coordinates;
import Storable.Location;
import Storable.Route;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class TableTabController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private TableView<Route> tableView;

    @FXML
    private TextField removeIDFDield;

    @FXML
    private Button removeBtn;

    @FXML
    private Button delFilters;

    @FXML
    private Button delSorts;

    private ObservableList<Route> initialItems;
    private TableColumn<Route, Long> idCol;
    private TableColumn<Route, String> nameCol;
    private TableColumn<Route, Coordinates> coordinatesCol;
    private TableColumn<Route, Double> coordinatesXCol;
    private TableColumn<Route, Double> coordinatesYCol;
    private TableColumn<Route, Date> creationDateCol;
    private TableColumn<Route, Location> fromCol;
    private TableColumn<Route, Integer> fromXCol;
    private TableColumn<Route, Long> fromYCol;
    private TableColumn<Route, String> fromNameCol;
    private TableColumn<Route, Location> toCol;
    private TableColumn<Route, Integer> toXCol;
    private TableColumn<Route, Long> toYCol;
    private TableColumn<Route, String> toNameCol;
    private TableColumn<Route, Double> distanceCol;
    private TableColumn<Route, String> ownerCol;
    private ObservableMap<TableColumn, Predicate> filtersMap = FXCollections.observableMap(new HashMap<>());

    @FXML
    void initialize() {
        idCol = new TableColumn<>(resources.getString("table.id"));
        nameCol = new TableColumn<>(resources.getString("table.name"));
        coordinatesCol = new TableColumn<>(resources.getString("table.coordinates"));
        coordinatesXCol = new TableColumn<>("x");
        coordinatesYCol = new TableColumn<>("y");
        creationDateCol = new TableColumn<>(resources.getString("table.creationDate"));
        fromCol = new TableColumn<>(resources.getString("table.from"));
        fromXCol = new TableColumn<>("x");
        fromYCol = new TableColumn<>("y");
        fromNameCol = new TableColumn<>(resources.getString("table.name"));
        toCol = new TableColumn<>(resources.getString("table.to"));
        toXCol = new TableColumn<>("x");
        toYCol = new TableColumn<>("y");
        toNameCol = new TableColumn<>(resources.getString("table.name"));
        distanceCol = new TableColumn<>(resources.getString("table.distance"));
        ownerCol = new TableColumn<>(resources.getString("table.owner"));

        idCol.setMaxWidth(1500);
        creationDateCol.setMaxWidth(5000);
        fromXCol.setMaxWidth(3000);
        toXCol.setMaxWidth(3000);
        ownerCol.setMaxWidth(3000);

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        creationDateCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        coordinatesXCol.setCellValueFactory(cell-> new SimpleDoubleProperty(cell.getValue().getCoordinates().getx()).asObject());
        coordinatesYCol.setCellValueFactory(cell-> new SimpleDoubleProperty(cell.getValue().getCoordinates().gety()).asObject());
        fromXCol.setCellValueFactory(cell-> new SimpleIntegerProperty(cell.getValue().getFrom().getX()).asObject());
        fromYCol.setCellValueFactory(cell-> new SimpleLongProperty(cell.getValue().getFrom().getY()).asObject());
        fromNameCol.setCellValueFactory(cell-> new SimpleStringProperty(cell.getValue().getFrom().getName()));
        toNameCol.setCellValueFactory(cell-> new SimpleStringProperty(cell.getValue().getFrom().getName()));
        toXCol.setCellValueFactory(cell-> new SimpleIntegerProperty(cell.getValue().getTo().getX()).asObject());
        toYCol.setCellValueFactory(cell-> new SimpleLongProperty(cell.getValue().getTo().getY()).asObject());
        distanceCol.setCellValueFactory(cell-> new SimpleDoubleProperty(cell.getValue().getDistance()).asObject());
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));

        removeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    Long id = Long.parseLong(removeIDFDield.getText());
                    Main.handler.sendRequest(new ClientRequest(new RemoveObject(id,Main.login),Main.login,Main.password));
                }catch (NumberFormatException e){
                    MessageFormat incorrectNum = new MessageFormat(resources.getString("console.incorrectIn"));
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert1.setResizable(false);
                    alert1.setContentText(incorrectNum.format(new Object[]{resources.getString("types.Long")}));
                    alert1.showAndWait();
                }
            }
        });

        delFilters.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                filtersMap.clear();
                reFiltrate();
            }
        });
        delSorts.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tableView.getSortOrder().clear();
            }
        });
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        try {
                            if(Main.login.equals(tableView.getSelectionModel().getSelectedItem().getOwner())) {
                                FXMLLoader fxmlLoader = new FXMLLoader();
                                fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle"));

                                Parent root = fxmlLoader.load(Main.class.getResource("/EditWindow.fxml").openStream());
                                EditWindowController controller = fxmlLoader.getController();

                                Stage stage = new Stage();
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.initModality(Modality.WINDOW_MODAL);
                                stage.initOwner(Main.stage.getScene().getWindow());
                                stage.setTitle(resources.getString("alerts.change"));
                                Scene scene = new Scene(root);
                                scene.getStylesheets().clear();
                                EThemes themes = EThemes.valueOf(Preferences.userRoot().get("theme","default"));
                                if(themes.file!=null) scene.getStylesheets().add(themes.file);

                                stage.setScene(scene);
                                controller.setFields(tableView.getSelectionModel().getSelectedItem());
                                stage.show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        formatColumnsData();

        addBtn(idCol);
        addBtn(nameCol);
        addBtn(coordinatesXCol);
        addBtn(coordinatesYCol);
        addBtn(creationDateCol);
        addBtn(fromXCol);
        addBtn(fromYCol);
        addBtn(fromNameCol);
        addBtn(toXCol);
        addBtn(toYCol);
        addBtn(toNameCol);
        addBtn(distanceCol);
        addBtn(ownerCol);

        fromCol.getColumns().addAll(fromXCol,fromYCol,fromNameCol);
        toCol.getColumns().addAll(toXCol,toYCol,toNameCol);
        coordinatesCol.getColumns().addAll(coordinatesXCol,coordinatesYCol);
        tableView.getColumns().addAll(idCol,nameCol,coordinatesCol,creationDateCol,fromCol,toCol,distanceCol,ownerCol);
        tableView.setEditable(true);
    }
    public void addSort(TableColumn tableColumn, boolean sortAZ){
        if(sortAZ) tableColumn.setSortType(TableColumn.SortType.ASCENDING);
        else tableColumn.setSortType(TableColumn.SortType.DESCENDING);
        tableView.getSortOrder().remove(tableColumn);
        tableView.getSortOrder().add(tableColumn);
    }
    public void addFilter(TableColumn tableColumn, Predicate<String> predicate){
        filtersMap.put(tableColumn,predicate);
        reFiltrate();
    }
    public void deleteFilter(TableColumn tableColumn){
        filtersMap.remove(tableColumn);
        reFiltrate();
    }
    private void reFiltrate(){
        tableView.setItems(FXCollections.observableArrayList(initialItems));

        Iterator<Map.Entry<TableColumn, Predicate>> it = filtersMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<TableColumn, Predicate> pair = it.next();
            tableView.getItems().removeAll(tableView.getItems().stream().filter(r->pair.getValue().test(pair.getKey().getCellObservableValue(r).getValue())).collect(Collectors.toList()));
        }
    }
    private void addBtn(TableColumn tableColumn){
        Button button = new Button();
        button.setPadding(new Insets(12));
        button.setStyle("-fx-background-image: url('/filter-icon.png'); -fx-background-size: 15px; -fx-background-repeat: no-repeat; -fx-background-position: 50%; -fx-background-color: transparent; -fx-border-color: transparent;");
        button.setCursor(Cursor.HAND);
        tableColumn.setGraphic(button);
        filtersMap.addListener(new MapChangeListener<TableColumn, Predicate>() {
            @Override
            public void onChanged(Change<? extends TableColumn, ? extends Predicate> change) {
                if(filtersMap.size()==0) button.setStyle("-fx-background-image: url('/filter-icon.png'); -fx-background-size: 15px; -fx-background-repeat: no-repeat; -fx-background-position: 50%; -fx-background-color: transparent; -fx-border-color: transparent;");
            }
        });
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

                    Parent root = fxmlLoader.load(Main.class.getResource("/FilterPopup.fxml").openStream());
                    FilterPopupController filterPopupController = fxmlLoader.getController();
                    filterPopupController.setTableController(TableTabController.this);
                    HashSet<Object> values = new HashSet<>();
                    HashSet<Object> selectedValues = new HashSet<>();
                    for(long i=0; i< initialItems.size();i++){
                        values.add(tableColumn.getCellObservableValue(initialItems.get((int)i)).getValue());
                    }
                    for(long i=0; i< tableColumn.getTableView().getItems().size();i++){
                        selectedValues.add(tableColumn.getCellData((int)i));
                    }
                    filterPopupController.addCheckBoxes(values, selectedValues);
                    filterPopupController.setTableColumn(tableColumn);
                    filterPopupController.setButton(button);

                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(Main.stage.getScene().getWindow());
                    Scene scene = new Scene(root);
                    scene.getStylesheets().clear();
                    EThemes themes = EThemes.valueOf(Preferences.userRoot().get("theme","default"));
                    if(themes.file!=null) scene.getStylesheets().add(themes.file);

                    stage.setScene(scene);
                    stage.show();
                }catch (IOException e){}
            }
        });
    }
    public void setupTable(ObservableList<Route> list){
        tableView.setItems(list);
        initialItems=list;
    }
    public void removeItems(ObservableList<Route> list){
        list.forEach(t->tableView.getItems().removeIf(r->r.getId()==t.getId()));
        list.forEach(t->initialItems.removeIf(r->r.getId()==t.getId()));
    }
    public void updateTable(Route route){
        try {
            if (initialItems.stream().noneMatch(r -> r.getId() == route.getId())) {
                initialItems.add(route);
            } else {
                int i = 0;
                for (Route r : initialItems) {
                    if (r.getId() == route.getId()) {
                        tableView.getItems().set(i, route);
                        initialItems.set(i, route);
                    }
                    i++;
                }
            }
            reFiltrate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void changeLanguage(Locale locale){
        resources = ResourceBundle.getBundle("MessagesBundle",locale);
        idCol.setText(resources.getString("table.id"));
        nameCol.setText(resources.getString("table.name"));
        coordinatesCol.setText(resources.getString("table.coordinates"));
        fromCol.setText(resources.getString("table.from"));
        toCol.setText(resources.getString("table.to"));
        distanceCol.setText(resources.getString("table.distance"));
        fromNameCol.setText(resources.getString("table.name"));
        toNameCol.setText(resources.getString("table.name"));
        ownerCol.setText(resources.getString("table.owner"));
        creationDateCol.setText(resources.getString("table.creationDate"));
        tableView.refresh();
    }
    private void formatColumnsData(){
        coordinatesXCol.setCellFactory(column -> {
            TableCell<Route, Double> cell = new TableCell<Route, Double>() {
                private DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item));
                }
            };
            return cell;
        });
        coordinatesYCol.setCellFactory(column -> {
            TableCell<Route, Double> cell = new TableCell<Route, Double>() {
                private DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item));
                }
            };
            return cell;
        });

        fromXCol.setCellFactory(column -> {
            TableCell<Route, Integer> cell = new TableCell<Route, Integer>() {
                private NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item));
                }
            };
            return cell;
        });
        fromYCol.setCellFactory(column -> {
            TableCell<Route, Long> cell = new TableCell<Route, Long>() {
                private NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item));
                }
            };
            return cell;
        });

        toXCol.setCellFactory(column -> {
            TableCell<Route, Integer> cell = new TableCell<Route, Integer>() {
                private NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item));
                }
            };
            return cell;
        });
        toYCol.setCellFactory(column -> {
            TableCell<Route, Long> cell = new TableCell<Route, Long>() {
                private NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item));
                }
            };
            return cell;
        });

        creationDateCol.setCellFactory(column -> {
            TableCell<Route, Date> cell = new TableCell<Route, Date>() {
                private DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item));
                }
            };
            return cell;
        });

        distanceCol.setCellFactory(column -> {
            TableCell<Route, Double> cell = new TableCell<Route, Double>() {
                private DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item));
                }
            };
            return cell;
        });
    }
}
