import DatabaseManager.DataBaseMgr;

public class App {
    public static void main(String[] args)  {
        System.out.println("Hello, World!");
        DataBaseMgr dataBaseMgr = new DataBaseMgr();
        try {
            dataBaseMgr.createZoneMap();
            dataBaseMgr.printZoneMap();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
