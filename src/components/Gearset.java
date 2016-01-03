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
public class Gearset implements Component {
    private ArrayList <String> tags;
    
//  addon.dat
    private final String DATA_FILENAME = "gearset.dat";
    private String dataDirString;
    private File data;
    
    //  TODO:  Determine if the default constructor is needed.
    public Gearset() {
        tags = new ArrayList<>();
    }
    
    //  TODO: Finish this:
    public Gearset(String cID, String dir) {
        this();
        
        dataDirString = dir + cID + "\\" + DATA_FILENAME;
        
        
    }
    
    
    //  TODO:  This should only be done when switching profiles, to backup the
    //         current (now previous) profile.
    private boolean getData (String dataDirString) {
        File file;
        
        try {
            file = new File(dataDirString);
            
            data = file;
            
        } catch (Exception e) {
            System.out.println("Gearset.getData() exception!");
            return false;
        }
        return true;
    }
    
    @Override
    public ArrayList<String> getTags (){
        return null;
    }
}
