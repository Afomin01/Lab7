package Controllers;

import Client.Main;
import Client.Utils.EThemes;
import Client.Utils.VisualPath;
import Storable.Location;
import Storable.Route;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.swing.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class VisualizeWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane anchor;

    @FXML
    private Label idText;

    @FXML
    private Label nameText;

    @FXML
    private Label fromXText;

    @FXML
    private Label fromyText;

    @FXML
    private Label fromNameText;

    @FXML
    private Label toXText;

    @FXML
    private Label toYText;

    @FXML
    private Label toNameText;

    @FXML
    private Label distText;

    @FXML
    private Label ownerText;

    @FXML
    private Label idL;

    @FXML
    private Label nameL;

    @FXML
    private Label fromXL;

    @FXML
    private Label fromYL;

    @FXML
    private Label fromNameL;

    @FXML
    private Label toXL;

    @FXML
    private Label toYL;

    @FXML
    private Label toNameL;

    @FXML
    private Label distanceL;

    @FXML
    private Label ownerL;

    @FXML
    private Label dateText;

    @FXML
    private Label dateL;

    @FXML
    private Label coordYText;

    @FXML
    private Label coordXText;

    @FXML
    private Label coordXL;

    @FXML
    private Label coordYL;

    @FXML
    private Button refreshColors;

    @FXML
    private Button pickColors;


    private ObservableList<Route> items;
    private Map<String, Color> userColors = Collections.synchronizedMap(new HashMap<>());
    private Map<Long, VisualPath> paths = Collections.synchronizedMap(new HashMap<>());

    private int sizeX = 1700;
    private int sizeY = 600;
    private double speedOfCircle = 1;

    @FXML

    void initialize() {
        refreshColors.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Iterator<Map.Entry<String, Color>> it = userColors.entrySet().iterator();
                while (it.hasNext()){
                    it.next().setValue(Color.color(Math.random(), Math.random(), Math.random()));
                }
                recolor();
            }
        });

        pickColors.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle"));

                    Parent root = fxmlLoader.load(Main.class.getResource("/ColourPickers.fxml").openStream());
                    ColourPickersController controller = fxmlLoader.getController();

                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(Main.stage.getScene().getWindow());
                    Scene scene = new Scene(root);
                    scene.getStylesheets().clear();
                    EThemes themes = EThemes.valueOf(Preferences.userRoot().get("theme", "default"));
                    if (themes.file != null) scene.getStylesheets().add(themes.file);

                    controller.setVisualizeWindowController(VisualizeWindowController.this);
                    controller.setUserColors(userColors);

                    stage.setScene(scene);
                    stage.show();
                }catch (Exception e){}
            }
        });
    }

    public void recolor(){
        for(Long id : paths.keySet()){
            paths.get(id).getPath().setStroke(userColors.get(paths.get(id).getOwner()));
        }
    }

    public void setUpVisual(ObservableList<Route> list){
        Platform.runLater(() -> {
            if(paths.size()>0){
                paths.values().forEach(r->anchor.getChildren().remove(r.getGroup()));
                paths.clear();
            }
            items = FXCollections.observableArrayList(list);
            for(Route r : items){
                userColors.putIfAbsent(r.getOwner(), Color.color(Math.random(), Math.random(), Math.random()));
                drawObject(r, userColors.get(r.getOwner()));
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                     List<Long> ids = new ArrayList<>(paths.keySet());
                     VisualPath visualPath = paths.get(ids.get(new Random().nextInt(ids.size())));
                     visualPath.playAnim();

                        Thread.sleep(200);
                    } catch (Exception e) {
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
    public void addItem(Route route){
        if(items.stream().noneMatch(r -> r.getId() == route.getId())){
            items.add(route);
            Platform.runLater(() ->{
                userColors.putIfAbsent(route.getOwner(), Color.color(Math.random(), Math.random(), Math.random()));
                drawObject(route, userColors.get(route.getOwner()));
            });
        }else{
            int i = 0;
            for(Route r : items){
                if(r.getId()==route.getId()){
                    items.set(i,route);
                    Platform.runLater(() -> {
                        anchor.getChildren().remove(paths.get(r.getId()).getGroup());
                        drawObject(route, userColors.get(route.getOwner()));
                    });
                }
                i++;
            }
        }
    }

    public void removeItems(ObservableList<Route> list) {
        Platform.runLater(() -> {
            list.forEach(r -> {
                anchor.getChildren().remove(paths.get(r.getId()).getGroup());
                paths.remove(r.getId());
                items.remove(r);
            });
        });
    }

    private void drawObject(Route route, Color colorOfLine){
        Path path = new Path();
        path.setStrokeWidth(4.0);

        CubicCurveTo cubicCurveTo = new CubicCurveTo();

        Location to = route.getTo();
        Location from = route.getFrom();

        MoveTo moveTo = new MoveTo();


        int toX = (int) (((to.getX() / Math.pow(10, Math.max(0, (int)(Math.log(to.getX())/Math.log(10) - 2))))) / (2000/sizeX));
        int fromX = (int) (((from.getX() / Math.pow(10, Math.max(0, (int)(Math.log(from.getX())/Math.log(10) - 2))))) / (2000/sizeX));

        int minX = Math.min(toX, fromX)+sizeX/2;
        int maxX = Math.max(toX, fromX)+sizeX/2;

        long fromY = (long) (((from.getY() / Math.pow(10, Math.max(0, (int)(Math.log(from.getY())/Math.log(10) - 2))))) / (2000/sizeY));
        long toY = (long) (((to.getY() / Math.pow(10, Math.max(0, (int)(Math.log(to.getY())/Math.log(10) - 2))))) / (2000/sizeY));

        long minY = Math.min(fromY, toY)+sizeY/2;
        long maxY = Math.max(fromY, toY)+sizeY/2;

        int minToMaxX = (int) Math.sqrt((maxX - minX) * (maxX - minX) + (maxY - minY) * (maxY - minY));



        moveTo.setX(fromX+sizeX/2);
        moveTo.setY(fromY+sizeY/2);

        cubicCurveTo.setControlX1((Math.random() * ((maxX - minX) + 1)) + minX + 2);
        cubicCurveTo.setControlY1((Math.random() * ((maxY - minY) + 1)) + minY + 3);
        cubicCurveTo.setControlX2((Math.random() * ((maxX - minX) + 1)) + minX + 4);
        cubicCurveTo.setControlY2((Math.random() * ((maxY - minY) + 1)) + minY + 5);

        cubicCurveTo.setX(toX+sizeX/2);
        cubicCurveTo.setY(toY+sizeY/2);

        path.getElements().add(moveTo);
        path.getElements().add(cubicCurveTo);
        path.setStroke(colorOfLine);

        Circle circle = new Circle();
        circle.setCenterX(fromX+sizeX/2);
        circle.setCenterY(fromY+sizeY/2);
        circle.setRadius(8.0D);
        circle.setFill(Color.YELLOW);
        circle.setStrokeWidth(20.0D);

        final PathTransition pathTransition = new PathTransition();
        if (minToMaxX < 500) {
            pathTransition.setDuration(Duration.millis(1000));
        } else {
            pathTransition.setDuration(Duration.millis((minToMaxX*2) / speedOfCircle));
        }
        pathTransition.setNode(circle);
        pathTransition.setPath(path);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);


        path.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if(e.getButton().equals(MouseButton.PRIMARY)) {
                if(e.getClickCount() == 2) {
                    if(Main.login.equals(route.getOwner())){
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setResources(ResourceBundle.getBundle("MessagesBundle"));

                            Parent root = fxmlLoader.load(Main.class.getResource("/EditWindow.fxml").openStream());
                            EditWindowController controller = fxmlLoader.getController();

                            Stage stage = new Stage();
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.initModality(Modality.WINDOW_MODAL);
                            stage.initOwner(Main.stage.getScene().getWindow());
                            stage.setTitle(resources.getString("alerts.change"));
                            Scene scene = new Scene(root);
                            scene.getStylesheets().clear();
                            EThemes themes = EThemes.valueOf(Preferences.userRoot().get("theme", "default"));
                            if (themes.file != null) scene.getStylesheets().add(themes.file);

                            stage.setScene(scene);
                            controller.setFields(route);
                            stage.show();
                        }catch (IOException ex){}
                    }
                }else {
                    idL.setText(NumberFormat.getInstance().format(route.getId()));
                    coordXL.setText(NumberFormat.getInstance().format(route.getCoordinates().getx()));
                    coordYL.setText(NumberFormat.getInstance().format(route.getCoordinates().gety()));
                    nameL.setText(route.getName());
                    dateL.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(route.getCreationDate()));
                    fromXL.setText(NumberFormat.getInstance().format(route.getFrom().getX()));
                    fromYL.setText(NumberFormat.getInstance().format(route.getFrom().getY()));
                    fromNameL.setText(route.getFrom().getName());
                    toXL.setText(NumberFormat.getInstance().format(route.getTo().getX()));
                    toYL.setText(NumberFormat.getInstance().format(route.getTo().getY()));
                    toNameL.setText(route.getTo().getName());
                    distanceL.setText(NumberFormat.getInstance().format(route.getDistance()));
                    ownerL.setText(route.getOwner());
                }
            }
        });

        Group group = new Group(path, circle);

        pathTransition.play();

        FadeTransition ft = new FadeTransition(Duration.millis(200), circle);
        ft.setDelay(Duration.millis(minToMaxX*2));
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();

        anchor.getChildren().add(group);
        paths.put(route.getId(), new VisualPath(group, pathTransition, circle, path, minToMaxX, route.getOwner()));
    }

    public void changeLanguage(Locale locale){
        resources = ResourceBundle.getBundle("MessagesBundle",locale);
        idText.setText(resources.getString("table.id"));
        nameText.setText(resources.getString("table.name"));
        coordXText.setText(resources.getString("console.coordX"));
        coordYText.setText(resources.getString("console.coordY"));
        dateText.setText(resources.getString("table.creationDate"));
        fromXText.setText(resources.getString("console.fromX"));
        fromyText.setText(resources.getString("console.fromY"));
        fromNameText.setText(resources.getString("console.fromName"));
        toXText.setText(resources.getString("console.toX"));
        toYText.setText(resources.getString("console.toY"));
        toNameText.setText(resources.getString("console.toName"));
        distText.setText(resources.getString("table.distance"));
        ownerText.setText(resources.getString("table.owner"));

        idL.setText("");
        coordXL.setText("");
        coordYL.setText("");
        nameL.setText("");
        dateL.setText("");
        fromXL.setText("");
        fromYL.setText("");
        fromNameL.setText("");
        toXL.setText("");
        toYL.setText("");
        toNameL.setText("");
        distanceL.setText("");
        ownerL.setText("");
    }
}

