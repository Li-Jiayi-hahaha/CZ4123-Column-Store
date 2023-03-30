package DatabaseManager;

import Disk.Disk;

import java.util.ArrayList;

/*
 * Singleton Class to manege the I/O of the column store database
 * (data access object)
 */
public class DataBaseMgr {
    private static DataBaseMgr single_instance = null;
    private Disk disk;

    // Assume buffer (main memory) can hold 200,000 values per col at once
    // If buffer is full, then process current buffer contents and fetch the next batch
    // To simulate buffer not being able to hold entire data
    private final int bufferSize;

    private ArrayList<byte[]> idColBuffer;
    private ArrayList<byte[]> timestampColBuffer;
    private ArrayList<byte[]> stationColBuffer;
    private ArrayList<byte[]> temperatureColBuffer;
    private ArrayList<byte[]> humidityColBuffer;

    /*
     * initialize the database fields here
     */
    public DataBaseMgr()
    {
        String filePath = "javaProj/src/SingaporeWeather.csv";
        this.disk = new Disk(filePath);
        this.bufferSize = 200_000;
        System.out.println(this.bufferSize);
        this.idColBuffer = new ArrayList<>();
        this.timestampColBuffer = new ArrayList<>();
        this.stationColBuffer = new ArrayList<>();
        this.temperatureColBuffer = new ArrayList<>();
        this.humidityColBuffer = new ArrayList<>();
    }

    public static synchronized DataBaseMgr getInstance()
    {
        if (single_instance == null)
            single_instance = new DataBaseMgr();
  
        return single_instance;
    }

    /*
     * assumes the year and month always increase
     * If Changi, address = C. If Paya Lebar, address = P
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
