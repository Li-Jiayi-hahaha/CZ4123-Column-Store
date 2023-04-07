package Disk;

public class ZoneMapMetaData {
    private final int lowerIndex;
    private final int upperIndex;
    private final int year;
    private final String station;

    public ZoneMapMetaData(int lowerIndex, int upperIndex, int year, String station) {
        this.lowerIndex = lowerIndex;
        this.upperIndex = upperIndex;
        this.year = year;
        this.station = station;
    }

    // for debugging
    @Override
    public String toString() {
        return "LowerIndex: " + lowerIndex + " " + "UpperIndex: "  + upperIndex + " " + "Year: " + year + " " + "Station: " + station;
    }
}
