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
public class CharacterConfig implements Component {
    private ArrayList <String> tags;
    
    private final String DATA_FILENAMES[] = {
        "COMMON.dat",
        "CONTROL0.dat",
        "CONTROL1.dat",
        "ITEMFDR.dat",
        "ITEMODR.dat",
        "LOGFLTR.dat",
        "UISAVE.dat"
    };
    
    private String[] dataDirectoryStrings = new String[7];
    
    private File[] data = new File[7];
    
    //  TODO:  Determine if the default constructor is needed.
    public CharacterConfig() {
        tags = new ArrayList<>();
    }
    
    //  TODO: Finish this:
    public CharacterConfig(String cID, String dir) {
        this();
        
        for (int i = 0; i < DATA_FILENAMES.length; i++) {
            dataDirectoryStrings[i] = dir + cID + "\\" + DATA_FILENAMES[i];
        }
        
    }
    
    
    //  TODO:  This should only be done when switching profiles, to backup the
    //         current (now previous) profile.
    private boolean getData () {
        
        try {
            
            for (int i = 0; i < dataDirectoryStrings.length; i++) {
                data[i] = new File(dataDirectoryStrings[i]);
                
            }
            
        } catch (Exception e) {
            System.out.println("Character.getData() exception!");
            return false;
        }
        return true;
    }
    
    @Override
    public ArrayList<String> getTags (){
        return null;
    }
}
