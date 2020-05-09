package Controllers;

import Storable.Coordinates;
import Storable.Location;
import Storable.Route;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class TableTabController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private TableView<Route> tableView;

    @FXML
    private Button visializeBtn;

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
        ImageView imageView = new ImageView("/filter-icon.png");
        imageView.setScaleX(0.35);
        imageView.setScaleY(0.35);
        ownerCol.setGraphic(imageView);

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

        fromCol.getColumns().addAll(fromXCol,fromYCol,fromNameCol);
        toCol.getColumns().addAll(toXCol,toYCol,toNameCol);
        coordinatesCol.getColumns().addAll(coordinatesXCol,coordinatesYCol);
        tableView.getColumns().addAll(idCol,nameCol,coordinatesCol,creationDateCol,fromCol,toCol,distanceCol,ownerCol);
    }
    public void updateTable(ObservableList<Route> list){
        tableView.setItems(list);
    }
}
