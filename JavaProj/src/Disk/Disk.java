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

/**
 * Disk implemented in Column Store manner
 * TODO: really save column store data to binary files
 */
public class Disk {
    private static Disk single_instance = null;

    private static final String NaN = "M";
    private final String csvfilepath;

    //int[3]: id, year, month
    private ArrayList<int[]> ver_compYearMonth;
    private ArrayList<byte[]> yearMonthCol_buffer;
    private ArrayList<byte[]> daytimeCol_buffer;
    private ArrayList<byte[]> temperatureCol_buffer;
    private ArrayList<byte[]> humidityCol_buffer;
    private ColumnStoreDAO columnStoreDAO;

    private ArrayList<WeatherDataTuple> tuples_buffer;

    private final ZoneMap zoneMap;;

    public Disk() {
        this.csvfilepath = "javaProj/src/SingaporeWeather.csv";

        this.ver_compYearMonth = new ArrayList<>();
        this.yearMonthCol_buffer = new ArrayList<>();
        this.daytimeCol_buffer = new ArrayList<>();
        this.temperatureCol_buffer = new ArrayList<>();
        this.humidityCol_buffer = new ArrayList<>();

        this.columnStoreDAO = ColumnStoreDAO.getInstance();
        this.zoneMap = ZoneMap.getInstance();

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

    public static synchronized Disk getInstance()
    {
        if (single_instance == null){
            single_instance = new Disk();
        }
  
        return single_instance;
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

    private int[] stringToDate(String val) {
        int[] datetime = {};
        final String dateFormat = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        try {
            Date date = sf.parse(val);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            //Add one to month {0 - 11}
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY); 
            int minute = calendar.get(Calendar.MINUTE);
            datetime = new int[]{year, month, day, hour, minute};
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datetime;
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

        if(this.tuples_buffer.size() == Constants.BUFFERSIZE){
            this.writeBufferToColumnStore();
        }
    }

    private void writeBufferToColumnStore() {
        for (WeatherDataTuple tuple : this.tuples_buffer) {
            this.tupleToColumnBuffer(tuple);
        }

        //write buffer to disk storage
        this.columnStoreDAO.addOneCol(0, this.yearMonthCol_buffer);
        this.columnStoreDAO.addOneCol(1, this.daytimeCol_buffer);
        this.columnStoreDAO.addOneCol(2, this.temperatureCol_buffer);
        this.columnStoreDAO.addOneCol(3, this.humidityCol_buffer);

        //add this block's info to zone map
        this.zoneMap.addOneBlock(ver_compYearMonth);

        //flash the buffers
        this.tuples_buffer = new ArrayList<>();
        this.ver_compYearMonth = new ArrayList<>();
        this.yearMonthCol_buffer = new ArrayList<>();
        this.daytimeCol_buffer = new ArrayList<>();
        this.temperatureCol_buffer = new ArrayList<>();
        this.humidityCol_buffer = new ArrayList<>();

    }

    private void tupleToColumnBuffer(WeatherDataTuple tuple) {

        int id = tuple.getId();
        int year = tuple.getYear();
        int month = tuple.getMonth();
        int day = tuple.getDay();
        int hour = tuple.getHour(); 
        int minute = tuple.getMinute(); 

        byte[] daytime_comp = TupleCompress.compressDayTime(day, hour, minute);
        this.daytimeCol_buffer.add(daytime_comp);

        addTupleToVerticallyCompressedYearMonth(id, year, month);

        String station = tuple.getStation();
        int addr = station.equals("Changi")? 0:1;

        byte[] temperature_comp = TupleCompress.compressValue(tuple.getTemperature(), addr);
        this.temperatureCol_buffer.add(temperature_comp);

        byte[] humidity_comp = TupleCompress.compressValue(tuple.getHumidity(), addr);
        this.humidityCol_buffer.add(humidity_comp);

    }

    private void addTupleToVerticallyCompressedYearMonth(int id, int year, int month){
        int s = this.ver_compYearMonth.size();
        if(s!=0){
            int[] last = this.ver_compYearMonth.get(s-1);
            if(last[1]==year && last[2]==month) return;
        }

        int[] arr = new int[]{id, year, month};
        this.ver_compYearMonth.add(arr);

        byte[] idYearMonth = TupleCompress.compressIdYearMonth(id, year, month);
        this.yearMonthCol_buffer.add(idYearMonth);
        return;
    }

    public ArrayList<String> getAllRowsString(){
        ArrayList<String> tuple_strs = new ArrayList<String>();
        int num_block = this.columnStoreDAO.getNumBlock();

        for(int i=0;i<num_block;i++){
            ArrayList<WeatherDataTuple> tuples = getBlock(i);
            for(WeatherDataTuple tuple: tuples){
                tuple_strs.add(tuple.toString());
            }
        }
        return tuple_strs;

    }

    public ArrayList<WeatherDataTuple> getBlock(int fid){
        ArrayList<WeatherDataTuple> tuples = new ArrayList<>();

        ArrayList<int[]> idYearMonths = getBlockRIdYearMonth(fid);
        ArrayList<int[]> daytimes = getBlockDayTime(fid);
        ArrayList<int[]> temperatureAddrs = getBlockTemperature(fid);
        ArrayList<int[]> humidityAddrs = getBlockHumidity(fid);

        int s = temperatureAddrs.size();

        for(int i=0;i<s;i++){
            int[] idYearMonth = idYearMonths.get(i);
            int id = idYearMonth[0], year = idYearMonth[1], month = idYearMonth[2];

            int[] daytime = daytimes.get(i);
            int day = daytime[0], hour = daytime[1], minute = daytime[2];

            int[] temperatureAddr = temperatureAddrs.get(i);
            int temperature = temperatureAddr[0], addr = temperatureAddr[1];

            int[] humidityAddr = humidityAddrs.get(i);
            int humidity = humidityAddr[0];

            String station = addr == 0? "Changi" : "Paya Lebar";
            int[] datetime = {year, month, day, hour, minute};
            WeatherDataTuple tuple = new WeatherDataTuple(id, datetime, station, temperature, humidity);

            tuples.add(tuple);
        }

        return tuples;
    }
    
    //{id, year, month}
    public ArrayList<int[]> getBlockRIdYearMonth(int fid){

        ArrayList<byte[]> idYearMonth_compressed_buffer = this.columnStoreDAO.readOneFile(0, fid);

        int n = columnStoreDAO.getNumBlock();
        int s = -1;
        if(fid < n-1) s = Constants.BUFFERSIZE;
        else s = columnStoreDAO.getLastBlockSize();
        int cnt = -1;
        int start_id = -1, year = -1, month = -1;
        int[] start_idYearMonth;
        int next_id = -1;

        ArrayList<int[]> result = new ArrayList<>();

        for(int j=0;j<s;j++){
            //decompress from the vertical compression of yearmonth column
            if(j>=next_id){
                cnt += 1;
                start_idYearMonth = DecompressToTuple.decompressToIdYearMonth(idYearMonth_compressed_buffer.get(cnt)) ;
                start_id = start_idYearMonth[0];
                year = start_idYearMonth[1];
                month = start_idYearMonth[2];
                if(idYearMonth_compressed_buffer.size()>cnt+1){
                    int[] next_idYearMonth = DecompressToTuple.decompressToIdYearMonth(idYearMonth_compressed_buffer.get(cnt+1));
                    next_id = next_idYearMonth[0];
                }
                else next_id = s;
            }
            int[] arr = {j + fid*Constants.BUFFERSIZE, year, month};
            result.add(arr);
        }

        return result;

    }

    //{day, hour, min}
    public ArrayList<int[]> getBlockDayTime(int fid){
        ArrayList<byte[]> daytime_buffer = this.columnStoreDAO.readOneFile(1, fid);

        ArrayList<int[]> result = new ArrayList<>();

        for(byte[] daytime_bytes: daytime_buffer){
            int[] daytime = DecompressToTuple.decompressToDayTime(daytime_bytes);
            int day = daytime[0], hour = daytime[1], minute = daytime[2];
            int[] arr = {day, hour, minute};
            result.add(arr);
        }
        return result;
    }

    //{temperature, addr}
    public ArrayList<int[]> getBlockTemperature(int fid){
        ArrayList<byte[]> temperature_buffer = this.columnStoreDAO.readOneFile(2, fid);

        ArrayList<int[]> result = new ArrayList<>();

        for(byte[] temperature_bytes: temperature_buffer){
            int[] temperature_addr = DecompressToTuple.decompressToValue(temperature_bytes);
            int temperature = temperature_addr[0];
            int addr = temperature_addr[1];
            int[] arr = {temperature, addr};
            result.add(arr);
        }
        return result;
    }

    //{humidity, addr}
    public ArrayList<int[]> getBlockHumidity(int fid){
        ArrayList<byte[]> humidity_buffer = this.columnStoreDAO.readOneFile(3, fid);

        ArrayList<int[]> result = new ArrayList<>();

        for(byte[] humidity_bytes: humidity_buffer){
            int[] humidity_addr = DecompressToTuple.decompressToValue(humidity_bytes);
            int humidity = humidity_addr[0];
            int addr = humidity_addr[1];
            int[] arr = {humidity, addr};
            result.add(arr);
        }
        return result;
    }

    public int getSize(){
        return this.temperatureCol_buffer.size() + getNumBlock()*Constants.BUFFERSIZE;
    }

    public int getNumBlock() {
        return this.columnStoreDAO.getNumBlock();
    }

    
}