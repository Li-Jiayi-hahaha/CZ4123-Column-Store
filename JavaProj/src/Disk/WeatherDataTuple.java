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


    public WeatherDataTuple(int id, int[] datetime, String station, int temperature, int humidity) {
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

    public int getId() { return id; }

    public int getYear() { return year; }

    public int getMonth() { return month; }

    public int getDay() { return day; }

    public int getHour() { return hour; }

    public int getMinute() { return minute; }

    public String getStation() { return station; }

    public int getTemperature() { return temperature; }

    public int getHumidity() { return humidity; }

    public static String timestampToString(int year, int month, int day, int hour, int minute) {
        String str_date = getDateString(year, month, day);
        String str_time = String.format("%02d",hour) + ":" + String.format("%02d",minute);
        String str_datetime = str_date + " " + str_time;
        return str_datetime;
    }

    public static String getDateString(int year, int month, int day){
        return String.format("%04d",year) + "-" + String.format("%02d",month) + "-" + String.format("%02d",day);
    }

    public static String valToString(int num){
        if (num==10100) return "M";
        return Integer.toString(num/100) + "." + String.format("%02d",num%100);
    }

    public String toString() {
        return id + "," + timestampToString(year, month, day, hour, minute) + "," + station + "," + valToString(temperature) + "," + valToString(humidity);
    }
}
