package Controllers;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import Storable.Route;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class VisualizeWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private TextFlow routeInfo;

    @FXML
    private Canvas canvas;

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
        canvas.getGraphicsContext2D().clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        for(Route r : items){
            drawObject(r);
        }
    }

    private void drawObject(Route route){
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        double w = (canvas.getWidth()-10.0)/(Integer.MAX_VALUE*2.0);
        double h = (canvas.getHeight()-10.0)/(Integer.MAX_VALUE*2.0);
        double wl = (canvas.getWidth()-10.0)/(Long.MAX_VALUE*2.0);
        double hl = (canvas.getHeight()-10.0)/(Long.MAX_VALUE*2.0);
        int startX = (int) (route.getFrom().getX()*w+((canvas.getWidth()-10.0)/2)+Math.random()*10.0);
        int startY = (int) (route.getFrom().getY()*hl+((canvas.getHeight()-10.0)/2)+Math.random()*10.0);
        int endX = (int) (route.getTo().getX()*w+((canvas.getWidth()-10.0)/2)+Math.random()*10.0);
        int endY = (int) (route.getTo().getY()*hl+((canvas.getHeight()-10.0)/2)+Math.random()*10.0);
        System.out.println(startX +" "+startY +" "+endX +" "+endY);
        if(Math.abs(startX*2)<canvas.getWidth()) startX*=2;
        if(Math.abs(startY*2)<canvas.getHeight()) startY*=2;
        if(Math.abs(endX*2)<canvas.getWidth()) endX*=2;
        if(Math.abs(endY*2)<canvas.getHeight()) endY*=2;

        graphicsContext.beginPath();
        graphicsContext.moveTo(startX,startY);
        graphicsContext.lineTo(endX,endY);
        graphicsContext.stroke();
    }

    public void changeLanguage(Locale locale){
        resources = ResourceBundle.getBundle("MessagesBundle",locale);

    }
}

