
import components.Profile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import javax.swing.JOptionPane;
import util.Character;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import util.TagManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alan
 */
public class ProfileManager {
    
    //  Change this to be not only change able for non-default installs, but also
    //  for Mac OS.
    private static final String FFXIV_FOLDER = "C:\\Users\\Alan\\Documents\\My Games\\FINAL FANTASY XIV - A Realm Reborn\\";
    
    //private static final String DATA_FILE_NAME = "config.dat";
    private static final String DATA_FILE_NAME = "data.json";
    
    //  Assumes that whatever is active in the user directory is the
    //  application's active profile.
    private static Profile activeProfile;
    private static Character activeCharacter;
    private static String  activeCharacterName;
    private static String  activeCharacterID;
    
    //  TODO:  Need to fill this out.
    private static ArrayList<Character> characters;
    
    //  TODO:  Consider making this a local class:
    private static TagManager tagManager;
    
    
    public static void main(String[] args) {
        
//        JSONTest.writeJSON();
//        JSONTest.readJSON();
        
        //  Change this from hard-coded to dynamic:
        activeCharacterName = "Feyen";
        activeCharacterID = "FFXIV_CHR00400000009D4722";
        
        init();
        
        System.out.printf("Profile: %s\n", activeProfile.name);
        
        backupActiveProfile();
    }
    
    
    
    /**
     * 
     * @param name
     * @return 
     */
    private static Character getCharacter (String name) {
        //  TODO:  Fill this in!
        return null;
    }
    
    
    
    /**
     * Attempts to find the folder for the character name given in the
     * parameter.
     * 
     * @param name
     * @return 
     */
    private static boolean identifyCharacterFolder (String name) {
        
        /**
         * 1. Verify that the character is not already identified.  Return false
         *    if the character is already verified.
         */
        
        // TODO:  Add support for multiple characters of same name, but different servers.
        
        //  2. Locate folder by character name.  If successful, create a new
        //     Character object, call identifyProfiles() on it and add it to
        //     characers ArrayList, then return true.
        
        return false;
    }
    
    
    /**
    * Runs every time the application is opened.  Checks if there is a
    * <code>DATA_FILE_NAME</code> file (first run condition), and if so, it asks
    * the user to name their current profile.
    * 
    * TODO: Need to make this ask for <b>ALL</b> characters' full names and then identify each character's folder from the log files.
    */
    private static void init() {
        System.out.println("Initializing app");
        
        File file = null;
        try {
            file = new File(DATA_FILE_NAME);
        } catch (Exception e) {
            System.out.println("Exception!");
        }
        
        //  Check if this is first load:
        if (!file.exists()) {
            System.out.println(DATA_FILE_NAME + " does not exist.");
            
            initConfig(file);
            if (!scanBackups()) {
                System.out.println("Scanning of backups failed.");
                return;
            }
                
        } else {
            System.out.println(DATA_FILE_NAME + " exists.");
            
            if (!scanBackups()) {
                System.out.println("Scanning of backups failed.");
                return;
            }
            loadConfig(file);
        }
        
        //  change "test" to the name received earlier
        activeProfile = getActiveProfile(activeCharacterName);
        
    }  //  end int() method
    
    
    
    // TODO:  Need to fix initConfig(File, String) so that it creates the correct JSON file structure.
     
    /**
     * Runs during first-time setup.  Creates a new configuration file, who's
     * name was previously determined by <code>DATA_FILE_NAME</code>.
     * 
     * @param file     Configuration file, whose name is determined by
     *                 <code>DATA_FILE_NAME</code>.
     * 
     * @param name     Name of active profile, need to fix this.
     */
    //private static void initConfig (File file, String name) {
    private static void initConfig (File file) {
        
        System.out.println("Initializing " + DATA_FILE_NAME);
        
        try {
//            FileOutputStream out = new FileOutputStream(file);
//            PrintWriter writer = new PrintWriter(out);
//            writer.println(name);
//            writer.flush();
//            writer.close();
//            out.close();
        } catch (Exception e) {
            System.out.println("initConfig() exception!");
        }
    }

    
    
    /**
     * This runs during application startup, to initialize a
     * <code>Profile</code> object from the parameters of the last active
     * profile in the file identified by <code>DATA_FILE_NAME</code>.
     * 
     * TODO:  Add functionality to handle multiple characters by ID
     * 
     * @param name     The name of the last active profile, from
     *                 <code>DATA_FILE_NAME</code>.
     * 
     * @return         <code>Profile</code> object corresponding to the last
     *                 (and current) active profile
     */
    private static Profile getActiveProfile(String name) {
        
        System.out.println("Getting active profile from name");
        
        // TODO:  Make sure that activeCharacterName and activeCharacterID really will be set at this point, not just because they are hard-coded in main().
        
        return new Profile(name, activeCharacterName,
                           activeCharacterID, FFXIV_FOLDER);
    }
    
    
    
