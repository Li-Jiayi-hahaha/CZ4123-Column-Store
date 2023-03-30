package Disk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Disk implemented in Column Store manner
 */
public class Disk {
    private static final String NaN = "M";
    private static final Charset charset = StandardCharsets.UTF_8;
    private final String filePath;

    private ArrayList<byte[]> idCol;
    private ArrayList<byte[]> timestampCol;
    private ArrayList<byte[]> stationCol;
    private ArrayList<byte[]> temperatureCol;
    private ArrayList<byte[]> humidityCol;

    public Disk(String filePath) {
        this.filePath = filePath;
        this.idCol = new ArrayList<>();
        this.timestampCol = new ArrayList<>();
        this.stationCol = new ArrayList<>();
        this.temperatureCol = new ArrayList<>();
        this.humidityCol = new ArrayList<>();
        this.init();
    }

    private void init() {
        try {
            ArrayList<WeatherDataTuple> table = this.loadCSV();
            ArrayList<WeatherDataTuple> cTemp = new ArrayList<>();
            ArrayList<WeatherDataTuple> pTemp = new ArrayList<>();

            // Sorts the table by Station then by Date
            for (WeatherDataTuple tuple : table) {
                String station = tuple.getStation();
                if (station.equals("Changi")) {
                    cTemp.add(tuple);
                } else if (station.equals("Paya Lebar")) {
                    pTemp.add(tuple);
                }
            }
            cTemp.sort(new TimestampComparator());
            pTemp.sort(new TimestampComparator());
            cTemp.addAll(pTemp);

//            printTable(cTemp.size(), cTemp);

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
        String format = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sf = new SimpleDateFormat(format);
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
            if (station.equals("Changi")) {
                this.writeToColumnStore(tuple.getId(), tuple.getTimestamp(), "C", tuple.getTemperature(), tuple.getHumidity());
            } else if (station.equals("Paya Lebar")) {
                this.writeToColumnStore(tuple.getId(), tuple.getTimestamp(), "P", tuple.getTemperature(), tuple.getHumidity());
            }
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

    // For debugging
    public void printTable(int head, ArrayList<WeatherDataTuple> table) {
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

    public ArrayList<byte[]> getIdCol() {
        return idCol;
    }

    public ArrayList<byte[]> getTimestampCol() {
        return timestampCol;
    }

    public ArrayList<byte[]> getStationCol() {
        return stationCol;
    }

    public ArrayList<byte[]> getTemperatureCol() {
        return temperatureCol;
    }

    public ArrayList<byte[]> getHumidityCol() {
        return humidityCol;
    }
}