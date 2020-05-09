package Controllers;

import Client.Main;
import Storable.Route;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainWindowController {
    private TableTabController tableTabController;
    private CommandsTabController commandsTabController;

    public CommandsTabController getCommandsTabController() {
        return commandsTabController;
    }

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

            fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

            Parent commandsTabView = fxmlLoader.load(Main.class.getResource("/CommandsTab.fxml").openStream());
            commandsTabController = fxmlLoader.getController();
            commandTab.setContent(commandsTabView);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void updateTableView(ObservableList<Route> list){
        tableTabController.setupTable(list);
    }
    public void addTableViewItem(Route route){
        tableTabController.updateTable(route);
    }
    public void removeItems(ObservableList<Route> list){
        tableTabController.removeItems(list);
    }
}
