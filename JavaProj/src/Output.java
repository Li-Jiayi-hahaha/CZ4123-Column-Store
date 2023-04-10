import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;

public class Output {

    public void exportDataBaseToCsv(List<String> Array) throws IOException {
        String csvFilePath = "javaProj/src/ReproducedTable.csv";
        
        PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath));
        // Write header row
        String[] header = { "id", "Timestamp", "Station", "Temperature", "Humidity" };
        writer.println(String.join(",", header));


        for ( String row : Array ){
            writer.println(row);
        }

        writer.close();
    }

    public void exportString(String str) throws IOException {

        String csvFilePath = "javaProj/src/output.csv";
        
        PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath));
        // Write str
        writer.println(str);

        writer.close();
    }

}