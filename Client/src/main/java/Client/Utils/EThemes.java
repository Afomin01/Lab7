package Client.Utils;

public enum EThemes {
    DEFAULT(null),
    DARK("dark.css"),
    RED("red.css"),
    BLUE("blue.css");

    public final String file;

    EThemes(String file) {
        this.file = file;
    }


    @Override
    public String toString() {
        return name();
    }
}
