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
    
    //  TODO:  Figure out best way to optimize these into a collection:
    private final String DATA_FILENAME1 = "common.dat";
    private final String DATA_FILENAME2 = "control0.dat";
    private final String DATA_FILENAME3 = "control1.dat";
    private final String DATA_FILENAME4 = "ITEMFDR.dat";
    private final String DATA_FILENAME5 = "ITEMODR.dat";
    private final String DATA_FILENAME6 = "LOGFLTR.dat";
    private final String DATA_FILENAME7 = "UISAVE.dat";
    
    private String dataDirString1;
    private String dataDirString2;
    private String dataDirString3;
    private String dataDirString4;
    private String dataDirString5;
    private String dataDirString6;
    private String dataDirString7;
    
    private File data1;
    private File data2;
    private File data3;
    private File data4;
    private File data5;
    private File data6;
    private File data7;
    
    //  TODO:  Determine if the default constructor is needed.
    public CharacterConfig() {
        tags = new ArrayList<>();
    }
    
    //  TODO: Finish this:
    public CharacterConfig(String cID, String dir) {
        this();
        
        dataDirString1 = dir + cID + "\\" + DATA_FILENAME1;
        dataDirString2 = dir + cID + "\\" + DATA_FILENAME2;
        dataDirString3 = dir + cID + "\\" + DATA_FILENAME3;
        dataDirString4 = dir + cID + "\\" + DATA_FILENAME4;
        dataDirString5 = dir + cID + "\\" + DATA_FILENAME5;
        dataDirString6 = dir + cID + "\\" + DATA_FILENAME6;
        dataDirString7 = dir + cID + "\\" + DATA_FILENAME7;
        
    }
    
    
    //  TODO:  This should only be done when switching profiles, to backup the
    //         current (now previous) profile.
    private boolean getData () {
        File file1;
        File file2;
        File file3;
        File file4;
        File file5;
        File file6;
        File file7;
        
        try {
            file1 = new File(dataDirString1);
            file2 = new File(dataDirString2);
            file3 = new File(dataDirString3);
            file4 = new File(dataDirString4);
            file5 = new File(dataDirString5);
            file6 = new File(dataDirString6);
            file7 = new File(dataDirString7);
            
            data1 = file1;
            data2 = file2;
            data3 = file3;
            data4 = file4;
            data5 = file5;
            data6 = file6;
            data7 = file7;
            
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
