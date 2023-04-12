package Disk;

import java.util.ArrayList;

public class ZoneMap{
    private static ZoneMap single_instance = null;

    //int[]: min_monthDis, max_monthDis of one block
    private final ArrayList<int[]> zonemaps;

    public ZoneMap(){
        zonemaps = new ArrayList<>();
    }

    public static synchronized ZoneMap getInstance()
    {
        if (single_instance == null){
            single_instance = new ZoneMap();
        }
        return single_instance;
    }

    public void addOneBlock(ArrayList<int[]> idYearMonth){
        int minMonthDis = 999999;
        int maxMonthDis = -1;

        for(int[] arr: idYearMonth){
            int year = arr[1];
            int month = arr[2];
            int monthDis = computeMonthDis(year, month);

            if(monthDis < minMonthDis) minMonthDis = monthDis;
            if(monthDis > maxMonthDis) maxMonthDis = monthDis;
        }

        int[] onezone = {minMonthDis, maxMonthDis};
        zonemaps.add(onezone);
    }

    private int computeMonthDis(int year, int month){
        //computes month difference from 2002-0 (not exist) to year-month
        int yeardis = year - Constants.STARTYEAR;
        return yeardis*12 + month;
    }

    public ArrayList<Integer> getOverlapedBlocks(int year, int month){
        int monthDis = computeMonthDis(year, month);

        ArrayList<Integer> result = new ArrayList<>();

        for(int i=0;i<zonemaps.size();i++){
            int[] arr = zonemaps.get(i);
            //for block i, the time range: [minmonthdis, maxmonthdis]
            if(monthDis >= arr[0] && monthDis <= arr[1]){
                result.add(i);
            }
        }
        return result;
    }
}