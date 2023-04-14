import Disk.Disk;
import Disk.ZoneMap;
import Disk.Constants;
import Disk.MinMaxValues;
import Disk.WeatherDataTuple;

import java.util.ArrayList;
import java.util.HashSet;

public class QueryMgr{
    private static QueryMgr single_instance = null;

    private final Disk disk;
    private final ZoneMap zoneMap;
    private final MinMaxValues minMaxValues;

    public QueryMgr(){
        this.disk = Disk.getInstance();
        this.zoneMap = ZoneMap.getInstance();
        this.minMaxValues = MinMaxValues.getInstance();
    }

    public static synchronized QueryMgr getInstance()
    {
        if (single_instance == null){
            single_instance = new QueryMgr();
        }
        return single_instance;
    }

    public ArrayList<String> queryYearMonth(int year, int month){
        ArrayList<String> result = new ArrayList<>();

        int[] min_max_vals = minMaxValues.queryMinMaxValues(year, month);

        int[] min_temp = {min_max_vals[0], min_max_vals[4]};
        int[] max_temp = {min_max_vals[1], min_max_vals[5]};
        int[] min_hum  = {min_max_vals[2], min_max_vals[6]};
        int[] max_hum  = {min_max_vals[3], min_max_vals[7]};

        ArrayList<HashSet<Integer>> registerd_days = new ArrayList<>(4);
        
        for (int i = 0; i < 4; i++) {
            HashSet<Integer> set = new HashSet<>();
            registerd_days.add(set);
        }

        ArrayList<Integer> blocks = zoneMap.getOverlapedBlocks(year, month);

        for(int fid: blocks){
            ArrayList<WeatherDataTuple> tuples = disk.getBlock(fid);

            for(WeatherDataTuple tuple: tuples){
                if(tuple.getYear() == year && tuple.getMonth() == month){
                    int addr = tuple.getStation().equals("Changi")? 0:1;

                    int temp = tuple.getTemperature();
                    int hum = tuple.getHumidity();

                    int day = tuple.getDay();

                    if(temp == min_temp[addr]){
                        int pid = 0;
                        if(!registerd_days.get(pid).contains(day)){
                            registerd_days.get(pid).add(day);
                            result.add(processTuple(tuple, pid));
                        }
                        
                    }
                    if(temp == max_temp[addr]){
                        int pid = 1;
                        if(!registerd_days.get(pid).contains(day)){
                            registerd_days.get(pid).add(day);
                            result.add(processTuple(tuple, pid));
                        }
                    }

                    if(hum == min_hum[addr]){
                        int pid = 2;
                        if(!registerd_days.get(pid).contains(day)){
                            registerd_days.get(pid).add(day);
                            result.add(processTuple(tuple, pid));
                        }
                    }
                    if(hum == max_hum[addr]){
                        int pid = 3;
                        if(!registerd_days.get(pid).contains(day)){
                            registerd_days.get(pid).add(day);
                            result.add(processTuple(tuple, pid));
                        }
                    }
                }
            }

        }

        return result;
    }

    private String processTuple(WeatherDataTuple tuple, int pid){
        String[] properties = {"Min Temperature", "Max Temperature", "Min Humidity", "Max Humidity"};

        int value = pid < 2? tuple.getTemperature() : tuple.getHumidity();
        String value_str = WeatherDataTuple.valToString(value);

        return tuple.getDateString() + ","  + tuple.getStation() + "," + properties[pid] + "," + value_str;
    }

    public ArrayList<String> metricToLocationYear (String input){
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> yearList = new ArrayList<>();
        char seventhChar = input.charAt(6);
        char eighthChar = input.charAt(7);
        if (seventhChar % 2 == 0) {
            result.add("Changi");
        } else {
            result.add("Paya Lebar");
        }

        for (int year = 2002; year <= 2021; year++) {
            yearList.add(Integer.toString(year));
            if (Integer.toString(year).charAt(3) == eighthChar){
                result.add(Integer.toString(year));
            }
        }
        
        return result;

    }
}