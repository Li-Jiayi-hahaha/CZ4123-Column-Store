package Disk;

public class ZoneMapMetaData {
    //earliest date in this block: minyear-minmonth
    //latest date: maxyear-maxmonth
    private final int minyear;
    private final int minmonth;
    private final int maxyear;
    private final String maxmonth;

    public ZoneMapMetaData(int minyear, int minmonth, int maxyear, String maxmonth) {
        this.minyear = minyear;
        this.minmonth = minmonth;
        this.maxyear = maxyear;
        this.maxmonth = maxmonth;
    }

    // for debugging
    @Override
    public String toString() {
        return "from " + minyear + "-" + minmonth  + " to " + maxyear + "-" + maxmonth;
    }
}
