package Disk;

public class Constants {
    public static final int BUFFERSIZE = 2000;
    public static final int STARTYEAR = 2002;

    public static int station2Addr(String station){
        return station.equals("Changi")? 0:1;
    }

    public static String addr2Station(int addr){
        return addr == 0? "Changi" : "Paya Lebar";
    }
}
