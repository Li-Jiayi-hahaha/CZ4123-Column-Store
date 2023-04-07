package Disk;

public class ZoneMapMetaData {
    private final int lowerIndex;
    private final int upperIndex;
    private final String station;

    public ZoneMapMetaData(int lowerIndex, int upperIndex, String station) {
        this.lowerIndex = lowerIndex;
        this.upperIndex = upperIndex;
        this.station = station;
    }

    @Override
    public String toString() {
        return lowerIndex + " " + upperIndex + " " + station;
    }
}
