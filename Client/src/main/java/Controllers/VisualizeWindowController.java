package Controllers;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import Client.Main;
import Storable.Location;
import Storable.Route;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class VisualizeWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane anchor;

    @FXML
    private TextFlow routeInfo;


    private ObservableList<Route> items;

    private int sizeX = 1000;
    private int sizeY = 500;

    @FXML

    void initialize() {
        try {
        Route route = new Route();

        Path path = new Path();
        path.setStrokeWidth(4.0);

        CubicCurveTo cubicCurveTo = new CubicCurveTo();

//        Location to = route.getTo();
//        Location from = route.getFrom();

        MoveTo moveTo = new MoveTo();
        //moveTo.setX(from.getX());
        //moveTo.setY(from.getY());

        moveTo.setX(460.0f);
        moveTo.setY(300.0f);

        //int minX = Math.min(to.getX(), from.getX());
        //int maxX = Math.max(to.getX(), from.getX());

        //long minY = Math.min(to.getY(), from.getY());
        //long maxY = Math.max(to.getY(), from.getY());

        //cubicCurveTo.setControlX1((Math.random() * ((maxX - minX) + 1)) + minX);
        //cubicCurveTo.setControlY1((Math.random() * ((maxY - minY) + 1)) + minY);
        //cubicCurveTo.setControlX2((Math.random() * ((maxX - minX) + 1)) + minX);
        //cubicCurveTo.setControlY2((Math.random() * ((maxY - minY) + 1)) + minY);

        cubicCurveTo.setControlX1(200.0f);
        cubicCurveTo.setControlX2(322.2f);
        cubicCurveTo.setControlY1(50.0f);
        cubicCurveTo.setControlY2(600.0f);


        //cubicCurveTo.setX(to.getX());
        //cubicCurveTo.setY(to.getY());

        cubicCurveTo.setX(355.5f);
        cubicCurveTo.setY(40.0f);

        path.getElements().add(moveTo);
        path.getElements().add(cubicCurveTo);

        AnchorPane.setLeftAnchor(path, 15.0);

        anchor.getChildren().add(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUpVisual(ObservableList<Route> list){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                items=list;
                for(Route r : items){
                    drawObject(r);
                }
            }
        });

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
        Path path = new Path();
        path.setStrokeWidth(4.0);

        CubicCurveTo cubicCurveTo = new CubicCurveTo();

        Location to = route.getTo();
        Location from = route.getFrom();

        MoveTo moveTo = new MoveTo();


        int toX = (int) (((to.getX() / Math.pow(10, Math.max(0, (int)(Math.log(to.getX())/Math.log(10) - 2))))) / 2);
        int fromX = (int) (((from.getX() / Math.pow(10, Math.max(0, (int)(Math.log(from.getX())/Math.log(10) - 2))))) / 2);

        int minX = Math.min(toX, fromX)+sizeX/2;
        int maxX = Math.max(toX, fromX)+sizeX/2;

        long fromY = (long) (((from.getY() / Math.pow(10, Math.max(0, (int)(Math.log(from.getY())/Math.log(10) - 2))))) / 4);
        long toY = (long) (((to.getY() / Math.pow(10, Math.max(0, (int)(Math.log(to.getY())/Math.log(10) - 2))))) / 4);

        long minY = Math.min(fromY, toY)+sizeY/2;
        long maxY = Math.max(fromY, toY)+sizeY/2;

        moveTo.setX(fromX+sizeX/2);
        moveTo.setY(fromY+sizeY/2);

        cubicCurveTo.setControlX1((Math.random() * ((maxX - minX) + 1)) + minX);
        cubicCurveTo.setControlY1((Math.random() * ((maxY - minY) + 1)) + minY);
        cubicCurveTo.setControlX2((Math.random() * ((maxX - minX) + 1)) + minX);
        cubicCurveTo.setControlY2((Math.random() * ((maxY - minY) + 1)) + minY);

        cubicCurveTo.setX(toX+sizeX/2);
        cubicCurveTo.setY(toY+sizeY/2);

        System.out.println(toX);
        System.out.println(toY);
        System.out.println(fromX);
        System.out.println(fromY);
        System.out.println("next");

        path.getElements().add(moveTo);
        path.getElements().add(cubicCurveTo);

        Circle circle = new Circle();
        circle.setCenterX(toX+sizeX/2);
        circle.setCenterY(toY+sizeY/2);
        circle.setRadius(7.0D);
        circle.setFill(Color.MAGENTA);
        circle.setStrokeWidth(20.0D);

        final PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(1000.0D));
        pathTransition.setNode(circle);
        pathTransition.setPath(path);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);

        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                pathTransition.play();
            }
        };
        path.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);

        Group group = new Group(path, circle);
        AnchorPane.setLeftAnchor(path, 15.0);
        AnchorPane.setTopAnchor(path, 15.0);
        AnchorPane.setBottomAnchor(path, 15.0);
        AnchorPane.setRightAnchor(path, 15.0);

        anchor.getChildren().add(group);

    }

    public void changeLanguage(Locale locale){
        resources = ResourceBundle.getBundle("MessagesBundle",locale);

    }
}

