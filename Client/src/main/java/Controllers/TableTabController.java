package Controllers;

import Client.Main;
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

    @FXML
    void initialize() {
        TableColumn<Route, Long> idCol = new TableColumn<>(resources.getString("table.id"));
        idCol.setMaxWidth(1500);
        TableColumn<Route, String> nameCol = new TableColumn<>(resources.getString("table.name"));
        TableColumn<Route, Coordinates> coordinatesCol = new TableColumn<>(resources.getString("table.coordinates"));
        TableColumn<Route, Coordinates> coordinatesXCol = new TableColumn<>("x");
        TableColumn<Route, Coordinates> coordinatesYCol = new TableColumn<>("y");
        TableColumn<Route, Date> creationDateCol = new TableColumn<>(resources.getString("table.creationDate"));
        creationDateCol.setMaxWidth(3000);
        TableColumn<Route, Location> fromCol = new TableColumn<>(resources.getString("table.from"));
        TableColumn<Route, Location> fromXCol = new TableColumn<>("x");
        fromXCol.setMaxWidth(3000);
        TableColumn<Route, Location> fromYCol = new TableColumn<>("y");
        TableColumn<Route, Location> fromNameCol = new TableColumn<>(resources.getString("table.name"));
        TableColumn<Route, Location> toCol = new TableColumn<>(resources.getString("table.to"));
        TableColumn<Route, Location> toXCol = new TableColumn<>("x");
        toXCol.setMaxWidth(3000);
        TableColumn<Route, Location> toYCol = new TableColumn<>("y");
        TableColumn<Route, Location> toNameCol = new TableColumn<>(resources.getString("table.name"));
        TableColumn<Route, Double> distanceCol = new TableColumn<>(resources.getString("table.distance"));
        TableColumn<Route, String> ownerCol = new TableColumn<>(resources.getString("table.owner"));
        ownerCol.setMaxWidth(3000);
        Button button = new Button();
        button.setPadding(new Insets(12));
        button.setStyle("-fx-background-image: url('/filter-icon.png'); -fx-background-size: 15px; -fx-background-repeat: no-repeat; -fx-background-position: 50%; -fx-background-color: transparent;");
        button.setCursor(Cursor.HAND);
        ownerCol.setGraphic(button);

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


        fromCol.getColumns().addAll(fromXCol,fromYCol,fromNameCol);
        toCol.getColumns().addAll(toXCol,toYCol,toNameCol);
        coordinatesCol.getColumns().addAll(coordinatesXCol,coordinatesYCol);
        tableView.getColumns().addAll(idCol,nameCol,coordinatesCol,creationDateCol,fromCol,toCol,distanceCol,ownerCol);
        tableView.setEditable(true);
    }
    public void setupTable(ObservableList<Route> list){
        tableView.setItems(list);
    }
    public void removeItems(ObservableList<Route> list){
        list.forEach(t->tableView.getItems().removeIf(r->r.getId()==t.getId()));
    }
    public void updateTable(Route route){
        List<Route> list = tableView.getItems().stream().filter(r->r.getId()==route.getId()).collect(Collectors.toList());
        if(list.size()==0){
            tableView.getItems().add(route);
        }else{
            int i = 0;
            for(Route r : tableView.getItems()){
                if(r.getId()==route.getId()){
                    tableView.getItems().set(i,route);
                }
                i++;
            }
        }
    }
}
