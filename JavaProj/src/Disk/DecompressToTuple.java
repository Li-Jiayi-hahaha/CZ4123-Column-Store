package Disk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DecompressToTuple {

    public static int[] decompressToIdYearMonth(byte[] idYearMonth_bytes){
        int idYearMonth = (idYearMonth_bytes[2] & 0xFF) << 16 | (idYearMonth_bytes[1] & 0xFF) <<8 | (idYearMonth_bytes[0] & 0xFF);

        int id = idYearMonth >> 9;
        int year = (idYearMonth - (id << 9)) >> 4;
        year += Constants.STARTYEAR;
        int month = idYearMonth % 16;

        int[] result = {id, year, month};
        return result;
    }

    public static int[] decompressToDayTime(byte[] daytime_bytes){
        int daytime = (daytime_bytes[1] & 0xFF) <<8 | (daytime_bytes[0] & 0xFF);
        int day = daytime >> 11;
        int hour = (daytime - (day << 11)) >> 6;
        int minute = daytime % 64;

        int[] result = {day, hour, minute};
        return result;
    }

    public static int[] decompressToValue(byte[] value_addr_bytes){
        int num = (value_addr_bytes[1] & 0xFF) <<8 | (value_addr_bytes[0] & 0xFF);
        int addr = num%2;
        int value = num>>1;
        int[] result = {value, addr};
        return result;
    }

}
