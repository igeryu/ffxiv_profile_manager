/**
 * Changelog:
 * 2016-01-29 : Added support for working on Mac OS X.
 * 2016-01-29 : Refactored backupActiveProfile(), 
 * 2016-01-29 : Removed chooseProfileName(), not needed.
 * 
 * 2016-01-29 : Added activeProfileName.
 * 2016-01-29 : Updated logic for defining FFXIV_FOLDER, for Windows user path.
 * 2016-01-29 : Added addCharacter() method, to prevent duplication of Character objects created by scanBackups()
 * 2016-01-29 : Add much more to loadConfig(); now it reads the JSON file and creates appropriate Character/Profile/Backup objects
 * 
 * 2016-01-30 : Added identifyAllCharacters() method
 * 2016-01-30 : Changed the first if/while loop in saveConfig() to one call to identifyAllCharacters()
 * 2016-01-30 : Added call to identifyAllCharacters() to end of loadConfig()
 * 2016-01-30 : Changed loadConfig() to call identifyAllCharacters() at the end
 * 2016-01-30 : Added getActiveCharacter() method.
 * 2016-01-30 : Changed init() to call getActiveCharacter() when it isn't already set
 * 2016-01-30 : Fixed initConfig() to account for non-null active character/profile
 * 2016-01-30 : Changed initConfig()'s name to saveConfig()
 * 2016-01-30 : Updated loadConfig() so that it sets activeCharacter using getCharacter(activeCharacterName)
 * 
 * 2016-02-01 : Did some refactoring.
 * 
 * 2016-02-05 : Added detectFolderChanges(File, File)
 * 2016-02-05 : Added FileMismatchException
 * 2016-02-05 : Added a call to detectFolderChanges() in init()
 * 2016-02-05 : Updated backupActiveProfile() to actually copy files to a new backup directory
 */

/**
 * TODO: (2016-01-29) Add way to manage all characters at once, rather than one active character (perhaps using tabs)
 */

import components.Profile;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import util.Character;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import static javax.swing.SwingConstants.CENTER;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import util.TagManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alan Johnson
 */
public class ProfileManager {
    
    private static String FFXIV_FOLDER;
    //  TODO: determine if folderDivider is needed:
    private static String FOLDER_DIVIDER;
    
    private static final String DATA_FILE_NAME = "data.json";
    
    //  Assumes that whatever is active in the user directory is the
    //  application's active profile.
    private static Profile activeProfile;
    private static String  activeProfileName;
    
    private static Character activeCharacter;
    private static String    activeCharacterName;
    private static String    activeCharacterID;
    
    private static ArrayList<Character> characters;
    private static boolean unidentifiedCharacters;
    
    //  TODO:  Consider making this a local class:
    private static TagManager tagManager;
    
    
    public static void main(String[] args) {
        
        if (System.getProperty("os.name").equals("Mac OS X")) {
            FFXIV_FOLDER = System.getProperty("user.home") + "/documents/Final Fantasy XIV - A Realm Reborn/";
            FOLDER_DIVIDER = "/";
        } else {
            FFXIV_FOLDER = System.getProperty("user.home") + "\\Documents\\My Games\\FINAL FANTASY XIV - A Realm Reborn\\";
            FOLDER_DIVIDER = "\\";
        }
        
        System.out.println("\nRunning on " + System.getProperty("os.name"));
        
        init();
        
        System.exit(0);
        
    }
    
    
    
    /**
     * <p>Checks if a similar character (same folder ID) has already been added.
     * If so, this combines the data by copying it to the existing character.</p>
     * @param c     New to be added (and verified)
     */
    private static void addCharacter(Character c) {
        for (Character character : characters) {
            if (character.getId().equals(c.getId())) {
                character.setName(c.getName());
                character.setIdentified(true);
            }  //  if both characters have the same ID
        }  //  for each item in characters
    }  //  end method addCharacter()
    
    
    
