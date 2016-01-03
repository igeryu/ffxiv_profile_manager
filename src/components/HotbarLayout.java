package components;


import java.io.File;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alan
 */
public class HotbarLayout implements Component {
    private ArrayList <String> tags;
    
    //  TODO:  Figure out best way to optimize these into a collection:
    private final String DATA_FILENAME1 = "hotbar.dat";
    private final String DATA_FILENAME2 = "macro.dat";
    private String dataDirString1;
    private String dataDirString2;
    private File data1;
    private File data2;
    
    //  TODO:  Determine if the default constructor is needed.
    public HotbarLayout() {
        tags = new ArrayList<>();
    }
    
    //  TODO: Finish this:
    public HotbarLayout(String cID, String dir) {
        this();
        
        dataDirString1 = dir + cID + "\\" + DATA_FILENAME1;
        dataDirString2 = dir + cID + "\\" + DATA_FILENAME2;
        
    }
    
    
    //  TODO:  This should only be done when switching profiles, to backup the
    //         current (now previous) profile.
    private boolean getData () {
        File file1;
        File file2;
        
        try {
            file1 = new File(dataDirString1);
            file2 = new File(dataDirString2);
            
            data1 = file1;
            data2 = file2;
            
        } catch (Exception e) {
            System.out.println("HotbarLayout.getData() exception!");
            return false;
        }
        return true;
    }
    
    @Override
    public ArrayList<String> getTags (){
        return null;
    }
}
