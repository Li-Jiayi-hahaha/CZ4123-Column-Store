package Disk;

public class TupleCompress {

    public static byte[] compressDayTime(int day, int hour, int minute){
        int daytime = (day << 11) | (hour << 6) | (minute << 0);
        byte[] result = new byte[2];
        result[1] = (byte) (daytime >> 8);
        result[0] = (byte) (daytime >> 0);
        return result;
    }

    public static byte[] compressIdYearMonth(int id, int year, int month){
        int idYearMonth = ( (id % Constants.BUFFERSIZE) << 9) | ((year - Constants.STARTYEAR) << 4) | (month << 0);

        byte[] result = new byte[3];
        result[2] = (byte) (idYearMonth >> 16);
        result[1] = (byte) (idYearMonth >> 8);
        result[0] = (byte) (idYearMonth >> 0);
        return result;
    }

    public static byte[] compressValue(int val, int addr){
        int num = (val<<1) + addr;
        byte[] result = new byte[2];
        result[1] = (byte) (num >> 8);
        result[0] = (byte) (num >> 0);
        return result;
    }

}
