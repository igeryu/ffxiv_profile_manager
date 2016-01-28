
import components.Profile;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import util.Character;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
    //private static ArrayList<Character> unidentifiedCharacters;
    private static boolean unidentifiedCharacters;
    private static UnidentifiedCharactersFrame unidentifiedCharactersFrame;
    
    //  TODO:  Consider making this a local class:
    private static TagManager tagManager;
    
    
    public static void main(String[] args) {
        
//        JSONTest.writeJSON();
//        JSONTest.readJSON();
        
        //  Change this from hard-coded to dynamic:
        //activeCharacterName = "Feyen";
        //activeCharacterID = "FFXIV_CHR00400000009D4722";
        
        init();
        
        
        
        //System.out.printf("Profile: %s\n", activeProfile.name);
        
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
     * <p>Lists identified characters, and asks the user to give a name of a yet
     * to be identified character.</p>
     * 
     * <p> Then searches the folder of each <code>Character</code> object whose
     * <code>identified</code> flag is <code>false</code>, until one is
     * identified as being correct, none are found (error).</p>
     * 
     * <p>OLD:  Then searches the folder of each entry in
     * <code>unidentifiedCharacters</code> until one is identified as being
     * correct, or none are found (error).</p>
     */
    private static void identifyCharacter () {
        
        System.out.println("\nIdentifying Character...");
        
        //unidentifiedCharactersFrame = new UnidentifiedCharactersFrame();
        
        String unidentifiedCharacterNames = "Identified Characters:\n";
        
        for (Character character : characters) {
            if (character.identified()) {
                //DEBUG:
                //System.out.printf("\nAdding identified character name (%s) to list...\n", character.getName());
                unidentifiedCharacterNames += character.getName() + "\n";
            }  //  if character is identified
        }  // for each character in characters
        
        //  TODO:  identifyCharacter() : need to ensure player enters character full name
        String nameInput = JOptionPane.showInputDialog(null, unidentifiedCharacterNames, "Identify New Character", QUESTION_MESSAGE);
        
        identifyCharacterFolder(nameInput);
        
    }  //  end method identifyCharacter()
    
    
    
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
         * 
         * NOTE:  Currently commented out because I have two characters with the same name on different servers.
         * TODO:  Figure out how to make this work for two like-named characters.
         */
//        for (Character character : characters) {
//            if (character.getName() != null && character.getName().equals(name)) {
//                System.out.println("\nCharacter " + name + " already identified...");
//                return false;
//            }
//        }
        
        // TODO:  Add support for multiple characters of same name, but different servers.
        
        //  2. Locate folder by character name.  If successful, create a new
        //     Character object, call identifyProfiles() on it and add it to
        //     characers ArrayList, then return true.
        //  TODO: Situation where a list of unidentified characters would be more efficient:
        for (Character character : characters) {
            if (!character.identified() && searchCharacterFolder(character, name)) {
                //DEBUG:
                //System.out.println("\nCharacter " + name + " found!");
                return true;
            }
        }
        
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
        characters = new ArrayList<>();
        
        File file = null;
        try {
            file = new File(DATA_FILE_NAME);
        } catch (Exception e) {
            System.out.println("Exception!");
        }
        
        //  Check if any folders exist:
        if (scanBackups()) {
            //characters.get(0);
            //  Check if this is first load:
            if (file.exists()) {
                loadConfig(file);
            } else {
                initConfig(file);
            }
            
        } else {
            System.out.println("Scanning of backups failed, or no backups exist.");
        }
        
        //  change "test" to the name received earlier
        activeProfile = getActiveProfile(activeCharacterName);
        
    }  //  end int() method
    
    
    
    // TODO:  Need to fix initConfig(File, String) so that it creates the correct JSON file structure.
     
    /**
     * <p>Runs during first-time setup.  Creates a new configuration file, who's
     * name was previously determined by <code>DATA_FILE_NAME</code>.</p>
     * 
     * <p>Parameters beginning with a '<code>@</code>' are place holders.</p>
     * 
     * @param file     Configuration file, whose name is determined by
     *                 <code>DATA_FILE_NAME</code>.
     * 
     * @param name     Name of active profile, need to fix this.
     */
    //private static void initConfig (File file, String name) {
    private static void initConfig (File file) {
        
        System.out.println("\nInitializing configuration settings...");
        
        //  DEBUG:
        //characters.get(0).setIdentified(true);
        //characters.get(0).setName("Test Name");
        
        if (verifyScannedBackups() > 0) {
            //OLD: while (unidentifiedCharacters.size() > 0) {
            while (unidentifiedCharacters) {
                
                identifyCharacter();
                
                //  This is to ensure that unidentifiedCharacters is still valid:
                verifyScannedBackups();
                
            }  //  while unidentifiedCharacters (has objects within)
            
        }  //  if there is at least one unverified character
        
        //  =================================================================
        
        System.out.println("Initializing " + DATA_FILE_NAME);
        System.out.println("Writing JSON file...");
        
        //  Root
        JSONObject rootJSON = new JSONObject();
        rootJSON.put("Active Character", "@Not_Selected");
        rootJSON.put("Active Profile", "@Not_Selected");
        JSONObject charactersJSON = new JSONObject();
        
        //  TODO:  This is just to make it work, for now...
        int increment = 0;
        
        for (Character character : characters) {
            
            //System.out.println("\nCreating character in JSON (" + character.getId() + ")...");
            
            JSONObject characterJSON = new JSONObject();
            characterJSON.put("ID", character.getId());
            String name = character.getName() != null ? character.getName() : "@Unknown_" + increment++;
            
            //  TODO:  Implement this:
            //characterJSON.put("Active Profile", character.getActiveProfile().name);
            
            /*
            //  Add profiles/backups:
            JSONArray profileJSON = new JSONArray();
            profileJSON.add("Backup1 Timestamp");
            profileJSON.add("Backup2 Timestamp");
            characterJSON.put("MyProfile", myProfile);
            */

            charactersJSON.put(name, characterJSON);
            
            //System.out.println("\nJSON Snapshot:\n" + charactersJSON);

        }
        
        rootJSON.put("Characters", charactersJSON);
        

        // try-with-resources statement based on post comment below :)
        try (FileWriter fileWriter = new FileWriter(DATA_FILE_NAME)) {
            fileWriter.write(rootJSON.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + rootJSON);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getLocalizedMessage());
        }
        
    }  //  end method initConfig()

    
    
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
     * method runs.  It then verifies that each character folder contained
     * within is matched with a corresponding entry in
     * <code>characters</code></p>
     * 
     * @param file     <code>File</code> object, whose name is determined by
     *                 <code>DATA_FILE_NAME</code>.
     * 
     * @return         <code>null String</code> TODO: fix this
     */
    //private static String loadConfig(File file) {
    private static boolean loadConfig(File file) {
        
        //  TODO:  Iterate through character folders in 'FFXIV_FOLDER' and look for ones that are not identified in the 'characters' identifier.
        
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
     * <p>Searches through the FFXIV folder, identified by
     * <code>FFXIV_FOLDER</code>, and creates a <code>characters</code> object
     * for each folder it finds.</p>
     * 
     * <p>For each character/folder it finds, it will call
     * <code>Character.scanBackups()</code>.
     */
    private static boolean scanBackups () {
        
        System.out.println("\nScanning backups...");
        
        /**
         * TODO:  Iterate through character folders in 'FFXIV_FOLDER' and create Character objects for each
         * 
         * <p>Iterates through each folder in <code>FFXIV_FOLDER</code> and add
         * each one that matches the "<code>FFXIV_CHR...</code>" pattern.</p>
         * 
         *<p>OLD: If there is at least one folder not identified, inform the
         * user of the number ("Found # unidentified characters"), and then call
         * chooseProfileName() that many times.</p>
         */
        
        File directory = new File(FFXIV_FOLDER);
        File[] characterFolders = directory.listFiles(new FilenameFilter(){
            public boolean accept (File dir, String name) {
                return name.startsWith("FFXIV_CHR");
            }
        });
        
        if (characterFolders.length > 0) {
            
            System.out.println("\nFound " + characterFolders.length + " folders.");
            
            for (File folder : characterFolders) {
                //System.out.println("\nAdding character folder...");
                String id = folder.getName();
                Character newCharacter = new Character(id);
                characters.add(newCharacter);
                
                if (!newCharacter.scanBackups())
                    return false;
            }
            
        } else
            return false;
        
        return true;
    }  //  end method scanBackups()
    
    
    
    
    private static boolean searchCharacterFolder (Character character, String name) {
        //  TODO:  Build searchCharacterFolder()
        String characterLogDirectoryString = FFXIV_FOLDER + "\\" + character.getId() + "\\log\\";
        File characterLogDirectory = new File(characterLogDirectoryString);
        
        File[] logFiles = characterLogDirectory.listFiles(new FilenameFilter(){
            public boolean accept (File dir, String name) {
                return name.endsWith("log");
            }
        });
        
        if (logFiles != null && logFiles.length > 0) {
            
            //DEBUG:
            //System.out.printf("\n(%s) Found %d log files.\n", character.getId(), logFiles.length);

            for (File file : logFiles) {
                //  DEBUG:
                //System.out.printf("\n(%s) Searching through file (%s)...\n", character.getId(), file.getName());
                try {
                    
                    byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                    String fileContents = new String(encoded, StandardCharsets.US_ASCII);

                    if (Pattern.compile(Pattern.quote(name), Pattern.CASE_INSENSITIVE).matcher(fileContents).find()) {
                            System.out.println("Log file found.  Identified character " + name + ".");
                            character.setName(name);
                            character.setIdentified(true);
                            return true;
                        }
                    
                } catch (Exception e) {
                    System.out.printf("\n(%s) Exception...\n", character.getId());
                }
            }
            
        } else {
            System.out.printf("\n(%s)No log files found.\n", character.getId());
        }

        //DEBUG:
        //System.out.printf("\n(%s) String not found in log file...\n", character.getId());

        return false;
    }
    
    
    
    /**
     * TODO:  Likely should rename this so that the return value makes more sense.
     * 
     * <p>Should probably be depreciated/removed...</p>
     * 
     * <p>After <code>scanBackups()</code> has been run at least once, this
     * checks each <code>Character</code> object in the <code>characters</code>
     * identifiedCharactersList for a <code>null</code> name.  Each time one is found, it is
     * added to <code>unidentifiedCharacters</code>.</p>
     * 
     * <p>OLD:  After <code>scanBackups()</code> has been run at least once, this
     * checks each <code>Character</code> object in the <code>characters</code>
     * identifiedCharactersList for a <code>null</code> name.  Each time one is found, it is
     * added to <code>unidentifiedCharacters</code>.</p>
     * 
     * <p><code>Character</code> objects with having name indicate that they
     * have not been identified by the user.</p>
     * 
     * <p>Returns the number of unidentified characters found.</p>
     * 
     * @return     Number of unidentified characters found.
     */
    private static int verifyScannedBackups() {
        System.out.println("\nVerifying scanned backups...");
        int found = 0;
        //unidentifiedCharacters = new ArrayList<>();
        
        for (Character character : characters) {
            //if (character.getName() == null) {
            if (!character.identified()) {
                //unidentifiedCharacters.add(character);
                found++;
            }  // if the character has no name (hasn't been identified)
        }  //  for each character in characters
        
        unidentifiedCharacters = found > 0 ? true : false;
        return found;
        
    }  //  end method verifiyScannedBackups()
    
    
    
    private static class UnidentifiedCharactersFrame extends JFrame {
        
        public UnidentifiedCharactersFrame () {
            super();

            this.setTitle("Identify New Character");

            //             -----  Header -----

            add(new JLabel("Profiles"));
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            //  TODO:  Make this into a JTree (for Profiles and past backups)
            JScrollPane scrollPane = new JScrollPane();
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 3;
            add(scrollPane, c);
            
            JButton loadButton = new JButton("Load");
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 1;
            add(loadButton, c);
            
            JButton cancelButton = new JButton("Cancel");
            c.gridx = 2;
            c.gridy = 1;
            c.gridwidth = 1;
            add(cancelButton, c);

            int width = 300; int height = 500;
            setMinimumSize(new Dimension(width, height));
            setVisible(true);

        }  //  end UnidentifiedCharactersFrame constructor
        
        private JScrollPane buildIdentifiedCharacterList() {
            
            //Get a identifiedCharactersList of identified characters:
            ArrayList<JPanel> identifiedCharacterPanels = new ArrayList<>();
            for (Character character : characters) {
                if (character.identified())
                    identifiedCharacterPanels.add(character.getCharacterPanel());
            }
            
            JList<JPanel> identifiedCharactersList = new JList<JPanel>() {
                private static final long serialVersionUID = 1L;
                @Override public Dimension getPreferredSize() {
                    int width = 250;
                    int rows = identifiedCharacterPanels.size();
                    int height = 5 * rows;
                    return new Dimension(width, height);
                }
            };
            
            for (JPanel panel : identifiedCharacterPanels) {
                identifiedCharactersList.add(panel);
            }
            
            identifiedCharactersList.setLayout(new FlowLayout());
            identifiedCharactersList.setLayoutOrientation(JList.VERTICAL);
            
            JScrollPane pane = new JScrollPane(identifiedCharactersList);
            pane.getVerticalScrollBar().setUnitIncrement(100);
            pane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
            
            identifiedCharactersList.validate();
            pane.validate();
            
            setVisible(true);
            
            return pane;
            
        }
        
    }  //  end class UnidentifiedCharactersFrame
    
}  //  end class ProfileManager