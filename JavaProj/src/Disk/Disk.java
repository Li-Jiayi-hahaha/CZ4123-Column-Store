package Disk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Disk implemented in Column Store manner
 */
public class Disk {
    private static final String NaN = "M";
    private final String filePath;
    private final ArrayList<byte[]> idCol;
    private final ArrayList<byte[]> timestampCol;
    private final ArrayList<byte[]> stationCol;
    private final ArrayList<byte[]> temperatureCol;
    private final ArrayList<byte[]> humidityCol;

    private final HashMap<Integer, ZoneMapMetaData> zoneMap;

    public Disk(String filePath) {
        this.filePath = filePath;
        this.idCol = new ArrayList<>();
        this.timestampCol = new ArrayList<>();
        this.stationCol = new ArrayList<>();
        this.temperatureCol = new ArrayList<>();
        this.humidityCol = new ArrayList<>();
        this.zoneMap = new HashMap<>();
        this.init();
    }

    private void init() {
        try {
            ArrayList<WeatherDataTuple> table = this.loadCSV();
            ArrayList<WeatherDataTuple> cTemp = new ArrayList<>();
            ArrayList<WeatherDataTuple> pTemp = new ArrayList<>();

            // Sorts the table by Station
            for (WeatherDataTuple tuple : table) {
                String station = tuple.getStation();
                if (station.equals("Changi")) {
                    cTemp.add(tuple);
                } else if (station.equals("Paya Lebar")) {
                    pTemp.add(tuple);
                }
            }
            // Sort by Date
            cTemp.sort(new TimestampComparator());
            pTemp.sort(new TimestampComparator());
            // Join changi and paya lebar ArrayList
            cTemp.addAll(pTemp);

            this.compressAndWriteToColumnStore(cTemp);
        } catch (IOException e) {
            System.out.println("An error occurred while loading data to disk!");
            e.printStackTrace();
        }
    }
    private ArrayList<WeatherDataTuple> loadCSV() throws IOException {
        ArrayList<WeatherDataTuple> data = new ArrayList<>();

        System.out.printf("Loading data from %s to disk...\n", this.filePath);

        File file = new File(this.filePath);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String[] splitAttr;
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            splitAttr = line.split(",");
            // Ignore Headers
            if (splitAttr[0].equals("id")) continue;
            WeatherDataTuple tuple = new WeatherDataTuple(stringToInt(splitAttr[0]), stringToDate(splitAttr[1]), splitAttr[2], stringToFloat(splitAttr[3]), stringToFloat(splitAttr[4]));
            data.add(tuple);
        }
        bufferedReader.close();
        return data;
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

    private Float stringToFloat(String val) {
        if (val.equals(NaN)) return null;
        return Float.parseFloat(val);
    }

    // For now only compress Station attribute
    private void compressAndWriteToColumnStore(ArrayList<WeatherDataTuple> table) {
        System.out.println("Compressing and Writing to Column Store on Disk...");
        for (WeatherDataTuple tuple : table) {
            String station = tuple.getStation();
            String stationCompressed = "M";
            if (station.equals("Changi")) {
                stationCompressed = "C";
            } else if (station.equals("Paya Lebar")) {
                stationCompressed = "P";
            }
//            System.out.println(tuple);
            this.writeToColumnStore(tuple.getId(), tuple.getTimestamp(), stationCompressed, tuple.getTemperature(), tuple.getHumidity());
        }

        System.out.println("Data successfully written into Disk Column Store!");
    }

    private void writeToColumnStore(int id, Date timestamp, String station, Float temperature, Float humidity) {
        this.idCol.add(Integer.toString(id).getBytes());
        this.timestampCol.add(timestamp.toString().getBytes());
        this.stationCol.add(station.getBytes());
        this.temperatureCol.add(temperature == null ? NaN.getBytes() : Float.toString(temperature).getBytes());
        this.humidityCol.add(humidity == null ? NaN.getBytes() : Float.toString(humidity).getBytes());
    }

    public Row getRow(int index) {
        byte[] id = this.idCol.get(index);
        byte[] timestamp = this.timestampCol.get(index);
        byte[] station = this.stationCol.get(index);
        byte[] temperature = this.temperatureCol.get(index);
        byte[] humidity = this.humidityCol.get(index);
        return new Row(id, timestamp, station, temperature, humidity);
    }

    public byte[] getItemAtColumnOf(int index, ColumnHeader header) {
        switch (header) {
            case ID: return this.idCol.get(index);
            case TIMESTAMP: return this.timestampCol.get(index);
            case STATION: return this.stationCol.get(index);
            case TEMPERATURE: return this.temperatureCol.get(index);
            case HUMIDITY: return this.humidityCol.get(index);
            default: return new byte[] {};
        }
    }

    public boolean checkSize() {
        return (idCol.size() == timestampCol.size() && timestampCol.size() == stationCol.size() && stationCol.size() == temperatureCol.size() && temperatureCol.size() == humidityCol.size());
    }

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
                prevYear = year;
                ZoneMapMetaData zoneMapMetaData = new ZoneMapMetaData(lowerIndex, i - 1, stn);
                zoneMap.put(key, zoneMapMetaData);
                lowerIndex = i;
                key++;
            }

            if (!stn.equals(prevStation)) {
                ZoneMapMetaData zoneMapMetaData = new ZoneMapMetaData(lowerIndex, i - 1, prevStation);
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
                prevYear = year;
                ZoneMapMetaData zoneMapMetaData = new ZoneMapMetaData(lowerIndex, i - 1, stn);
                zoneMap.put(key, zoneMapMetaData);
                lowerIndex = i;
                key++;
            }
            if (i == timestampCol.size() - 1) {
                prevYear = year;
                ZoneMapMetaData zoneMapMetaData = new ZoneMapMetaData(lowerIndex, i -1, stn);
                zoneMap.put(key, zoneMapMetaData);
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
}