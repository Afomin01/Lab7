package Controllers;

import Client.Main;
import Commands.GetTableItems;
import Storable.Route;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainWindowController {
    TableTabController tableTabController;
    @FXML
    private ResourceBundle resources;

    @FXML
    private Tab tableTab;

    @FXML
    private Tab visualizationTab;

    @FXML
    private Tab commandTab;

    @FXML
    void initialize() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

            Parent tableTabView = fxmlLoader.load(Main.class.getResource("/TableTab.fxml").openStream());
            tableTabController = fxmlLoader.getController();
            tableTab.setContent(tableTabView);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void updateTableView(ObservableList<Route> list){
        tableTabController.updateTable(list);
    }
}
