
import components.Profile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;

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
    private static String  activeCharacterName;
    private static String  activeCharacterID;
    
    //  TODO:  Need to fill this out.
    private ArrayList<Character> characters;
    
    
    public static void main(String[] args) {
        
        //  Change this from hard-coded to dynamic:
        activeCharacterName = "Feyen";
        activeCharacterID = "FFXIV_CHR00400000009D4722";
        
        init();
        
        System.out.printf("Profile: %s\n", activeProfile.name);
        
        backupActiveProfile();
    }
    
    
    /**
    * Runs every time the app is opened.  Checks if there is a DATA_FILE_NAME
    * file (first run condition), and if so, it asks the user to name their
    * current profile.
    * TODO: Need to make this ask for <b>ALL</b> characters' full names and
    *       then identify each character's folder from the log files.
    */
    private static void init() {
        System.out.println("Initializing app");
        
        File file = null;
        try {
            file = new File(DATA_FILE_NAME);
        } catch (Exception e) {
            System.out.println("Exception!");
        }
        
        String name;
        //  Check if this is first load:
        if (!file.exists()) {
            System.out.println(DATA_FILE_NAME + " does not exist.");
            name = chooseProfileName();
            
            initConfig(file, name);
        } else {
            //  Grab name from the DATA_FILE_NAME:
            name = loadConfig(file);
        }
        
        //  change "test" to the name received earlier
        activeProfile = getActiveProfile(name);
        
    }  //  end int() method
    
    
    
    //  Creates a new config file.
    private static void initConfig (File file, String name) {
        
        System.out.println("Initializing " + DATA_FILE_NAME);
        
        try {
            FileOutputStream out = new FileOutputStream(file);
            PrintWriter writer = new PrintWriter(out);
            writer.println(name);
            writer.flush();
            writer.close();
            out.close();
        } catch (Exception e) {
            System.out.println("initConfig() exception!");
        }
    }

    
    
    //  TODO:  Add functionality to handle multiple characters by ID
    private static Profile getActiveProfile(String name) {
        
        System.out.println("Getting active profile from name");
        
        //  TODO:  Make sure that activeCharacterName and activeCharacterID really
        //         will be set at this point, not just because they are
        //         hard-coded in main().
        return new Profile(name, activeCharacterName, activeCharacterID, FFXIV_FOLDER);
    }
    
    
    
    private static String chooseProfileName() {
        
        System.out.println("Getting profile name.");

        String name = "";

        do {

            name = JOptionPane.showInputDialog(null, "Please input the name of your current profile.", "First-Time Setup", QUESTION_MESSAGE);

        } while (name.equals(""));
        
        System.out.println("\nInitializing profile: " + name);

        return name;

    }  // end chooseProfileName() method
    
    
    
    private static String loadConfig(File file) {
        
        System.out.println("Loading " + DATA_FILE_NAME);
        
        Scanner scanner = null;
        
        try {
            scanner = new Scanner(file);
        } catch (Exception e) {
            System.out.println("loadConfig() exception!");
            return null;
        }
        
        if (scanner == null) {
            System.out.println("null scanner");
            return null;
        }
        
        if (scanner.hasNextLine())
            return scanner.next();
        else return null;
    }
    
    
    
    //  @TODO:  Need to add functionality to honor user's inclusion/exclusion
    //         choices (include gearsets, exclude keybinds, etc.) from the
    //         profile and ergo the backup.
    private static boolean backupActiveProfile () {
        
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        String timestamp = dateFormat.format(cal.getTime());
        
        System.out.println("Timestamp: " + timestamp);
        
        String backupName = timestamp + " " + activeCharacterName + " " + activeProfile.name + " Backup";
        
        System.out.println("Backup Name: " + backupName);
        
        return true;
    }
    
    
    
    //  TODO:  For now, this will simply overwrite the files in the current
    //         directory with whichever one(s) are in the backup being loaded.
    private static boolean loadProfile () {
        return true;
    }
    
    
    
    //  TODO:  Build this!
    private static void scanBackups () {
        
    }
    
    
}
