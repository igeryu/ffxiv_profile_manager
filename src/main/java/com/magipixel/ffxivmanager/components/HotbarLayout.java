package com.magipixel.ffxivmanager.components;


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
    
    private final String[] DATA_FILENAMES = {
        "HOTBAR.dat",
        "MACRO.dat"
    };
    private String[] dataDirectoryStrings = new String[2];
    private File[] data = new File[2];
    
    //  TODO:  Determine if the default constructor is needed.
    public HotbarLayout() {
        tags = new ArrayList<>();
    }
    
    //  TODO: Finish this:
    public HotbarLayout(String cID, String dir) {
        this();
        
        for (int i = 0; i < DATA_FILENAMES.length; i++) {
            dataDirectoryStrings[i] = dir + cID + "\\" + DATA_FILENAMES[i];
        }
        
    }
    
    
    //  TODO:  This should only be done when switching profiles, to backup the
    //         current (now previous) profile.
    private boolean getData () {
        
        try {
            
            for (int i = 0; i < DATA_FILENAMES.length; i++) {
                data[i] = new File(dataDirectoryStrings[i]);
            }
            
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
