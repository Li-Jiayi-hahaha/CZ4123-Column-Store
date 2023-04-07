import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;

public class Output {

    public void exportToCsv(List<String[]> Array) throws IOException {
        String csvFilePath = "output.csv";
        
        PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath));
        // Write header row
        String[] header = { "id", "Timestamp", "Station", "Temperature", "Humidity" };
        writer.println(String.join(",", header));


        for ( String[] row : Array ){
            writer.println(String.join(",", row));
        }

        writer.close();
    }

}