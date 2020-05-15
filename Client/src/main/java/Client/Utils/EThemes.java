package Client.Utils;

import javafx.scene.paint.Color;

public enum EThemes {
    DEFAULT(null,null),
    DARK(Color.DARKSLATEGREY, Color.WHITE),
    RED(Color.DARKRED, Color.ORANGERED),
    CUSTOM(Color.WHITE, Color.WHITE);

    public Color backgroundColor;
    public Color textColor;

    EThemes(Color backgroundColor, Color textColor) {
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }
}
