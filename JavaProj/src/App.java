import DatabaseManager.DataBaseMgr;

import java.util.ArrayList;
import java.util.Scanner;

public class App {

    private final DataBaseMgr dataBaseMgr;

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
        this.dataBaseMgr = new DataBaseMgr();
        /* 
        try {
            this.dataBaseMgr.createZoneMap();
            this.dataBaseMgr.printZoneMap();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        */
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

                if (input.length() == 9 && input.charAt(0) == 'U'){
                    System.out.println("Query Function is not implemented yet.");
                }
                else{
                    System.out.println("The input is not in the correct format of a matric number");
                }
            }

            if (input.equals("2")) {
                
                Output out = new Output();
                ArrayList<String> rows = dataBaseMgr.getRows(0, dataBaseMgr.getSize()-1);
                out.exportDataBaseToCsv(rows);
                
                System.out.println("\nThe data is exported to ReproducedTable.csv.\n");
                
            }

        } while (!input.equals("3"));

        System.exit(0);

    }

}
