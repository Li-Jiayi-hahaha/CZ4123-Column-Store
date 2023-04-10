import DatabaseManager.DataBaseMgr;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        DataBaseMgr dataBaseMgr = new DataBaseMgr();
        try {
            dataBaseMgr.createZoneMap();
            dataBaseMgr.printZoneMap();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            App app = new App();
            app.displayMenu();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayMenu() throws Exception {

        System.out.println("======================================================================================");
        System.out.println("            << Welcome to Group X Project 1 >>");
        System.out.println();
        System.out.println("What would you like to do?");
        System.out.println("1) Initialize DB \n2) Exit");
        System.out.println("======================================================================================");
        System.out.print("You have selected: ");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();

        if (input.equals("1")) {
            DB db = new DB();
            db.readCSV();

            do {
                System.out.println("\n\n======================================================================================");
                System.out.println("Enter Metric Number\n");
                System.out.println("(exit): Exit ");
                System.out.println("======================================================================================");
                System.out.print("Selection: ");
                input = in.nextLine();

                if (input.length() == 9 && input.charAt(0) == 'U'){
                    Output out = new Output();
                    out.exportToCsv(db.getArray("Changi"));
                }
                else if(input.equals("exit")){
                    System.out.println("Thank You!");
                }
                else{
                    System.out.println("Wrong input, Try again");
                }



            } while (!input.equals("exit"));



        } else {
            System.exit(0);
        }

    }

}
