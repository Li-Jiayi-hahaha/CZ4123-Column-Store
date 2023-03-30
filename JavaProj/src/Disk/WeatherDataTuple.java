package Disk;

import java.util.Date;

public class WeatherDataTuple {
    private final int id;
    private final Date timestamp;
    private final String station;
    private final Float temperature;
    private final Float humidity;

    protected WeatherDataTuple(int id, Date timestamp, String station, Float temperature, Float humidity) {
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

    protected Float getTemperature() {
        return temperature;
    }

    protected Float getHumidity() {
        return humidity;
    }

    public String toString() {
        return id + " " + timestamp + " " + station + " " + temperature + " " + humidity;
    }
}
