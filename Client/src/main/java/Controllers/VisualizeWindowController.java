package Controllers;

import Storable.Route;
import javafx.animation.PathTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VisualizeWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane anchor;

    @FXML
    private TextFlow routeInfo;


    private ObservableList<Route> items;

    @FXML
    void initialize() {

    }

    public void setUpVisual(ObservableList<Route> list){
        items=list;
        for(Route r : items){
            drawObject(r);
        }
    }
    public void addItem(Route route){
        List<Route> list = items.stream().filter(r->r.getId()==route.getId()).collect(Collectors.toList());
        if(list.size()==0){
            items.add(route);
            drawObject(route);
        }else{
            int i = 0;
            for(Route r : items){
                if(r.getId()==route.getId()){
                    items.set(i,route);
                }
                i++;
            }
        }
        for(Route r : items){
            drawObject(r);
        }
    }

    private void drawObject(Route route){
        AnchorPane anchorPane = new AnchorPane();
        Button button = new Button("sadglkj");
    }

    public void changeLanguage(Locale locale){
        resources = ResourceBundle.getBundle("MessagesBundle",locale);

    }
}