    /**
     * <p>Backs up the active profile stored in <code>activeProfile</code>.  It
     * does this by creating a folder following the
     * "<code>yyyy-MM-dd-HH-mm</code>" format for dates and appends
     * <code>activeCharacterName</code>, <code>activeProfile.name</code> and
     * then "<code>Backup</code>".  Inside that folder, all selected components
     * are copied from the active directory.</p>
     * 
     * @return <code>true</code> if the backup was successful,
     *         <code>false</code> otherwise.
     */
    private static boolean backupActiveProfile () {
        
        //  DEBUG:
        System.out.println("\nAttempting to backup active profile...");
        
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        String timestamp = dateFormat.format(cal.getTime());
        
        System.out.println("Timestamp: " + timestamp);
        
        String backupName = timestamp + " [" + activeCharacterName
                                      + "] [" + activeProfile.getName() + "] Backup";
        
        //  TODO: backupActiveProfile() ~ Acknowledge user's inclusion/exclusion choices (includ gearsets, exclude keybinds, etc.)
        String characterDirectory = FFXIV_FOLDER + FOLDER_DIVIDER + activeCharacterID;
        File current = new File(characterDirectory);
        File backup = new File(characterDirectory + FOLDER_DIVIDER + backupName);
        try {
            IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".DAT");
            IOFileFilter txtFiles = FileFilterUtils.and(FileFileFilter.FILE, txtSuffixFilter);
            
            FileUtils.copyDirectory(current, backup, txtFiles, true);
        } catch (Exception e) {
            System.out.println("\nProfile backup failed!");
            e.printStackTrace();
        }

        //  DEBUG:
        System.out.println("Backup Name: " + backupName);
        
