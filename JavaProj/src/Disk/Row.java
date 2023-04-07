package Disk;

public class Row {
    private final byte[] id;
    private final byte[] timestamp;
    private final byte[] station;
    private final byte[] temperature;
    private final byte[] humidity;

    public Row(byte[] id, byte[] timestamp, byte[] station, byte[] temperature, byte[] humidity) {
        this.id = id;
        this.timestamp = timestamp;
        this.station = station;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public byte[] getId() {
        return id;
    }

    public byte[] getTimestamp() {
        return timestamp;
    }

    public byte[] getStation() {
        return station;
    }

    public byte[] getTemperature() {
        return temperature;
    }

    public byte[] getHumidity() {
        return humidity;
    }

    public String toString() {
        String idStr = new String(id);
        String timestampStr = new String(timestamp);
        String stationStr = new String(station);
        String tempStr = new String(temperature);
        String humidityStr = new String(humidity);
        return idStr + " " + timestampStr + " " + stationStr + " " + tempStr + " " + humidityStr;
    }
}
