package Disk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Disk implemented in Column Store manner
 * TODO: really save column store data to binary files
 */
public class Disk {
    private static final String NaN = "M";
    private final String csvfilepath;
    private ArrayList<byte[]> idCol_buffer;
    private ArrayList<byte[]> daytimeCol_buffer;
    private ArrayList<byte[]> addrYearMonthCol_buffer;
    private ArrayList<byte[]> temperatureCol_buffer;
    private ArrayList<byte[]> humidityCol_buffer;
    private ColumnStoreDAO columnStoreDAO;

    private ArrayList<WeatherDataTuple> tuples_buffer;

    //private final HashMap<Integer, ZoneMapMetaData> zoneMap;

    private final int bufferSize;

    public Disk(String csvfilepath) {
        this.csvfilepath = csvfilepath;
        this.idCol_buffer = new ArrayList<>();
        this.daytimeCol_buffer = new ArrayList<>();
        this.addrYearMonthCol_buffer = new ArrayList<>();
        this.temperatureCol_buffer = new ArrayList<>();
        this.humidityCol_buffer = new ArrayList<>();

        this.columnStoreDAO = ColumnStoreDAO.getInstance();
        //this.zoneMap = new HashMap<>();

        this.bufferSize = 2000;
        this.init();
    }

    private void init() {
        try {
            this.loadCSV();
            
        } catch (IOException e) {
            System.out.println("An error occurred while loading data to disk!");
            e.printStackTrace();
        }
    }

    private void loadCSV() throws IOException {
        this.tuples_buffer = new ArrayList<>();

        System.out.printf("Loading data from %s to disk...\n", this.csvfilepath);

        File file = new File(this.csvfilepath);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String[] splitAttr;
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            splitAttr = line.split(",");
            // Ignore Headers
            if (splitAttr[0].equals("id")) continue;
            WeatherDataTuple tuple = new WeatherDataTuple(stringToInt(splitAttr[0]), stringToDate(splitAttr[1]), splitAttr[2], stringToValue(splitAttr[3]), stringToValue(splitAttr[4]));
            this.addTupleToInputBuffer(tuple);
        }
        this.writeBufferToColumnStore();
        bufferedReader.close();

