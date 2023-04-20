import Disk.Disk;
import Disk.ZoneMap;
import Disk.Constants;
import Disk.MinMaxValues;
import Disk.WeatherDataTuple;

import java.util.ArrayList;
import java.util.Collections;
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

    public ArrayList<String> queryYearMonth(int year, int month, int addr){
        ArrayList<String> result = new ArrayList<>();

        int[] min_max_vals = minMaxValues.queryMinMaxValues(year, month);

        int min_temp = min_max_vals[addr*4 + 0];
        int max_temp = min_max_vals[addr*4 + 1];
        int min_hum = min_max_vals[addr*4 + 2];
        int max_hum = min_max_vals[addr*4 + 3];

        int[] values = {min_temp, max_temp, min_hum, max_hum};

        ArrayList<HashSet<Integer>> registerd_days = new ArrayList<>(4);
        
        for (int i = 0; i < 4; i++) {
            HashSet<Integer> set = new HashSet<>();
            registerd_days.add(set);
        }

        ArrayList<Integer> blocks = zoneMap.getOverlapedBlocks(year, month);

        for(int fid: blocks){
            //ArrayList<WeatherDataTuple> tuples = disk.getBlock(fid);

            ArrayList<int[]> idYearMonth = disk.getBlockIdYearMonth(fid);
            HashSet<Integer> filter_id = new HashSet<Integer>();

            for(int[] arr: idYearMonth){
                int t_id = arr[0], t_year = arr[1], t_month =arr[2];
                if(t_year == year && t_month == month){
                    filter_id.add(t_id % Constants.BUFFERSIZE);
                }
            }

            // pid: 0: min_temp, 1: max_temp, 2: min_hum, 3:max_hum

            ArrayList<int[]> id_pid = new ArrayList<int[]>();

            ArrayList<int[]> temp_addrs = disk.getBlockTemperature(fid);
            for(int i=0; i<temp_addrs.size();i++){
            
                int[] temp_addr = temp_addrs.get(i);
                int t_temp = temp_addr[0], t_addr = temp_addr[1];
                if(t_addr == addr){
                    if(filter_id.contains(i)){
                        
                        if(t_temp == min_temp) {
                            int[] arr = {i, 0};
                            id_pid.add(arr);
                        }

                        if(t_temp == max_temp) {
                            int[] arr = {i, 1};
                            id_pid.add(arr);
                        }
                    }
                }
            }

            ArrayList<int[]> hum_addrs = disk.getBlockHumidity(fid);
            for(int i=0; i<hum_addrs.size();i++){
            
                int[] hum_addr = hum_addrs.get(i);
                int t_hum = hum_addr[0], t_addr = hum_addr[1];
                if(t_addr == addr){
                    if(filter_id.contains(i)){
                        
                        if(t_hum == min_hum) {
                            int[] arr = {i, 2};
                            id_pid.add(arr);
                        }

                        if(t_hum == max_hum) {
                            int[] arr = {i, 3};
                            id_pid.add(arr);
                        }
                    }
                }
            }

            ArrayList<int[]> daytimes = disk.getBlockDayTime(fid);

            for(int[] arr: id_pid){
                int id = arr[0], pid = arr[1];
                int[] daytime = daytimes.get(id);
                int day = daytime[0];

                if(!registerd_days.get(pid).contains(day)){
                    registerd_days.get(pid).add(day);
                    result.add(processLine(year, month, day, addr, pid, values[pid]));
                }
            }

        }
        Collections.sort(result);

        return result;
    }
 
    private String processLine(int year, int month, int day, int addr, int pid, int value){
        String[] properties = {"Min Temperature", "Max Temperature", "Min Humidity", "Max Humidity"};

        String date_str = WeatherDataTuple.getDateString(year, month, day);
        String station = Constants.addr2Station(addr);
        String value_str = WeatherDataTuple.valToString(value);

        return date_str + ","  + station + "," + properties[pid] + "," + value_str;
    }

    //{addr, year1, year2}
    public ArrayList<Integer> metricToLocationYear (String input){
        ArrayList<Integer> result = new ArrayList<>();

        char seventhChar = input.charAt(6);
        char eighthChar = input.charAt(7);
        if (seventhChar % 2 == 0) {
            result.add(0);
        } else {
            result.add(1);
        }

        for (int year = 2002; year <= 2021; year++) {
            if (Integer.toString(year).charAt(3) == eighthChar){
                result.add(year);
            }
        }
        
        return result;

    }
}