    /**
     * Runs when unidentified character folders are found by
     * <code>scanBackups()</code>.  Asks the user to name any character that is
     * not identified by the application yet (whether this is first-time setup
     * or later), and then the application will attempt to find the folder
     * belonging to that character.
     * 
     * @param setup     Currently depreciated.
     * @param id        Currently depreciated.
     * 
     * @return          the name of the profile, chosen by the user
     */
    //private static boolean chooseProfileName(boolean setup, String id) {
    private static boolean chooseProfileName() {
        
        //  From depreciated setup param:
        //System.out.println("Getting profile name. (" + (setup ? "first-time setup" : "new profile") + ")");
        
//        String name = "";
//        String prompt = "";
//        String promptTitle = "";
        
        //  From depreciated setup param:
//        if (setup) {
//            prompt = "Please provide a name for your current profile.";
//            promptTitle = "First-Time Setup";
//        } else {
//            prompt = "New character found "
//        }
        
        System.out.println("Getting profile name.");
        String name = "";
        String prompt = "Unidentified character found.\nPlease enter a name for"
                      + " any new character.";
        String promptTitle = "Unidentified Character";
        
        do {

            name = JOptionPane.showInputDialog(null, prompt,
                                               promptTitle, QUESTION_MESSAGE);

        } while (name.equals(""));
                
        System.out.println("\nLocating profile: " + name);
        
        return identifyCharacterFolder(name);

    }  // end chooseProfileName() method
    
    
    
    /**
     * Loads the configuration file, whose name is identified by
     * <code>DATA_FILE_NAME</code>, into memory.
     * 
     * <p><code>scanBackups()</code> is assumed to have already run before this
     * method runs.</p>
     * 
     * @param file     <code>File</code> object, whose name is determined by
     *                 <code>DATA_FILE_NAME</code>.
     * 
     * @return         <code>null String</code> TODO: fix this
     */
    //private static String loadConfig(File file) {
    private static boolean loadConfig(File file) {
        
        System.out.println("Loading " + DATA_FILE_NAME);
        
        Scanner scanner = null;
        
        try {
            scanner = new Scanner(file);
        } catch (Exception e) {
            System.out.println("loadConfig() exception!");
            return false;
        }
        
        if (scanner.hasNextLine()) {
            //return scanner.next();
            return true;
        }
        
        String name = "";
        //  TODO:  Store the active character name from the config file into 'name'.
        
        //  NOTE:  activeCharacterName and activeCharacterID must be known for
        //         getActiveProfile() to succeed.
        activeCharacterName = name;
        activeCharacter = getCharacter(name);
        activeCharacterID = activeCharacter.getId();
        activeProfile = getActiveProfile(name);
        
        
        return false;
    }
    
    
    
    //  @TODO:  Need to add functionality to honor user's inclusion/exclusion choices (include gearsets, exclude keybinds, etc.) from the profile and ergo the backup.
    
    /**
     * Backs up the active profile stored in <code>activeProfile</code>.  It
     * does this by creating a folder following the
     * "<code>yyyy-MM-dd-HH-mm</code>" format for dates and appends
     * <code>activeCharacterName</code>, <code>activeProfile.name</code> and
     * then "<code>Backup</code>".  Inside that folder, all selected components
     * are copied from the active directory.
     * 
     * @return <code>true</code> if the backup was successful,
     *         <code>false</code> otherwise.
     */
    private static boolean backupActiveProfile () {
        
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        String timestamp = dateFormat.format(cal.getTime());
        
        System.out.println("Timestamp: " + timestamp);
        
        String backupName = timestamp + " " + activeCharacterName
                                      + " " + activeProfile.name + " Backup";
        
        System.out.println("Backup Name: " + backupName);
        
        return true;
    }
    
    
    /**
     * TODO:  For now, this will simply overwrite the files in the current directory with whichever one(s) are in the backup being loaded.
     * 
     * Initiates profile load procedure by backing up the active profile, and
     * then calling the <code>loadProfile()</code> method on the
     * <code>character</code> parameter.
     * 
     * @return     
     */    
    private static boolean loadProfile (Character character) {
        
        backupActiveProfile();
        
        Profile loadedProfile = character.loadProfile();
        
        if (loadedProfile != null) {
            activeProfile = loadedProfile;
            activeCharacterName = character.getName();
            activeCharacterID = character.getId();
        }
        return true;
    }
    
    
    /**
     * TODO:  Build this!
     * 
     * Searches through the FFXIV folder, identified by
     * <code>FFXIV_FOLDER</code>, and verifies that each character folder
     * contained within is matched with a corresponding entry in
     * <code>characters</code>.
     */
    private static boolean scanBackups () {
        
        /**
         * TODO:  Iterate through character folders in 'FFXIV_FOLDER' and look for ones that are not identified in the 'characters' identifier.
         *        
         *        If there is at least one folder not identified, inform the
         *        user of the number ("Found # unidentified characters"), and
         *        then call chooseProfileName() that many times.
         */
        
        return false;
    }
    
    
}
