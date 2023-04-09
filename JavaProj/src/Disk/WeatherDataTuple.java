package Disk;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherDataTuple {
    private final int id;
    private final Date timestamp;
    private final String station;
    private final int temperature;
    private final int humidity;


    protected WeatherDataTuple(int id, Date timestamp, String station, int temperature, int humidity) {
        this.id = id;
        this.timestamp = timestamp;
        this.station = station;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    protected int getId() {
        return id;
    }

    protected Date getTimestamp() {
        return timestamp;
    }

    protected String getStation() {
        return station;
    }

    protected int getTemperature() {
        return temperature;
    }

    protected int getHumidity() {
        return humidity;
    }

    private String timestampToString() {
        final String dateFormat = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        return sf.format(timestamp);
    }

    private String valToString(int num){
        if (num==101) return "M";
        return Integer.toString(num/100) + "." + String.format("%02d",num%100);
    }

    public String toString() {
        return id + "," + timestampToString() + "," + station + "," + valToString(temperature) + "," + valToString(humidity);
    }
}
