package CustomFilter;

import Client.Main;
import Storable.Route;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.stage.Popup;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class FilteredColumn<T> extends TableColumn<Route, T> {

/*    ObservableList<Route> itemsBeforeFilter;
    public FilteredColumn(){
        super();
        addBtn();
    }

    public FilteredColumn(String text, ObservableList<Route> list){
        super(text);
        addBtn();
        itemsBeforeFilter = list;
    }

    private void addBtn(){
        Button button = new Button();
        button.setPadding(new Insets(12));
        button.setStyle("-fx-background-image: url('/filter-icon.png'); -fx-background-size: 15px; -fx-background-repeat: no-repeat; -fx-background-position: 50%; -fx-background-color: transparent;");
        button.setCursor(Cursor.HAND);
        setGraphic(button);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle", Locale.getDefault()));

                    Parent root = null;
                    root = fxmlLoader.load(Main.class.getResource("/FilterPopup.fxml").openStream());
                    FilterPopupController filterPopupController = fxmlLoader.getController();
                    filterPopupController.setList(itemsBeforeFilter);
                    T t = null;
                    filterPopupController.setT(t);
                    filterPopupController.setTableView(getTableView());
                    for(long i=0; i< getTableView().getItems().stream().count();i++){
                        filterPopupController.addText(getCellData((int)i).toString(),FilteredColumn.this);
                    }

                    Popup popup = new Popup();
                    popup.getScene().setRoot(root);
                    popup.setAutoHide(true);
                    popup.show(getTableView().getScene().getWindow());
                } catch (IOException e) {
                }
            }

        });
    }*/
}
