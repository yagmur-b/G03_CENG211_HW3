package enums;

public enum HazardType {
    LIGHT_ICE_BLOCK("LB", "Light Ice Block"),
    HEAVY_ICE_BLOCK("HB", "Heavy Ice Block"),
    SEA_LION("SL", "Sea Lion"),
    HOLE_IN_ICE("HI", "Hole in Ice");

    private final String notation;
    private final String displayName;

    HazardType(String notation, String displayName) {
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