        return;
    } 

    private int stringToInt(String val) {
        return Integer.parseInt(val);
    }

    private Date stringToDate(String val) {
        final String dateFormat = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        try {
            return sf.parse(val);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int stringToValue(String val) {
        if (val.equals(NaN)) {
            return 101;
        }

        String[] strs = val.split("\\.");
        int int_part = Integer.parseInt(strs[0]);
        int frag_part = Integer.parseInt(strs[1]);
        
        int num = int_part * 100 + frag_part;
        return num;
    }

    private void addTupleToInputBuffer(WeatherDataTuple tuple){
        this.tuples_buffer.add(tuple);

        if(this.tuples_buffer.size() == this.bufferSize){
            this.writeBufferToColumnStore();
        }
    }

    private void writeBufferToColumnStore() {
        for (WeatherDataTuple tuple : this.tuples_buffer) {
            this.tupleToColumnStore(tuple);
        }

        //write buffer to disk storage
        this.columnStoreDAO.addOneCol(0, this.idCol_buffer);
        this.columnStoreDAO.addOneCol(1, this.daytimeCol_buffer);
        this.columnStoreDAO.addOneCol(2, this.addrYearMonthCol_buffer);
        this.columnStoreDAO.addOneCol(3, this.temperatureCol_buffer);
        this.columnStoreDAO.addOneCol(4, this.humidityCol_buffer);

        //flash the buffers
        this.tuples_buffer = new ArrayList<>();
        this.idCol_buffer = new ArrayList<>();
        this.daytimeCol_buffer = new ArrayList<>();
        this.addrYearMonthCol_buffer = new ArrayList<>();
        this.temperatureCol_buffer = new ArrayList<>();
        this.humidityCol_buffer = new ArrayList<>();

    }

    private void tupleToColumnStore(WeatherDataTuple tuple) {

        byte[] id_comp = TupleCompress.compressID(tuple.getId());
        this.idCol_buffer.add(id_comp);

        Date date = tuple.getTimestamp();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        //Add one to month {0 - 11}
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY); 
        int minute = calendar.get(Calendar.MINUTE); 

        byte[] daytime_comp = TupleCompress.compressDayTime(day, hour, minute);
        this.daytimeCol_buffer.add(daytime_comp);

        String station = tuple.getStation();

        byte[] addryearmonth_comp = TupleCompress.compressAddrYearMonth(station, year, month);
        this.addrYearMonthCol_buffer.add(addryearmonth_comp);

        byte[] temperature_comp = TupleCompress.compressValue(tuple.getTemperature());
        this.temperatureCol_buffer.add(temperature_comp);

        byte[] humidity_comp = TupleCompress.compressValue(tuple.getHumidity());
        this.humidityCol_buffer.add(humidity_comp);

    }

    public ArrayList<String> getAllRows() throws ParseException{
        ArrayList<String> result = new ArrayList<String>();
        int num_block = this.columnStoreDAO.getNumBlock();

        for(int i=0;i<num_block;i++){
            ArrayList<byte[]> id_buffer = this.columnStoreDAO.readOneFile(0, i);
            ArrayList<byte[]> daytime_buffer = this.columnStoreDAO.readOneFile(1, i);
            ArrayList<byte[]> addrYearMonth_buffer = this.columnStoreDAO.readOneFile(2, i);
            ArrayList<byte[]> temperature_buffer = this.columnStoreDAO.readOneFile(3, i);
            ArrayList<byte[]> humidity_buffer = this.columnStoreDAO.readOneFile(4, i);

            int s = id_buffer.size();
            for(int j=0;j<s;j++){
                WeatherDataTuple tuple = decompressRow(id_buffer.get(j), daytime_buffer.get(j), addrYearMonth_buffer.get(j), 
                                                        temperature_buffer.get(j), humidity_buffer.get(j));

                result.add(tuple.toString());
            }
        }

        return result;

    }

    public WeatherDataTuple decompressRow(byte[] id_comp, byte[] daytime_comp, byte[] addr_year_month_comp, 
                                        byte[] temperature_comp, byte[] humidity_comp) throws ParseException{
        
        int id = DecompressToTuple.decompressToID(id_comp);

        Date timestamp = DecompressToTuple.decompressToDateTime(daytime_comp, addr_year_month_comp);
        String station = DecompressToTuple.decompressToStation(addr_year_month_comp);
        
        int temperature = DecompressToTuple.decompressToValue(temperature_comp);

        int humidity = DecompressToTuple.decompressToValue(humidity_comp);

        WeatherDataTuple tuple = new WeatherDataTuple(id, timestamp, station, temperature, humidity);
        
        return tuple;
    }

    /* 
    public WeatherDataTuple getOneRow(int index) throws ParseException {
        byte[] id_comp = this.idCol_buffer.get(index);
        int id = DecompressToTuple.decompressToID(id_comp);

        byte[] daytime_comp = this.daytimeCol_buffer.get(index);
        byte[] addr_year_month_comp = this.addrYearMonthCol_buffer.get(index);

        Date timestamp = DecompressToTuple.decompressToDateTime(daytime_comp, addr_year_month_comp);
        String station = DecompressToTuple.decompressToStation(addr_year_month_comp);

        byte[] temperature_comp = this.temperatureCol_buffer.get(index);
        int temperature = DecompressToTuple.decompressToValue(temperature_comp);

        byte[] humidity_comp = this.humidityCol_buffer.get(index);
        int humidity = DecompressToTuple.decompressToValue(humidity_comp);
        
        return new WeatherDataTuple(id, timestamp, station, temperature, humidity);
    }
    */

    public int getSize(){
        return idCol_buffer.size();
    }

    public int getNumBlock() {
        return this.columnStoreDAO.getNumBlock();
    }

    /**
     * Zone Map is stored in a HashMap<Integer, ZoneMapMetaData> where the key is an Integer from 0 - x
     * It is designed in this manner:
     * - Key is just for indexing
     * - ZoneMapMetaData contains the lowerIndex, upperIndex of the range for the particular year and station
     * ZoneMapMetaData contains the relevant information about the items within then range of a particular year
     */
    /*
    public void createZoneMap() {
        if (!checkSize()) return;
        if (zoneMap.size() != 0) return; // Prevent recreation of zonemap if it already exists

        int prevYear = 2002;
        int lowerIndex = 0;
        int key = 0;
        int i = 0;
        String prevStation = "C";
        for (; i < timestampCol.size(); i++) {
            byte[] timestampBytes = timestampCol.get(i);
            byte[] stationBytes = stationCol.get(i);
            int year = byteToYear(timestampBytes);
            String stn = byteToString(stationBytes);

            if (year > prevYear) {
                ZoneMapMetaData zoneMapMetaData = new ZoneMapMetaData(lowerIndex, i - 1, prevYear, stn);
                zoneMap.put(key, zoneMapMetaData);
                prevYear = year;
                lowerIndex = i;
                key++;
            }

            if (!stn.equals(prevStation)) {
                ZoneMapMetaData zoneMapMetaData = new ZoneMapMetaData(lowerIndex, i - 1, prevYear, prevStation);
                zoneMap.put(key, zoneMapMetaData);
                key++;
                break;
            }
        }

        lowerIndex = i;
        prevYear = 2002;
        for (; i < timestampCol.size(); i++) {
            byte[] timestampBytes = timestampCol.get(i);
            byte[] stationBytes = stationCol.get(i);
            int year = byteToYear(timestampBytes);
            String stn = byteToString(stationBytes);

            if (year > prevYear) {
                ZoneMapMetaData zoneMapMetaData = new ZoneMapMetaData(lowerIndex, i - 1, prevYear, stn);
                zoneMap.put(key, zoneMapMetaData);
                prevYear = year;
                lowerIndex = i;
                key++;
            }
            if (i == timestampCol.size() - 1) {
                ZoneMapMetaData zoneMapMetaData = new ZoneMapMetaData(lowerIndex, i -1, prevYear, stn);
                zoneMap.put(key, zoneMapMetaData);
                prevYear = year;
                lowerIndex = i;
                key++;
            }
        }
    }

    public HashMap<Integer, ZoneMapMetaData> getZoneMap() {
        return zoneMap;
    }

    public int byteToYear(byte[] bytes) {
        String dateStr = new String(bytes);
        return Integer.parseInt(dateStr.split(" ")[5]);
    }

    private String byteToString(byte[] bytes) {
        return new String(bytes);
    }
    

    // For debugging
    private void printTable(int head, ArrayList<WeatherDataTuple> table) {
        if (table.size() == 0) {
            System.out.println("Table has not been initialized!");
            return;
        }

        System.out.printf("Printing %d rows from disk\n", head);
        for (int i = 0; i < head; i++) {
            WeatherDataTuple tuple = table.get(i);
            System.out.println(tuple.toString());
        }
    }
    */
}