import java.io.*;
import java.util.*;

/*
 * Singleton Class to manege the I/O of the column store database
 * (data access object)
 */
public class DataBaseMgr {
    private static DataBaseMgr single_instance = null;

    //Changi dataset
    ArrayList<Byte[]> index_arr_c;   // 3 bytes per index
    ArrayList<Byte> yearmonth_arr_c; // 4 bits for year (2002+?), 4 bits for month
    ArrayList<Byte[]> daytime_arr_c; // 2 bytes in total, 5 bits for date, 5 bits for hour, 6 bits for minute
    ArrayList<Float> temper_arr_c;   // -1 if invalid (M)
    ArrayList<Float> humid_arr_c;    // -1 if invalid (M)

    //Paya Lebar dataset
    ArrayList<Byte[]> index_arr_p;
    ArrayList<Byte> yearmonth_arr_p;
    ArrayList<Byte[]> daytime_arr_p;
    ArrayList<Float> temper_arr_p;
    ArrayList<Float> humid_arr_p;

    /*
     * initialize the database fields here
     */
    private DataBaseMgr()
    {
        index_arr_c = new ArrayList<Byte[]>(); 
        yearmonth_arr_c = new ArrayList<Byte>(); 
        daytime_arr_c = new ArrayList<Byte[]>(); 
        temper_arr_c = new ArrayList<Float>();
        humid_arr_c = new ArrayList<Float>();

        index_arr_p = new ArrayList<Byte[]>(); 
        yearmonth_arr_p = new ArrayList<Byte>(); 
        daytime_arr_p = new ArrayList<Byte[]>(); 
        temper_arr_p = new ArrayList<Float>();
        humid_arr_p = new ArrayList<Float>();
    }

    public static synchronized DataBaseMgr getInstance()
    {
        if (single_instance == null)
            single_instance = new DataBaseMgr();
  
        return single_instance;
    }

    /*
     * assumes the year and month always increase
     * If Changi, address = 0. If Paya Lebar, address = 1
     */
    public void addRow(int address, int index, int year, int month, int date, int hour, int minute, int temperature, int humidity){

        //station is Changi
        if(address == 0) {

        }
        //station is Paya Lebar
        else {

        }
        
    }
}
