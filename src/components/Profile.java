/**
 * changelog:
 * 2016-02-01 : Refactored.
 */

package components;


import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alan Johnson
 */
public class Profile implements Component {
    public  String name;
    private String characterName;
    private String characterID;
    private String directory;
    
    private ArrayList <String> tags;
    private Gearset gearset;
    private HotbarLayout hotbarLayout;
    private Keybind keybind;
    //  TODO: Add 'screenshot' property here
    private UiLayout uiLayout;
    
    
    /**
     * TODO:  Determine if 'dir' should become a File held in ProfileManager, or remain just a String argument passed to Profile class...
     * TODO:  This should iterate through all backup folders and locate each one that belongs to this profile.
     * TODO:  Determine if the <code>JSON</code> file should maintain backup names, or if identifying them each time is more efficient/enough.
     * @param n         Profile name
     * @param cName     Character name
     * @param cID       Character ID
     * @param dir       FFXIV install directory
     */
    public Profile (String n, String cName, String cID, String dir) {
        name = n;
        characterName = cName;
        characterID = cID;
        directory = dir + cID;
        
        loadProfileData();
    }
    
    //  TODO:  Determine if 'dir' should become a File held in ProfileManager,
    //         or remain just a String argument passed to Profile class...
    /**
     * 
     * @param n               Profile name
     * @param cName           Character name
     * @param cID             Character ID
     * @param dir             FFXIV install directory
     * @param backupNames     List of backup names, possibly useless.
     */
    public Profile (String n, String cName, String cID, String dir, ArrayList<String> backupNames) {
        name = n;
        characterName = cName;
        characterID = cID;
        directory = dir + cID;
        
        loadProfileData();
    }
    
    private boolean loadProfileData () {
        gearset      = new Gearset     (characterID, directory);
        hotbarLayout = new HotbarLayout(characterID, directory);
        keybind      = new Keybind     (characterID, directory);
        uiLayout     = new UiLayout    (characterID, directory);
        return true;
    }
    
    @Override
    public ArrayList <String> getTags () {
        return tags;
    }
}
