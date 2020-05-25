package Controllers;

import Client.Main;
import Client.Utils.EThemes;
import Commands.Exit;
import Instruments.ClientRequest;
import Storable.Route;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class MainWindowController {
    private TableTabController tableTabController;
    private CommandsTabController commandsTabController;
    private VisualizeWindowController visualizeWindowController;

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
    private MenuItem menuSettings;

    @FXML
    private MenuItem menuExit;

    @FXML
    private MenuItem menuHelp;

    @FXML
    private Label userNameText;
    @FXML
    private MenuItem menuClose;

    @FXML
    void initialize() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

            userNameText.setText(Main.login);

            Parent tableTabView = fxmlLoader.load(Main.class.getResource("/TableTab.fxml").openStream());
            tableTabController = fxmlLoader.getController();
            tableTab.setContent(tableTabView);

            fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

            Parent commandsTabView = fxmlLoader.load(Main.class.getResource("/CommandsTab.fxml").openStream());
            commandsTabController = fxmlLoader.getController();
            commandTab.setContent(commandsTabView);

            fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

            Parent visualizeTabView = fxmlLoader.load(Main.class.getResource("/VisualizeWindow.fxml").openStream());
            visualizeWindowController = fxmlLoader.getController();
            visualizationTab.setContent(visualizeTabView);

            menuClose.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.exit(0);
                }
            });

            menuSettings.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        Stage stage = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setResources(resources);
                        Parent settings = fxmlLoader.load(Main.class.getResource("/SettingsWindow.fxml").openStream());
                        SettingsWindowController controller = fxmlLoader.getController();
                        controller.setMainWindowController(MainWindowController.this);

                        Scene scene = new Scene(settings);
                        EThemes themes = EThemes.valueOf(Preferences.userRoot().get("theme","default"));
                        if(themes.file!=null) scene.getStylesheets().add(themes.file);

                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(Main.stage.getScene().getWindow());
                        stage.setScene(scene);
                        stage.setTitle(resources.getString("menu.settings"));
                        stage.show();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            menuExit.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Main.handler.sendRequest(new ClientRequest(new Exit(Main.login), Main.login,Main.password));
                }
            });
        }catch (Exception e){
            System.exit(1);
        }

    }

    public void updateTableView(ObservableList<Route> list){
        tableTabController.setupTable(list);
        visualizeWindowController.setUpVisual(list);
    }
    public void addTableViewItem(Route route){
        tableTabController.updateTable(route);
        visualizeWindowController.addItem(route);
    }
    public void removeItems(ObservableList<Route> list){
        tableTabController.removeItems(list);
    }
    public void changeLanguage(Locale locale){
        Locale.setDefault(locale);
        resources = ResourceBundle.getBundle("MessagesBundle", Locale.getDefault());
        menuSettings.setText(resources.getString("menu.settings"));
        menuExit.setText(resources.getString("alerts.exit"));
        menuHelp.setText(resources.getString("menu.help"));
        menuClose.setText(resources.getString("menu.close"));
        tableTab.setText(resources.getString("tab.table"));
        visualizationTab.setText(resources.getString("tab.visual"));
        commandTab.setText(resources.getString("tab.console"));
        tableTabController.changeLanguage(locale);
        visualizeWindowController.changeLanguage(locale);
        commandsTabController.changeLanguage(locale);
    }
    public void changeTheme(EThemes themes){
        Preferences preferences = Preferences.userRoot();
        preferences.put("theme",themes.toString());
        userNameText.getScene().getStylesheets().clear();
        if(themes.file!=null) userNameText.getScene().getStylesheets().add(themes.file);
    }
}
