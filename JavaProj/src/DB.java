import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
// import java.util.Collections;
// import java.util.Comparator;
import java.util.List;

public class DB {
    
    public void readCSV(){
        String csvFile = "SingaporeWeather.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        List<String[]> data = new ArrayList<>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] row = line.split(cvsSplitBy);

                // Process row data
                for (String value : row) {
                    System.out.print(value + ", ");
                }
                System.out.println();
                data.add(row);

            }

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
    
}