        return true;
    }  //  end method backupActiveProfile()
    
    
    
    /**
     * <p>TODO: Change detectFolderChanges() so that it accounts for backups whose sources are multiple profiles (because of included/excluded components)</p>
     * 
     * <p>Returns <code>true</code> if the <code>current</code> directory has any
     * files with newer modification dates than the <code>backup</code>
     * directory.</p>
     * 
     * @param backup      The backup directory
     * @param current     The current working directory, to be checked for
     *                    updates
     * 
     * @return            <code>true</code> if changes to the current directory
     *                    are detected; <code>false</code> otherwise
     */
    private static boolean detectFolderChanges (Character character) throws FileMismatchException {
        
        String characterDirectory = FFXIV_FOLDER + FOLDER_DIVIDER + character.getId();
        File current = new File(characterDirectory);
        File backup = new File(characterDirectory + FOLDER_DIVIDER + character.getActiveProfile().getLastBackup().getName());
        
        //  Verify that both backup and current are directories:
        if (!backup.isDirectory() || !current.isDirectory()) {
            return false;
        }
        
        Comparator<File> fileComparator = new Comparator<File>(){

            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
            
        };
        
        //  Gather and sort backup files:
        File[] backupFilesArray = backup.listFiles(new FilenameFilter(){
            public boolean accept (File dir, String name) {
                return name.endsWith("DAT");
            }
        });
        ArrayList<File> backupFiles = new ArrayList<File> (Arrays.asList(backupFilesArray));
        backupFiles.sort(fileComparator);
        
        //  Gather and sort current files:
        File[] currentFilesArray = current.listFiles(new FilenameFilter(){
            public boolean accept (File dir, String name) {
                return name.endsWith("DAT");
            }
        });
        ArrayList<File> currentFiles = new ArrayList<File> (Arrays.asList(currentFilesArray));
        currentFiles.sort(fileComparator);
        
        //  TODO: Fix the following patch code:
        int maxElements = backupFiles.size();
        maxElements = maxElements < currentFiles.size() ? currentFiles.size() : maxElements;
        
        for (int x = 0; x < maxElements; x++) {
            //  TODO: Fix the following patch code:
            if (!currentFiles.get(x).getName().equals(backupFiles.get(x).getName())) {
                throw new FileMismatchException();
            }
            
            if (currentFiles.get(x).lastModified() > backupFiles.get(x).lastModified()) {
                return true;
            }  //  check if a current file is newer than its corresponding file in backup
        }  // for each element in the ArrayLists
        
        
        return false;
    }  // end method detectFolderChanges()
    
    
    
    /**
     * <p>Asks the user to identify, from a list of all identified characters,
     * which character should be the current active one</p>
     * 
     * @return     Active character, chosen by user
     */
    private static Character getActiveCharacter() {
        
        JFrame charactersFrame;
        boolean madeSelection = false;
        Character characterSelected;
        
        charactersFrame = new JFrame() {
            JScrollPane pane;
            JList charactersList;
            JButton selectButton;
            JButton cancelButton;
            Character character;

            ArrayList<JRadioButton> characterRadioButtons;
            ButtonGroup characterButtonGroup;

            {
                setTitle("Identify Active Character");
                setName("");
                setLocationRelativeTo(null);
                characterRadioButtons = new ArrayList<>();
                characterButtonGroup = new ButtonGroup();

                pane = new JScrollPane(buildCharacterPanel());
                selectButton = new javax.swing.JButton();
                cancelButton = new javax.swing.JButton();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

                selectButton.setText("Select");
                selectButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        String characterName = characterButtonGroup.getSelection().getActionCommand();
                        if (characterName != null) {
                            //  DEBUG:
                            //System.out.printf("\nselectedButton.getText = \"%s\"\n", characterName);
                            
                            setName(characterName);
                            setVisible(false);
                        }
                    }
                });

                cancelButton.setText("Cancel");
                cancelButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        System.exit(0);
                    }
                });

                GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(pane)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(selectButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                                                .addComponent(cancelButton)))
                                .addContainerGap())
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(pane, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(selectButton)
                                        .addComponent(cancelButton))
                                .addContainerGap())
                );

                pack();
            }

            private JScrollPane buildCharacterPanel() {

                JPanel charactersPanel = new JPanel();
                charactersPanel.setLayout(new GridLayout(characters.size(), 1));

                for (Character character : characters) {
                    //  DEBUG:
                    //System.out.printf("\nAdding %s to characterRadioButtonList", character.getName());

                    JRadioButton newRadioButton = new JRadioButton(character.getName());
                    newRadioButton.setActionCommand(character.getName());
                    newRadioButton.setHorizontalAlignment(CENTER);
                    characterRadioButtons.add(newRadioButton);
                    characterButtonGroup.add(newRadioButton);
                    charactersPanel.add(newRadioButton);
                }

                JScrollPane pane = new JScrollPane(charactersPanel);
                pane.getVerticalScrollBar().setUnitIncrement(100);
                pane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
                pane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

                charactersPanel.validate();
                pane.validate();
                return pane;

            }  //end buildCharacterList()
            
        };

        charactersFrame.setVisible(true);

        //  DEBUG:
        //System.out.println("\nWaiting for user input...");
        while (charactersFrame.getName().equals("")) {
             //  spin on user input
            //System.out.print("...|");
            System.out.print("");
         }
        
        //  DEBUG:
        //System.out.printf("\nUser gave input (\"%s\").\n", charactersFrame.getName());
        
         activeCharacterName = charactersFrame.getName();
         return getCharacter(charactersFrame.getName());
        
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
                           activeCharacterID, FFXIV_FOLDER + FOLDER_DIVIDER);
    }

    
    
    /**
     * TODO:  Need to implement code for multiple characters of same name (different server)
     * 
     * Given the name of the character, this method locates the item in
     * <code>characters</code> that matches that name and returns it.
     * 
     * @param name     Name of character to locate
     * @return         Appropriate character or <code>null</code>
     */
    private static Character getCharacter (String name) {
        for (Character character : characters) {
            if (character.getName().equals(name)) {
                return character;
            }  //  if the character's name matches the search string
        }  // for each item in characters
        return null;
    }  // end method getCharacter()
    
    
    
    /**
     * <p>Loops every time <code>verifyScannedBackups</code> returns a value
     * greater than 0.  With each iteration, <code>identifyCharacter()</code> is
     * called.</p>
     */
    private static void identifyAllCharacters() {
        
        //  DEBUG:
        System.out.println("\nVerifying all characters are identified...");

        while (verifyScannedBackups() > 0) {
            
            identifyCharacter();

        }  //  if there is at least one unverified character
    }  // end method identifyAllCharacters()
    
    
    
    /**
     * <p>Lists identified characters, and asks the user to give a name of a yet
     * to be identified character.</p>
     * 
     * <p> Then searches the folder of each <code>Character</code> object whose
     * <code>identified</code> flag is <code>false</code>, until one is
     * identified as being correct, none are found (error).</p>
     */
    private static void identifyCharacter () {
        
        System.out.println("\nIdentifying Character...");
        
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
        
        System.out.printf("\nLocating character: \"%s\"\n", name);
        
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
        
        /**
         * 2. Locate folder by character name.  If successful, create a new
         *    Character object, call identifyProfiles() on it and add it to
         *    characters ArrayList, then return true.
         * 
         * TODO:  Add support for multiple characters of same name, but different servers.
         * TODO:  Situation where a list of unidentified characters would be more efficient:
         */
        
        for (Character character : characters) {
            if (!character.identified()) {
                return searchCharacterFolder(character, name);
            }  //  if the character is not identified
        }  // for each item in characters
        
        return false;
    }  // end method identifyCharacterFolder()
    
    
    /**
    * <p>Runs every time the application is opened:</p>
    * 
    * <p>If <code>scanBackups()</code> returns false, the method returns early.
    * Otherwise, it checks if <code>file</code> exists.  If it does,
    * <code>loadConfig()</code> is executed; otherwise <code>saveConfig()</code>
    * is executed.</p>
    * 
    * <p>TODO: Need to make this ask for <b>ALL</b> characters' full names and then identify each character's folder from the log files.</p>
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

            //  Check if this is first load:
            if (file.exists()) {
                loadConfig(file);
            } else {
                //  TODO:  Since saveConfig() runs at the end of this method, see if this call can be eliminated.
                saveConfig(file);
            }
            
        } else {
            System.out.println("Scanning of backups failed, or no backups exist.");
            return;
        }
        
        //  Check if the active character isn't chosen, and have the user choose it (if needed):
        if (activeCharacterName.equals("@Not_Selected") || activeCharacter == null) {
            activeCharacter = getActiveCharacter();
            activeCharacterID = activeCharacter.getId();
        }
        
        //  Check if the active character's folder has been changed since the
        //  last update:
        try {
            if (true) {
            //if (detectFolderChanges(activeCharacter)) {
                
                //  DEBUG:
                System.out.println("\nChanges detected in profile...");
                Profile testProfile = new Profile("Test Profile", activeCharacter.getName(), activeCharacter.getId(), FFXIV_FOLDER);
                activeCharacter.addProfile(testProfile);
                activeProfile = testProfile;
                
                //  TODO: Determine if the return value should be used (or refactored out..):
                backupActiveProfile();
            }
        } catch (Exception e) {
            //System.out.println("\ndetectFolderChanges() failed!");
            e.printStackTrace();
        }

        saveConfig(file);
        
    }  //  end init() method
    
    
    
    /**
     * <p><code>scanBackups()</code> is assumed to have already run before this
     * method runs.</p>
     * 
     * <p>Loads the configuration file, whose name is identified by
     * <code>DATA_FILE_NAME</code>, into memory.</p>
     * 
     * <p>It then verifies that each character folder contained
     * within is matched with a corresponding entry in
     * <code>characters</code></p>
     * 
     * @param file     <code>File</code> object, whose name is determined by
     *                 <code>DATA_FILE_NAME</code>.
     * 
     * @return         pass/fail status
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
        
        System.out.println("Reading JSON file...");
        
        JSONParser parser = new JSONParser();
        
        try {
 
            Object obj = parser.parse(new FileReader(
                    DATA_FILE_NAME));
 
            JSONObject rootJsonObject = (JSONObject) obj;
            
            activeProfileName = (String) rootJsonObject.get("Active Profile");
            activeCharacterName = (String) rootJsonObject.get("Active Character");
            
            JSONObject charactersRootJSON = (JSONObject) rootJsonObject.get("Characters");
            
            ArrayList<JSONObject> charactersJSON = new ArrayList<>();
            Set<Entry<String, Object>> characterSets = charactersRootJSON.entrySet();
            
            for (Map.Entry<String, Object> characterEntry : characterSets) {
                //  DEBUG:
                //System.out.printf("\nnewCharacterName = \"%s\"", characterEntry.getKey());
                
                String newCharacterName = characterEntry.getKey();
                JSONObject newCharacterJSON = (JSONObject) characterEntry.getValue();
                String newCharacterId = (String) newCharacterJSON.get("ID");
                Character newCharacter = new Character(newCharacterName, newCharacterId);
                
                ArrayList<Profile> profiles = new ArrayList<>();
                Set<Entry<String, Object>> profileSets = newCharacterJSON.entrySet();
                for (Map.Entry<String, Object> profileEntry : profileSets) {
                    
                    //  TODO: Refactor this if into something more streamlined:
                    if (profileEntry.getKey().equals("ID") || profileEntry.getKey().equals("Active Profile"))
                        continue;
                    
                    String newProfileName = profileEntry.getKey();
                    JSONArray newProfileArray = (JSONArray) profileEntry.getValue();
                    
                    ArrayList<String> backups = new ArrayList<>();
                    Iterator<?> profileIterator = newProfileArray.iterator();
                    while (profileIterator.hasNext()) {
                        backups.add((String) profileIterator.next());
                    }  // collect backup names
                    
                    Profile newProfile = new Profile(newProfileName, newCharacterName, newCharacterId, FFXIV_FOLDER, backups);
                    newCharacter.addProfile(newProfile);
                    
                }  //  iterate through all profiles
                
                addCharacter(newCharacter);
                
                //  DEBUG:
                //System.out.printf("\nnewCharacter.getName() = \"%s\"", newCharacter.getName());
            }  //  iterate through characters
            
            
            
            //   DEBUG:  Display Results:
            System.out.println("\n\nActive Profile: " + activeProfileName);
            System.out.println("Active Character: " + activeCharacterName);
            System.out.println("\nCharacters:");
            
            for (Character character : characters) {
                System.out.println("    " + character.getName() + ":");
                System.out.println("        ID: " + character.getId());
                
                System.out.println("\n        Profiles:");
                for (Profile profile : character.getProfiles()) {
                    System.out.println("            " + profile.getName());
                    //  TODO: print out profile backups
                    
                }  // iterate through profiles
                
            }  // iterate through characters
            //  END DEBUG
 
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        //  Verify that all characters are identified, and take action if not:
        identifyAllCharacters();
        
        activeCharacter = getCharacter(activeCharacterName);
        activeCharacterID = activeCharacter.getId();
        
        return true;
    }  //  end method loadConfig()
    
    
    
    /**
     * <p>TODO:  For now, this will simply overwrite the files in the current directory with whichever one(s) are in the backup being loaded.</p>
     * 
     * <p>TODO:  Implement way for this to return false whenever user attempts to load the active profile (or prevent this somewhere else, altogether).</p>
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
    }  //  end method loadProfile()
    
    
    
    /**
     * <p>Overwrites the configuration file, who's name was previously
     * determined by <code>DATA_FILE_NAME</code>.</p>
     * 
     * <p>Parameters beginning with a '<code>@</code>' are place holders.</p>
     * 
     * @param file     Configuration file, whose name is determined by
     *                 <code>DATA_FILE_NAME</code>.
     */
    //private static void saveConfig (File file, String name) {
    private static void saveConfig (File file) {
        
        System.out.println("\nSaving configuration settings...");
        
        identifyAllCharacters();
        
        //  =================  Begin JSON Processing  =================
        
        System.out.println("Initializing " + DATA_FILE_NAME);
        System.out.println("Writing JSON file...");
        
        //  Root
        JSONObject rootJSON = new JSONObject();
        if (activeCharacter != null && activeCharacterName != null) {
            rootJSON.put("Active Character", activeCharacterName);
        } else {
            rootJSON.put("Active Character", "@Not_Selected");
        }
        
        if (activeProfile != null && activeProfileName != null) {
            rootJSON.put("Active Profile", activeProfileName);
        } else {
            rootJSON.put("Active Profile", "@Not_Selected");
        }
        
        JSONObject charactersRootJSON = new JSONObject();
        
        //  TODO:  This is just to make it work, for now...
        int increment = 0;
        
        for (Character character : characters) {
            
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

            charactersRootJSON.put(name, characterJSON);
            
            //  DEBUG:
            //System.out.println("\nJSON Snapshot:\n" + charactersRootJSON);

        }
        
        rootJSON.put("Characters", charactersRootJSON);
        

        try (FileWriter fileWriter = new FileWriter(DATA_FILE_NAME)) {
            fileWriter.write(rootJSON.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            
        } catch (IOException e) {
            System.out.println("IOException: " + e.getLocalizedMessage());
        }
        
    }  //  end method saveConfig()
    
    
    
    /**
     * <p>Searches through the FFXIV folder, identified by
     * <code>FFXIV_FOLDER</code>, and creates a <code>characters</code> object
     * for each folder it finds.</p>
     * 
     * <p>For each character/folder it finds, it will call
     * <code>Character.scanBackups()</code>.
     * 
     * @return    <code>true</code> if at least one folder was found (regardless of it being properly identified)
     */
    private static boolean scanBackups () {
        
        System.out.println("\nScanning backups...");
        
        /**
         * Iterates through each folder in FFXIV_FOLDER and add a new Character
         * object for each one that matches the "FFXIV_CHR..." pattern.
         */
        
        File directory = new File(FFXIV_FOLDER);
        File[] characterFolders = directory.listFiles(new FilenameFilter(){
            public boolean accept (File dir, String name) {
                return name.startsWith("FFXIV_CHR");
            }
        });
        
        if (characterFolders.length > 0) {
            
            System.out.println("\nFolders found: " + characterFolders.length);
            
            for (File folder : characterFolders) {
                
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
    
    
    
    /**
     * <p>Searches unidentified character folders for the string provided in
     * <code>name</code>.  Once found:
     * <ol>
     * <li> The <code>Character</code> object who
     * owns that folder has its name set to <code>name</code></li>
     * <li> That same
     * <code>Character</code> object's <code>identified</code> flag is set to
     * <code>true</code></li>
     * <li> The <code>Character</code> object is
     * returned.</li>
     * </ol></p>
     * 
     * @param character     <code>Character</code> object, whose folder is to be
     *                      searched.
     * @param name          Name given by user, to check against character
     *                      folder.
     * 
     * @return              <code>true</code> if user-given name was found in
     *                      the folder, <code>false</code> otherwise.
     */
    private static boolean searchCharacterFolder (Character character, String name) {
        
        String characterLogDirectoryString = FFXIV_FOLDER + FOLDER_DIVIDER + character.getId() + FOLDER_DIVIDER + "log" + FOLDER_DIVIDER;
        File characterLogDirectory = new File(characterLogDirectoryString);
        
        File[] logFiles = characterLogDirectory.listFiles(new FilenameFilter(){
            public boolean accept (File dir, String name) {
                return name.endsWith("log");
            }
        });
        
        if (logFiles != null && logFiles.length > 0) {
            
            for (File file : logFiles) {
                
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
    }  //  end method searchCharacterFolder()
    
    
    
    /**
     * <p>TODO:  Likely should rename this so that the return value makes more sense.</p>
     * <p>TODO:  Determine if the <code>found</code> value (and return value) is significant beyond whether it is zero or non-zero.</p>
     * 
     * <p>After <code>scanBackups()</code> has been run at least once, this
     * checks each <code>Character</code> object in <code>characters</code>
     * for one whose <code>identified()</code> method is false.  Each time one
     * is found, <code>found</code> is incremented.</p>
     * 
     * <p>After iterating through <code>characters</code>,
     * <code>unidentifiedCharacters</code> is set to <code>true</code> if at least one
     * unidentified character was discovered; <code>false</code> otherwise.</p>
     * 
     * @return     Number of unidentified characters found.
     */
    private static int verifyScannedBackups() {
        System.out.println("\nVerifying scanned backups...");
        int found = 0;
        
        for (Character character : characters) {
            
            if (!character.identified()) {
                
                found++;
                
            }  // if the character has no name (hasn't been identified)
        
        }  //  for each item in characters
        
        unidentifiedCharacters = found > 0 ? true : false;
        return found;
        
    }  //  end method verifiyScannedBackups()

    
    
    
    
    /**
     * TODO:  Consider making UnidentifiedCharactersFrame logic into a ProfileManager method, and remove the class.
     * 
     * <p>Frame used to display all unidentified characters.</p>
     */
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
            
            //Get a charactersList of identified characters:
            ArrayList<JPanel> characterRadioButtonList = new ArrayList<>();
            for (Character character : characters) {
                if (character.identified())
                    characterRadioButtonList.add(character.getCharacterPanel());
            }
            
            JList<JPanel> charactersList = new JList<JPanel>() {
                private static final long serialVersionUID = 1L;
                @Override public Dimension getPreferredSize() {
                    int width = 250;
                    int rows = characterRadioButtonList.size();
                    int height = 5 * rows;
                    return new Dimension(width, height);
                }
            };
            
            for (JPanel panel : characterRadioButtonList) {
                charactersList.add(panel);
            }
            
            charactersList.setLayout(new FlowLayout());
            charactersList.setLayoutOrientation(JList.VERTICAL);
            
            JScrollPane pane = new JScrollPane(charactersList);
            pane.getVerticalScrollBar().setUnitIncrement(100);
            pane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
            
            charactersList.validate();
            pane.validate();
            
            setVisible(true);
            
            return pane;
            
        }
        
    }  //  end class UnidentifiedCharactersFrame

    private static class FileMismatchException extends Exception {

        public FileMismatchException() {
        }
    }
    
}  //  end class ProfileManager