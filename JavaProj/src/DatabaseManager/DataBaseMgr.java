package DatabaseManager;

import Disk.ZoneMapMetaData;
import Disk.Disk;
import Disk.ColumnHeader;

import java.util.*;

/*
 * Singleton Class to manage the I/O of the column store database
 * (data access object)
 */
public class DataBaseMgr {
    private static DataBaseMgr single_instance = null;
    private final Disk disk;

    // Assume buffer (main memory) can hold 200,000 values per col at once
    // If buffer is full, then process current buffer contents and fetch the next batch
    // To simulate buffer not being able to hold entire data
    private final int bufferSize;

    private final ArrayList<byte[]> idColBuffer;
    private final ArrayList<byte[]> timestampColBuffer;
    private final ArrayList<byte[]> stationColBuffer;
    private final ArrayList<byte[]> temperatureColBuffer;
    private final ArrayList<byte[]> humidityColBuffer;

    /*
     * initialize the database fields here
     */
    public DataBaseMgr()
    {
        String filePath = "javaProj/src/SingaporeWeather.csv";
        this.disk = new Disk(filePath);
        this.bufferSize = 200_000;
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

    public void createZoneMap() {
        disk.createZoneMap();
    }

    public void printZoneMap() {
        HashMap<Integer, ZoneMapMetaData> zoneMap = disk.getZoneMap();
        Set<Integer> keys = zoneMap.keySet();
        for (int key : keys) {
            ZoneMapMetaData zoneMapMetaData = zoneMap.get(key);
            System.out.println(key + " " + zoneMapMetaData.toString());
        }
    }

    public void printRows(int from, int to) {
        for (int i = from; i <= to; i++) {
            System.out.println(disk.getRow(i).toString());
        }
    }

    /**
     * Loads the data from disk to column buffer
     * @param fromIdx the index to start loading from
     * @param toIdx the index to stop loading from
     * @param header the column header which we want to load from
     * @return the last index to carry on from if buffer is full, else -1
     */
    public int loadToColumnBuffer(int fromIdx, int toIdx, ColumnHeader header) {
        for (int i = fromIdx; i < toIdx + 1; i++) {
            if (isBufferFull(header)) {
                return i;
            }

            byte[] item = disk.getItemAtColumnOf(i, header);
            switch (header) {
                case ID:
                    this.idColBuffer.add(item);
                    break;
                case TIMESTAMP:
                    this.timestampColBuffer.add(item);
                    break;
                case STATION:
                    this.stationColBuffer.add(item);
                    break;
                case TEMPERATURE:
                    this.temperatureColBuffer.add(item);
                    break;
                case HUMIDITY:
                    this.humidityColBuffer.add(item);
                    break;
            }
        }
        return -1;
    }

    /**
     * Checks if a particular column buffer is full
     * @param header the column we want to check
     * @return true if full else false
     */
    private boolean isBufferFull(ColumnHeader header) {
        switch (header) {
            case ID: return this.idColBuffer.size() == this.bufferSize;
            case TIMESTAMP: return this.timestampColBuffer.size() == this.bufferSize;
            case STATION: return stationColBuffer.size() == this.bufferSize;
            case TEMPERATURE: return this.temperatureColBuffer.size() == this.bufferSize;
            case HUMIDITY: return this.humidityColBuffer.size() == this.bufferSize;
            default: return false;
        }
    }

    public int byteToYear(byte[] bytes) {
        String dateStr = new String(bytes);
        return Integer.parseInt(dateStr.split(" ")[5]);
    }

    private String byteToString(byte[] bytes) {
        return new String(bytes);
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
