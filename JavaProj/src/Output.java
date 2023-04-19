import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Output {

    private String filepath;

    public void updateFilePath(String input){

        this.filepath = "ScanResult_" + input + ".csv";

    }

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

    public void writeQueryHeader() throws IOException {

        // String csvFilePath = "javaProj/src/output.csv";
        String csvFilePath = filepath;

        PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath));
        // Write str
        writer.println("Date,Station,Category,Value");

        writer.close();
    }

    public void appendQueryResults(List<String> strs) throws IOException {

        String csvFilePath = filepath;
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath, true));

        // Write strs
        for(String line: strs){
            writer.append(line + "\n");
        }

        writer.close();
    }


}