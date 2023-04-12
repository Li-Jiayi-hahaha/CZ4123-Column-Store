package Disk;

import java.util.ArrayList;
import java.util.Collections;

public class MinMaxValues {
    private static MinMaxValues single_instance = null;

    //first_half: Changi, second_half: Paya Lebar
    private final ArrayList<Integer> minTemperatures;
    private final ArrayList<Integer> maxTemperatures;
    private final ArrayList<Integer> minHumidity;
    private final ArrayList<Integer> maxHumidity;

    private final int n;

    public MinMaxValues(){
        n = 12*(2021 - Constants.STARTYEAR) + 12;

        minTemperatures = new ArrayList<>();
        maxTemperatures = new ArrayList<>();

        minHumidity = new ArrayList<>();
        maxHumidity = new ArrayList<>();

        for(int i=0; i<2*n+2; i++){
            minTemperatures.add(10200);
            minHumidity.add(10200);

            maxTemperatures.add(-1);
            maxHumidity.add(-1);
        }

    }

    public static synchronized MinMaxValues getInstance()
    {
        if (single_instance == null){
            single_instance = new MinMaxValues();
        }
        return single_instance;
    }

    public void addTuple(WeatherDataTuple tuple){
        int addr = tuple.getStation().equals("Changi")? 0:1;

        int year = tuple.getYear();
        int month = tuple.getMonth();
        int i = computeMonthDis(year, month);
        i = n*addr + i;

        int temperature = tuple.getTemperature();
        int humidity = tuple.getHumidity();

        if(temperature != 10100){
            if(temperature < minTemperatures.get(i)) minTemperatures.set(i, temperature);
            if(temperature > maxTemperatures.get(i)) maxTemperatures.set(i, temperature);
        }

        if(humidity != 10100){
            if(humidity < minHumidity.get(i)) minHumidity.set(i, humidity);
            if(humidity > maxHumidity.get(i)) maxHumidity.set(i, humidity);
        }
    }

    //return: {Changi, Paya Lebar} * {min_temp, max_temp, min_hum, max_hum}
    public int[] queryMinMaxValues(int year, int month){
        int i = computeMonthDis(year, month);

        int[] result = {minTemperatures.get(i), maxTemperatures.get(i), 
                        minHumidity.get(i), maxHumidity.get(i),
                        minTemperatures.get(n + i), maxTemperatures.get(n + i), 
                        minHumidity.get(n + i), maxHumidity.get(n + i)};
        return result;
    }

    private int computeMonthDis(int year, int month){
        //computes month difference from 2002-0 (not exist) to year-month
        //2002-1 i = 1
        //2003-1 i = (2003-2002)*12 + 1 = 13
        int yeardis = year - Constants.STARTYEAR;
        return yeardis*12 + month;
    }

}
