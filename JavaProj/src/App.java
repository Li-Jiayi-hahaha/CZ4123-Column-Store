import Disk.Disk;

import java.util.ArrayList;
import java.util.Scanner;

public class App {

    private final Disk dataBaseMgr;
    private final QueryMgr queryMgr;
    private final Output writer;

    public static void main(String[] args) {
        System.out.println("Hello, World!");

        try {
            App app = new App();
            app.displayMenu();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public App() {
        this.dataBaseMgr = Disk.getInstance();
        this.queryMgr = QueryMgr.getInstance();
        this.writer = new Output();
    }

    private boolean checkMetricFormat(String metric){ 
        if(metric.length() != 9) return false;
        if(metric.charAt(0)!='U') return false;
        if(metric.charAt(8) < 'A' || metric.charAt(8) > 'Z') return false;

        for(int i=1;i<8;i++){
            if(metric.charAt(i) < '0' || metric.charAt(i) > '9') return false;
        }

        return true;
    }
        

    public void displayMenu() throws Exception {

        System.out.println("======================================================================================");
        System.out.println("            << Welcome to Group X Project 1 >>");
        System.out.println();
        
        String input = "";

        do {
            System.out.println("\n======================================================================================");
            System.out.println("What would you like to do next?");
            System.out.println("1) Query By Matric Number");
            System.out.println("2) Output the Database to CSV in original format");
            System.out.println("3) exit");
            System.out.println("======================================================================================");
            System.out.print("Your selection is: ");
            Scanner in = new Scanner(System.in);
            input = in.nextLine();

            if (input.equals("1")) {
                System.out.println("\n======================================================================================");
                System.out.println("Enter Metric Number to query:");
                input = in.nextLine();
                System.out.println("======================================================================================");

                if (checkMetricFormat(input)){
                    
                    writer.updateFilePath(input);
                    ArrayList<String> resultsAll = new ArrayList<>();
                    writer.writeQueryHeader();
                    ArrayList<Integer> locYear = queryMgr.metricToLocationYear(input);
                    int year1=locYear.get(1), year2=locYear.get(2);
                    int address = locYear.get(0);
                    
                    for (int month = 1; month <= 12; month++) {
                        ArrayList<String> results = queryMgr.queryYearMonth(year1, month, address);
                        resultsAll.addAll(results);
                    }

                    for (int month = 1; month <= 12; month++) {
                        ArrayList<String> results = queryMgr.queryYearMonth(year2, month, address);
                        resultsAll.addAll(results);
                    }
                    writer.appendQueryResults(resultsAll);
                    
                    System.out.println("\nThe query results is exported to output.csv.\n");
                }
                else{
                    System.out.println("\nThe input is not in the correct format of a matric number\n");
                }
            }

            if (input.equals("2")) {

                ArrayList<String> rows = dataBaseMgr.getAllRowsString();
                writer.exportDataBaseToCsv(rows);

                System.out.println("\nThe data is exported to ReproducedTable.csv.\n");
                
            }

        } while (!input.equals("3"));

        System.exit(0);

    }

}
