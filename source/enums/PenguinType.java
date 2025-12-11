package enums;

public enum PenguinType {
    KING("King models.penguins.Penguin"),
    EMPEROR("Emperor models.penguins.Penguin"),
    ROYAL("Royal models.penguins.Penguin"),
    ROCKHOPPER("Rockhopper models.penguins.Penguin");

    private final String displayName;

    private PenguinType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
