package Controllers;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import Storable.Route;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.CubicCurve;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class VisualizeWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private TextFlow routeInfo;

    @FXML
    private StackPane stackPane;

    private ObservableList<Route> items;

    @FXML
    void initialize() {
        CubicCurve cubicCurve = new CubicCurve();

        //Setting properties to cubic curve
        cubicCurve.setStartX(100.0f);
        cubicCurve.setStartY(150.0f);
        cubicCurve.setControlX1(400.0f);
        cubicCurve.setControlY1(40.0f);
        cubicCurve.setControlX2(175.0f);
        cubicCurve.setControlY2(250.0f);
        cubicCurve.setEndX(500.0f);
        cubicCurve.setEndY(150.0f);
        StackPane.setAlignment(cubicCurve, Pos.BOTTOM_CENTER);

        stackPane.getChildren().add(cubicCurve);
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

    }

    public void changeLanguage(Locale locale){
        resources = ResourceBundle.getBundle("MessagesBundle",locale);

    }
}

