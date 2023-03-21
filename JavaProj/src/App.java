import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            App app = new App();
            app.displayMenu(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void displayMenu(int type) throws Exception {

        if (type == 1) {
            System.out.println("======================================================================================");
            System.out.println("            << Welcome to Group X Project 1 >>");
            System.out.println();
            System.out.println("What would you like to do?");
            System.out.println("1) Initialize DB \n2) Exit");
            System.out.println("======================================================================================");
            System.out.print("You have selected: ");
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();

            switch (input) {
                case "1":
                    DB db = new DB();
                    db.readCSV();
                    break;
                case "2":
                    System.exit(0);
            }
            
        } else {
            String input;
            do {
                System.out.println("======================================================================================");
                System.out.println("Enter Metric Number");
                System.out.println("(exit): Exit ");
                System.out.println("======================================================================================");
                System.out.print("Selection: ");
                try (Scanner in = new Scanner(System.in)) {
                    input = in.nextLine();
                }

            } while (!input.equals("exit"));

        }


    }














}


