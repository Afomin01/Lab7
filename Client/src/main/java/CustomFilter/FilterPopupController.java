package CustomFilter;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Storable.Route;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.jws.Oneway;

public class FilterPopupController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox dataVbox;

    @FXML
    private Button applyBtn;

    @FXML
    private Button removeFiltersBtn;
    private ObservableList<Route> list;
    private TableView tableView;
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private Object t;

    public void setT(Object t) {
        this.t = t;
    }

    public void setList(ObservableList<Route> list) {
        this.list = list;
    }

    public void setTableView(TableView tableView) {
        this.tableView = tableView;
    }

    @FXML
    void initialize() {
        removeFiltersBtn.setOnAction(event -> tableView.setItems(list));
        applyBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
    }
    public void addText(String string, TableColumn column){
        CheckBox checkBox = new CheckBox(string);
        checkBox.setSelected(true);
        checkBoxes.add(checkBox);
        dataVbox.getChildren().add(checkBox);
    }
}
