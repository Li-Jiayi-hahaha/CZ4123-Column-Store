package Disk;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherDataTuple {
    private final int id;
    private final int year;
    private final int month;
    private final int day;
    private final int hour;
    private final int minute;
    private final String station;
    private final int temperature;
    private final int humidity;


    protected WeatherDataTuple(int id, int[] datetime, String station, int temperature, int humidity) {
        this.id = id;

        this.year = datetime[0];
        this.month = datetime[1];
        this.day = datetime[2];
        this.hour = datetime[3];
        this.minute = datetime[4];

        this.station = station;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    protected int getId() { return id; }

    protected int getYear() { return year; }

    protected int getMonth() { return month; }

    protected int getDay() { return day; }

    protected int getHour() { return hour; }

    protected int getMinute() { return minute; }

    protected String getStation() { return station; }

    protected int getTemperature() { return temperature; }

    protected int getHumidity() { return humidity; }

    protected String timestampToString() {
        String str_date = String.format("%04d",year) + "-" + String.format("%02d",month) + "-" + String.format("%02d",day);
        String str_time = String.format("%02d",hour) + ":" + String.format("%02d",minute);
        String str_datetime = str_date + " " + str_time;
        return str_datetime;
    }

    private String valToString(int num){
        if (num==101) return "M";
        return Integer.toString(num/100) + "." + String.format("%02d",num%100);
    }

    public String toString() {
        return id + "," + timestampToString() + "," + station + "," + valToString(temperature) + "," + valToString(humidity);
    }
}
