package Controllers;

import Client.Main;
import CustomFilter.FilteredColumn;
import Storable.Coordinates;
import Storable.Location;
import Storable.Route;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TableTabController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private TableView<Route> tableView;

    private ObservableList<Route> initialItems;
    private TableColumn<Route, Long> idCol;
    private TableColumn<Route, String> nameCol;
    private TableColumn<Route, Coordinates> coordinatesCol;
    private TableColumn<Route, Coordinates> coordinatesXCol;
    private TableColumn<Route, Coordinates> coordinatesYCol;
    private TableColumn<Route, Date> creationDateCol;
    private TableColumn<Route, Location> fromCol;
    private TableColumn<Route, Location> fromXCol;
    private TableColumn<Route, Location> fromYCol;
    private TableColumn<Route, Location> fromNameCol;
    private TableColumn<Route, Location> toCol;
    private TableColumn<Route, Location> toXCol;
    private TableColumn<Route, Location> toYCol;
    private TableColumn<Route, Location> toNameCol;
    private TableColumn<Route, Double> distanceCol;
    private TableColumn<Route, String> ownerCol;

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
        creationDateCol.setMaxWidth(3000);
        fromXCol.setMaxWidth(3000);
        toXCol.setMaxWidth(3000);
        ownerCol.setMaxWidth(3000);

        creationDateCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        fromXCol.setCellValueFactory(new PropertyValueFactory<>("from"));
        toXCol.setCellValueFactory(new PropertyValueFactory<>("to"));
        fromYCol.setCellValueFactory(new PropertyValueFactory<>("from"));
        toYCol.setCellValueFactory(new PropertyValueFactory<>("to"));
        fromNameCol.setCellValueFactory(new PropertyValueFactory<>("from"));
        toNameCol.setCellValueFactory(new PropertyValueFactory<>("to"));
        distanceCol.setCellValueFactory(new PropertyValueFactory<>("distance"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        coordinatesXCol.setCellValueFactory(new PropertyValueFactory<>("coordinates"));
        coordinatesYCol.setCellValueFactory(new PropertyValueFactory<>("coordinates"));

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
                                stage.initModality(Modality.WINDOW_MODAL);
                                stage.initOwner(Main.stage.getScene().getWindow());
                                stage.setTitle(resources.getString("alerts.change"));
                                stage.setScene(new Scene(root));
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


        fromCol.getColumns().addAll(fromXCol,fromYCol,fromNameCol);
        toCol.getColumns().addAll(toXCol,toYCol,toNameCol);
        coordinatesCol.getColumns().addAll(coordinatesXCol,coordinatesYCol);
        tableView.getColumns().addAll(idCol,nameCol,coordinatesCol,creationDateCol,fromCol,toCol,distanceCol,ownerCol);
        tableView.setEditable(true);
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
        List<Route> list = initialItems.stream().filter(r->r.getId()==route.getId()).collect(Collectors.toList());
        if(list.size()==0){
            tableView.getItems().add(route);
            initialItems.add(route);
        }else{
            int i = 0;
            for(Route r : initialItems){
                if(r.getId()==route.getId()){
                    tableView.getItems().set(i,route);
                    initialItems.set(i,route);
                }
                i++;
            }
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
        formatColumnsData();
    }
    public void changeTheme(){

    }

    private void formatColumnsData(){
        coordinatesXCol.setCellFactory(column -> {
            TableCell<Route, Coordinates> cell = new TableCell<Route, Coordinates>() {
                private DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Coordinates item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item.getx()));
                }
            };
            return cell;
        });
        coordinatesYCol.setCellFactory(column -> {
            TableCell<Route, Coordinates> cell = new TableCell<Route, Coordinates>() {
                private DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Coordinates item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item.gety()));
                }
            };
            return cell;
        });

        fromXCol.setCellFactory(column -> {
            TableCell<Route, Location> cell = new TableCell<Route, Location>() {
                private NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Location item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item.getX()));
                }
            };
            return cell;
        });
        fromYCol.setCellFactory(column -> {
            TableCell<Route, Location> cell = new TableCell<Route, Location>() {
                private NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Location item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item.getY()));
                }
            };
            return cell;
        });
        fromNameCol.setCellFactory(column -> {
            TableCell<Route, Location> cell = new TableCell<Route, Location>() {
                @Override
                protected void updateItem(Location item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(item.getName());
                }
            };
            return cell;
        });

        toXCol.setCellFactory(column -> {
            TableCell<Route, Location> cell = new TableCell<Route, Location>() {
                private NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Location item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item.getX()));
                }
            };
            return cell;
        });
        toYCol.setCellFactory(column -> {
            TableCell<Route, Location> cell = new TableCell<Route, Location>() {
                private NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                @Override
                protected void updateItem(Location item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(format.format(item.getY()));
                }
            };
            return cell;
        });
        toNameCol.setCellFactory(column -> {
            TableCell<Route, Location> cell = new TableCell<Route, Location>() {
                @Override
                protected void updateItem(Location item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null);
                    else setText(item.getName());
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
