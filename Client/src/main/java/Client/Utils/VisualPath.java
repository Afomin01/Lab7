package Client.Utils;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class VisualPath {
    private String owner;
    private Group group;
    private PathTransition pathTransition;
    private Path path;
    private Circle circle;
    private int time;
    private transient boolean playingAnim=false;

    public Group getGroup() {
        return group;
    }

    public Path getPath() {
        return path;
    }

    public String getOwner() {
        return owner;
    }

    public VisualPath(Group group, PathTransition pathTransition, Circle circle, Path path, int time, String owner) {
        this.group = group;
        this.pathTransition = pathTransition;
        this.circle = circle;
        this.path = path;
        this.time = time;
        this.owner = owner;
        playAnim();
    }

    public void playAnim(){
        if(!playingAnim) {
            playingAnim = true;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    FadeTransition dt = new FadeTransition(Duration.millis(200), circle);
                    dt.setFromValue(0.0);
                    dt.setToValue(1.0);
                    dt.setCycleCount(1);
                    dt.setAutoReverse(false);
                    dt.play();

                    pathTransition.play();

                    FadeTransition kt = new FadeTransition(Duration.millis(1000), circle);
                    kt.setFromValue(1.0);
                    kt.setToValue(0.0);
                    kt.setCycleCount(1);
                    kt.setAutoReverse(false);
                    kt.setDelay(Duration.millis(time * 2));
                    kt.play();
                    playingAnim = false;
                }
            });
        }
    }
}
