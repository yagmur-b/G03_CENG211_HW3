package enums;

public enum FoodType {
    KRILL("Kr", "Krill"),
    CRUSTACEAN("Cr", "Crustacean"),
    ANCHOVY("An", "Anchovy"),
    SQUID("Sq", "Squid"),
    MACKEREL("Ma", "Mackerel");

    private final String displayName;
    private final String notation;

    private FoodType(String notation, String displayName) {
        this.notation = notation;
        this.displayName = displayName;
    }

    public String getNotation() {
        return notation;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
