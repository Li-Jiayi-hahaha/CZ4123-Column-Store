package DatabaseManager;

public enum ColumnDataTypes {
    ID(0),
    TIMESTAMP(1),
    STATION(2),
    TEMPERATURE(3),
    HUMIDITY(4);
    public final int type;
    private ColumnDataTypes(int type) {
        this.type = type;
    }
}
