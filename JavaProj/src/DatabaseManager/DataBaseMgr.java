package DatabaseManager;

import Disk.ZoneMapMetaData;
import Disk.Disk;
import Disk.ColumnHeader;

import java.text.ParseException;
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
    

    /*
     * initialize the database fields here
     */
    public DataBaseMgr()
    {
        String filePath = "javaProj/src/SingaporeWeather.csv";
        this.disk = new Disk(filePath);
        
    }

    public static synchronized DataBaseMgr getInstance()
    {
        if (single_instance == null)
            single_instance = new DataBaseMgr();
  
        return single_instance;
    }

    public ArrayList<String> getRows(int from, int to) throws ParseException {
        ArrayList<String> strs = new ArrayList<String>();
        for (int i = from; i <= to; i++) {
            strs.add(disk.getRow(i).toString());
        }
        return strs;
    }

    public int getSize(){
        return disk.getSize();
    }

    /* 
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
    */

    public void printRows(int from, int to) throws ParseException {
        for (int i = from; i <= to; i++) {
            System.out.println(disk.getRow(i).toString());
        }
    }

}
