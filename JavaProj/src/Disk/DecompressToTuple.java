package Disk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DecompressToTuple {

    public static int decompressToID(byte[] id_bytes) {
        return (id_bytes[2] & 0xFF) << 16 | (id_bytes[1] & 0xFF) <<8 | (id_bytes[0] & 0xFF);
    }

    public static String decompressToStation(byte[] addryearmonth_bytes) {
        int addr_year_month = (addryearmonth_bytes[1] & 0xFF) <<8 | (addryearmonth_bytes[0] & 0xFF);
        int addr = addr_year_month >> 11;
        return (addr == 0) ? "Changi" : "Paya Lebar";
    }

    public static Date decompressToDateTime(byte[] daytime_bytes, byte[] addryearmonth_bytes) throws ParseException{
        final String dateFormat = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);

        int daytime = (daytime_bytes[1] & 0xFF) <<8 | (daytime_bytes[0] & 0xFF);
        int day = daytime >> 11;
        int hour = (daytime - (day << 11)) >> 6;
        int minute = daytime % 64;

        int addr_year_month = (addryearmonth_bytes[1] & 0xFF) <<8 | (addryearmonth_bytes[0] & 0xFF);
        int year_month = addr_year_month % 2048;
        int year = 2002 + (year_month >> 4);
        int month = year_month % 16;

        String str_date = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
        String str_time = Integer.toString(hour) + ":" + Integer.toString(minute);
        String str_datetime = str_date + " " + str_time;
        return sf.parse(str_datetime);
    }

    public static int decompressToValue(byte[] float_bytes){

        int num = float_bytes[0]*100 + float_bytes[1];
        return num;
    }

}
