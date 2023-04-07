import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
// import java.util.Collections;
// import java.util.Comparator;
import java.util.List;

import javax.lang.model.util.ElementScanner6;

public class DB {

    private List<String[]> sortedData;
    private List<String[]> dataChangi;
    private List<String[]> dataPayaLebar;

    public DB() {
        this.dataChangi = new ArrayList<>();
        this.dataPayaLebar = new ArrayList<>();
        this.sortedData = new ArrayList<>();
    }
    
    public void readCSV(){
        String csvFile = "SingaporeWeather.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] row = line.split(cvsSplitBy);

                //Simple Sorting
                if (row[2].equals("Changi")){
                    dataChangi.add(row);
                    // System.out.println("C ADDED");
                }
                if (row[2].equals("Paya Lebar")){
                    dataPayaLebar.add(row);
                    // System.out.println("P ADDED");
                }

                // // Process row data
                // for (String value : row) {
                //     System.out.print(value + ", ");
                // }
                // System.out.println();

            }
            // for (String[] row : data){
            //     System.out.println("=============================== DATA ===================================");
            //     for (String value : row) {
            //         System.out.print(value + ", ");
            //     }
            //     System.out.println();
            // }

        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void sortArray(List<String[]> Array){
        //2003-02-02 00:40
        //2002-01-01 00:30

        //0 year - 2021
        //1 month - 12
        //2 date - 31
        //3 hour - 23
        //4 minutes - 30
        String[] date;
        String[] temp;
        List<String[]> data = new ArrayList<>();

        date = splitDate((Array.get(1))[1]);
        for ( String[] row : Array ){
            temp = splitDate(row[1]);
            if(Integer.parseInt(date[0]) <= Integer.parseInt(temp[0])){
                if(Integer.parseInt(date[1]) <= Integer.parseInt(temp[1])){
                    if(Integer.parseInt(date[2]) <= Integer.parseInt(temp[2])){
                        if(Integer.parseInt(date[3]) <= Integer.parseInt(temp[3])){
                            break;
                        }
                    }
                }
            }
            data.add(row);
        }
    }

    public String[] splitDate(String date){
        String[] parts = date.split("[-| |:]");

        for (String part : parts) {
            System.out.println(part);
        }
        
        return parts;
    }

    public void printArray(String location){
        System.out.println(dataChangi.get(1));
        if(location == "Changi"){
            System.out.println("=============================== Changi ===================================");
            for ( String[] row : dataChangi ){
                for (String value : row) {
                    System.out.print(value + ", ");
                }
                System.out.println();
            }

        }
        else {
            System.out.println("=============================== PAYA ===================================");
            for ( String[] row : dataPayaLebar ){
                for (String value : row) {
                    System.out.print(value + ", ");
                }
                System.out.println();
            }
        }
    }

    public List<String[]> getArray(String location){
        if(location == "Changi"){
            return dataChangi;
        }
        else if (location == "PayaLebar"){
            return dataPayaLebar;
        }
        else{
            return sortedData;
        }
    }
    
}
