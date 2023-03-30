package Disk;

import java.util.Comparator;

public class TimestampComparator implements Comparator<WeatherDataTuple> {
    @Override
    public int compare(WeatherDataTuple o1, WeatherDataTuple o2) {
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    }
}
