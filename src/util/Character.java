/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import components.Profile;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Alan
 */
public class Character {
    private HashMap <String, ArrayList<Backup>> profileBackups;
    private String name;
    private String id;
    
    
    
    /**
     * 
     * @return 
     */
    public String getName() {
        return name;
    }
    
    
    
    /**
     * 
     * @return 
     */
    public String getId() {
        return id;
    }
    
    
    
    /**
     * TODO:  Fill this in.
     * 
     * Loads profile by asking user to select one of the profile backups of held
     * by this character object.
     * 
     * @return     Latest profile backup of the profile selected by the user.
     */
    public Profile loadProfile () {
        
        //  TODO:  Ask user to select the profile they want to load
        
        /**
         *   TODO:  Ask the user if they want the latest backup of that profile,
         *          or an older one.
         */
        
        /**
         * TODO:  Load the profile selected by the user into memory and
         *        overwrite the active profile folder files with those in the
         *        backup folder.
        */
        
        return null;
    }
    
}
