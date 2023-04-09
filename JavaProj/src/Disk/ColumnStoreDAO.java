package Disk;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ColumnStoreDAO{

    private static ColumnStoreDAO single_instance = null;

    //example of file path: javaProj/src/DiskStorage/id/0.bin
    private final String[] folder_names = {"id/", "daytime/", "addrYearMonth/", "temperature/", "humidity/"};
    private final int[] item_sizes = {3,2,2,2,2};
    private final String folder_path_prefix = "javaProj/src/DiskStorage/";
    private final int buffer_size = 2000;
    private int[] num_files;

    public ColumnStoreDAO(){
        num_files = new int[]{0,0,0,0,0};
    }

    public static synchronized ColumnStoreDAO getInstance()
    {
        if (single_instance == null)
            single_instance = new ColumnStoreDAO();
  
        return single_instance;
    }

    public int getNumBlock() {return num_files[0];}

    public void addOneCol(int cid, ArrayList<byte[]> buffer){
        String filename = Integer.toString(num_files[cid]) + ".bin";
        String filepath = folder_path_prefix + folder_names[cid] + filename;
        File file = new File(filepath);

        try {
            OutputStream os = new FileOutputStream(file);
            for(byte[] arr: buffer) os.write(arr);
            os.close();
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }

        num_files[cid] += 1;
    }

    public ArrayList<byte[]> readOneFile(int cid, int fid){
        String filename = Integer.toString(fid) + ".bin";
        String filepath = folder_path_prefix + folder_names[cid] + filename;
        File file = new File(filepath);
        int num_bytes = (int) file.length();
        byte[] data = new byte[num_bytes];

        try {
            DataInputStream stream = new DataInputStream(new FileInputStream(file));
            stream.readFully(data);
            stream.close();
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }

        ArrayList<byte[]> output_buffer = new ArrayList<byte[]>();

        for(int i=0;i<num_bytes/item_sizes[cid];i++){
            byte[] item;
            if(item_sizes[cid] == 2) item = new byte[]{data[2*i], data[2*i+1]};
            else item = new byte[]{data[3*i], data[3*i+1], data[3*i+2]};

            output_buffer.add(item);
        }

        return output_buffer;
    }
}
