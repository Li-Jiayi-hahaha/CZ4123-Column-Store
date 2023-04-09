package Disk;

public class TupleCompress {

    public static byte[] compressID(int id){
        byte[] result = new byte[3];
        result[2] = (byte) (id >> 16);
        result[1] = (byte) (id >> 8);
        result[0] = (byte) (id >> 0);
        return result;
    }

    public static byte[] compressDayTime(int day, int hour, int minute){
        int daytime = (day << 11) | (hour << 6) | (minute << 0);
        byte[] result = new byte[2];
        result[1] = (byte) (daytime >> 8);
        result[0] = (byte) (daytime >> 0);
        return result;
    }

    public static byte[] compressAddrYearMonth(String station, int year, int month){
        int addr = station.equals("Changi")? 0: 1;
        int addr_year_month = (addr << 11) | ( (year-2002) << 4) | (month << 0);

        byte[] result = new byte[2];
        result[1] = (byte) (addr_year_month >> 8);
        result[0] = (byte) (addr_year_month >> 0);
        return result;
    }

    public static byte[] compressValue(int num){
        byte[] result = new byte[2];
        result[0] = (byte) (num/100);
        result[1] = (byte) (num%100);
        return result;
    }

}
