package com.magipixel.ffxivmanager.util;

/**
 * Changelog: 2016-01-29 : Added support for working on Mac OS X. 2016-01-29 :
 * Refactored backupActiveProfile(), 2016-01-29 : Removed chooseProfileName(),
 * not needed.
 *
 * 2016-01-29 : Added activeProfileName. 2016-01-29 : Updated logic for defining
 * FFXIV_FOLDER, for Windows user path. 2016-01-29 : Added addCharacter()
 * method, to prevent duplication of Character objects created by scanBackups()
 * 2016-01-29 : Add much more to loadConfig(); now it reads the JSON file and
 * creates appropriate Character/Profile/Backup objects
 *
 * 2016-01-30 : Added identifyAllCharacters() method 2016-01-30 : Changed the
 * first if/while loop in saveConfig() to one call to identifyAllCharacters()
 * 2016-01-30 : Added call to identifyAllCharacters() to end of loadConfig()
 * 2016-01-30 : Changed loadConfig() to call identifyAllCharacters() at the end
 * 2016-01-30 : Added getActiveCharacter() method. 2016-01-30 : Changed init()
 * to call getActiveCharacter() when it isn't already set 2016-01-30 : Fixed
 * initConfig() to account for non-null active character/profile 2016-01-30 :
 * Changed initConfig()'s name to saveConfig() 2016-01-30 : Updated loadConfig()
 * so that it sets activeCharacter using getCharacter(activeCharacterName)
 *
 * 2016-02-01 : Did some refactoring.
 *
 * 2016-02-05 : Added detectFolderChanges(File, File) 2016-02-05 : Added
 * FileMismatchException 2016-02-05 : Added a call to detectFolderChanges() in
 * init() 2016-02-05 : Updated backupActiveProfile() to actually copy files to a
 * new backup directory
 */
/**
 * TODO: (2016-01-29) Add way to manage all characters at once, rather than one
 * active character (perhaps using tabs)
 */
import static com.magipixel.ffxivmanager.ProfileManagerApplication.*;
import com.magipixel.ffxivmanager.components.Profile;
import com.magipixel.ffxivmanager.ui.CharacterFrame;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

    private static Logger logger
            = Logger.getLogger(ProfileManager.class.getSimpleName());

//    private static ProfileManager theManager = new ProfileManager();
    private CharacterManager characterManager;

//    private static final String DATA_FILE_NAME = "data.json";
//  Assumes that whatever is active in the user directory is the
    //  application's active profile.
    private Profile activeProfile;
    private String activeProfileName;

    private Character activeCharacter;
    private String activeCharacterName;
    private String activeCharacterID;

    private ArrayList<Character> characters;

    public ProfileManager() {
        characterManager = new CharacterManager(getCharacters());
    }

    /**
     * Probably isn't needed.
     */
    private boolean unidentifiedCharacters;

    //  TODO:  Consider making this a local class:
    private TagManager tagManager;

    /**
     * <p>
     * Checks if a similar character (same folder ID) has already been added. If
     * so, this combines the data by copying it to the existing character.</p>
     *
     * @param c New to be added (and verified)
     */
    public void addCharacter(Character c) {
        for (Character character : getCharacters()) {
            if (character.getId().equals(c.getId())) {
                character.setName(c.getName());
                character.setIdentified(true);
                character.setNamed(true);
                return;
            }
        }  //  for each item in characters
        getCharacters().add(c);
    }  //  end method addCharacter()

    /**
     * <p>
     * Asks the user to identify, from a list of all identified characters,
     * which character should be the current active one</p>
     *
     * @return Active character, chosen by user
     */
//    private Character chooseActiveCharacter() {
//        CharacterFrame charactersFrame;
//        boolean madeSelection = false;
//        Character characterSelected;
//
//        charactersFrame = CharacterFrame.createFrame(getCharacters());
//
//        charactersFrame.setVisible(true);
//
//        //  DEBUG:
//        //System.out.println("\nWaiting for user input...");
//        while (charactersFrame.getActiveCharacterName().equals("")) {
//            //  spin on user input
//            //System.out.print("...|");
////            System.out.print("");
//        }
//
//        //  DEBUG:
//        //System.out.printf("\nUser gave input (\"%s\").\n", charactersFrame.getName());
//        setActiveCharacterName(charactersFrame.getName());
//        return findCharacter(charactersFrame.getName());
//
//    }
    /**
     * This runs during application startup, to initialize a
     * <code>Profile</code> object from the parameters of the last active
     * profile in the file isIdentified by <code>DATA_FILE_NAME</code>.
     *
     * TODO: Add functionality to handle multiple characters by ID
     *
     * @param name The name of the last active profile, from
     * <code>DATA_FILE_NAME</code>.
     *
     * @return         <code>Profile</code> object corresponding to the last (and
     * current) active profile
     */
    private Profile findActiveProfile(String name) {

        System.out.println("Getting active profile from name");

        // TODO:  Make sure that activeCharacterName and activeCharacterID really will be set at this point, not just because they are hard-coded in main().
        return new Profile(name, getActiveCharacterName(), getActiveCharacterID(),
                getFFXIV_FOLDER() + getFOLDER_DIVIDER());
    }

    /**
     * TODO: Need to implement code for multiple characters of same name
     * (different server)
     *
     * Given the name of the character, this method locates the item in
     * <code>characters</code> that matches that name and returns it.
     *
     * @param name Name of character to locate
     * @return Appropriate character or <code>null</code>
     */
    public Character findCharacter(String name) {
        for (Character character : getCharacters()) {
            if (character.getName().equals(name)) {
                return character;
            }  //  if the character's name matches the search string
        }  // for each item in characters
        return null;
    }  // end method getCharacter()

    /**
     * <p>
     * If <code>scanBackups()</code> returns false, the method returns early.
     * Otherwise, it checks if <code>file</code> exists. If it does,
     * <code>loadConfig()</code> is executed; otherwise
     * <code>saveConfig()</code> is executed.</p>
     *
     * <p>
     * TODO: Need to make this ask for <b>ALL</b> characters' full names and
     * then identify each character's folder from the log files.</p>
     */
    public void init() {
        logger.info("Initializing Profile Manager");
        if (characters == null) {
            setCharacters(new ArrayList<>());
        }

//        File file = null;
//        try {
//            file = new File(DATA_FILE_NAME);
//        } catch (Exception e) {
//            logger.log(Level.SEVERE, "Exception creating file handle!");
//        }
        //  Check if any folders exist:
//        if (isFirstTimeStartup()) {
//
//            //  Check if this is first load:
//            if (file.exists()) {
//                loadConfig(file);
//            } else {
//                //  TODO:  Since saveConfig() runs at the end of this method, see if this call can be eliminated.
//                saveConfig(file);
//            }
//
//        } else {
//            System.out.println("Scanning of backups failed, or no backups exist.");
//            return;
//        }
        //  Check if the active character isn't chosen, and have the user choose it (if needed):
//        if ("@Not_Selected".equals(getActiveCharacterName())
//                || getActiveCharacter() == null) {
//            setActiveCharacter(chooseActiveCharacter());
//            setActiveCharacterID(getActiveCharacter().getId());
//        }
        //  Check if the active character's folder has been changed since the
        //  last update:
        try {
            if (true) {
            //if (detectFolderChanges(activeCharacter)) {

                //  DEBUG:
                System.out.println("\nChanges detected in profile...");
                Profile testProfile
                        = new Profile("Test Profile",
                                getActiveCharacter().getName(),
                                getActiveCharacter().getId(),
                                getFFXIV_FOLDER());
                getActiveCharacter().addProfile(testProfile);
                setActiveProfile(testProfile);

                //  TODO: Determine if the return value should be used (or refactored out..):
                BackupManager
                        .backupActiveProfile(getActiveCharacterName(), getActiveProfile(), getActiveCharacterID());
            }
        } catch (Exception e) {
            //System.out.println("\ndetectFolderChanges() failed!");
            e.printStackTrace();
        }

//        saveConfig(file);
    }  //  end init() method

    public boolean isFirstTimeStartup() {
        return BackupManager.scanBackups(getCharacters());
    }

    /**
     * <p>
     * <code>scanBackups()</code> is assumed to have already run before this
     * method runs.</p>
     *
     * <p>
     * Loads the configuration file, whose name is identified by
     * <code>DATA_FILE_NAME</code>, into memory.</p>
     *
     * <p>
     * It then verifies that each character folder contained within is matched
     * with a corresponding entry in <code>characters</code></p>
     *
     * @param file     <code>File</code> object, whose name is determined by
     * <code>DATA_FILE_NAME</code>.
     *
     * @return pass/fail status
     */
    //private String loadConfig(File file) {
//    private boolean loadConfig(File file) {
//
//        System.out.println("Loading " + DATA_FILE_NAME);
//
//        Scanner scanner = null;
//
//        try {
//            scanner = new Scanner(file);
//        } catch (Exception e) {
//            System.out.println("loadConfig() exception!");
//            return false;
//        }
//
//        System.out.println("Reading JSON file...");
//
//        JSONParser parser = new JSONParser();
//
//        try {
//
//            Object obj = parser.parse(new FileReader(
//                    DATA_FILE_NAME));
//
//            JSONObject rootJsonObject = (JSONObject) obj;
//
//            setActiveProfileName((String) rootJsonObject.get("Active Profile"));
//            setActiveCharacterName((String) rootJsonObject.get("Active Character"));
//
//            JSONObject charactersRootJSON
//                    = (JSONObject) rootJsonObject.get("Characters");
//
//            ArrayList<JSONObject> charactersJSON = new ArrayList<>();
//            Set<Map.Entry<String, Object>> characterSets
//                    = charactersRootJSON.entrySet();
//
//            for (Map.Entry<String, Object> characterEntry : characterSets) {
//                //  DEBUG:
//                //System.out.printf("\nnewCharacterName = \"%s\"", characterEntry.getKey());
//
//                String newCharacterName = characterEntry.getKey();
//                JSONObject newCharacterJSON
//                        = (JSONObject) characterEntry.getValue();
//                String newCharacterId = (String) newCharacterJSON.get("ID");
//                Character newCharacter
//                        = Character.createCharacter(newCharacterName, newCharacterId);
//
//                ArrayList<Profile> profiles = new ArrayList<>();
//                Set<Map.Entry<String, Object>> profileSets
//                        = newCharacterJSON.entrySet();
//                for (Map.Entry<String, Object> profileEntry : profileSets) {
//
//                    //  TODO: Refactor this if into something more streamlined:
//                    if (profileEntry.getKey().equals("ID")
//                            || profileEntry.getKey().equals("Active Profile")) {
//                        continue;
//                    }
//
//                    String newProfileName = profileEntry.getKey();
//                    JSONArray newProfileArray
//                            = (JSONArray) profileEntry.getValue();
//
//                    ArrayList<String> backups = new ArrayList<>();
//                    Iterator<?> profileIterator = newProfileArray.iterator();
//                    while (profileIterator.hasNext()) {
//                        backups.add((String) profileIterator.next());
//                    }  // collect backup names
//
//                    Profile newProfile
//                            = new Profile(newProfileName, newCharacterName,
//                                    newCharacterId, getFFXIV_FOLDER(), backups);
//                    newCharacter.addProfile(newProfile);
//
//                }  //  iterate through all profiles
//
//                addCharacter(newCharacter);
//
//                //  DEBUG:
//                //System.out.printf("\nnewCharacter.getName() = \"%s\"", newCharacter.getName());
//            }  //  iterate through characters
//
//            //   DEBUG:  Display Results:
//            System.out.println("\n\nActive Profile: " + getActiveProfileName());
//            System.out.println("Active Character: " + getActiveCharacterName());
//            System.out.println("\nCharacters:");
//
//            for (Character character : getCharacters()) {
//                System.out.println("    " + character.getName() + ":");
//                System.out.println("        ID: " + character.getId());
//
//                System.out.println("\n        Profiles:");
//                for (Profile profile : character.getProfiles()) {
//                    System.out.println("            " + profile.getName());
//                    //  TODO: print out profile backups
//
//                }  // iterate through profiles
//
//            }  // iterate through characters
//            //  END DEBUG
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//
//        //  Verify that all characters are isIdentified, and take action if not:
//        characterManager.identifyAllCharacters();
//
//        setActiveCharacter(findCharacter(getActiveCharacterName()));
//        setActiveCharacterID(getActiveCharacter().getId());
//
//        return true;
//    }  //  end method loadConfig()
    /**
     * <p>
     * TODO: For now, this will simply overwrite the files in the current
     * directory with whichever one(s) are in the backup being loaded.</p>
     *
     * <p>
     * TODO: Implement way for this to return false whenever user attempts to
     * load the active profile (or prevent this somewhere else, altogether).</p>
     *
     * Initiates profile load procedure by backing up the active profile, and
     * then calling the <code>loadProfile()</code> method on the
     * <code>character</code> parameter.
     *
     * @return
     */
    private boolean loadProfile(Character character) {

        BackupManager.backupActiveProfile(getActiveCharacterName(), getActiveProfile(), getActiveCharacterID());

        Profile loadedProfile = character.loadProfile();

        if (loadedProfile != null) {
            setActiveProfile(loadedProfile);
            setActiveCharacterName(character.getName());
            setActiveCharacterID(character.getId());
        }
        return true;
    }  //  end method loadProfile()

    /**
     * <p>
     * Overwrites the configuration file, who's name was previously determined
     * by <code>DATA_FILE_NAME</code>.</p>
     *
     * <p>
     * Parameters beginning with a '<code>@</code>' are place holders.</p>
     *
     * @param file Configuration file, whose name is determined by
     * <code>DATA_FILE_NAME</code>.
     */
    //private void saveConfig (File file, String name) {
//    private void saveConfig(File file) {
//
//        System.out.println("\nSaving configuration settings...");
//
//        characterManager.identifyAllCharacters();
//
//        //  =================  Begin JSON Processing  =================
//        System.out.println("Initializing " + DATA_FILE_NAME);
//        System.out.println("Writing JSON file...");
//
//        //  Root
//        JSONObject rootJSON = new JSONObject();
//        if (getActiveCharacter() != null && getActiveCharacterName() != null) {
//            rootJSON.put("Active Character", getActiveCharacterName());
//        } else {
//            rootJSON.put("Active Character", "@Not_Selected");
//        }
//
//        if (getActiveProfile() != null && getActiveProfileName() != null) {
//            rootJSON.put("Active Profile", getActiveProfileName());
//        } else {
//            rootJSON.put("Active Profile", "@Not_Selected");
//        }
//
//        JSONObject charactersRootJSON = new JSONObject();
//
//        //  TODO:  This is just to make it work, for now...
//        int increment = 0;
//
//        for (Character character : getCharacters()) {
//
//            JSONObject characterJSON = new JSONObject();
//            characterJSON.put("ID", character.getId());
//            String name = character.getName() != null ? character.getName() : "@Unknown_" + increment++;
//
//            charactersRootJSON.put(name, characterJSON);
//
//            //  DEBUG:
//            //System.out.println("\nJSON Snapshot:\n" + charactersRootJSON);
//        }
//
//        rootJSON.put("Characters", charactersRootJSON);
//
//        try (FileWriter fileWriter = new FileWriter(DATA_FILE_NAME)) {
//            fileWriter.write(rootJSON.toJSONString());
//            System.out.println("Successfully Copied JSON Object to File...");
//
//        } catch (IOException e) {
//            System.out.println("IOException: " + e.getLocalizedMessage());
//        }
//
//    }  //  end method saveConfig()
    /**
     * @return the activeProfile
     */
    public Profile getActiveProfile() {
        return activeProfile;
    }

    /**
     * @param activeProfile the activeProfile to set
     */
    private void setActiveProfile(Profile activeProfile) {
        this.activeProfile = activeProfile;
    }

    /**
     * @return the activeCharacterName
     */
    // TODO - this probably should be removed so that there's not two locations for the name (it already lives in the character!)
    public String getActiveCharacterName() {
        return activeCharacterName;
    }

    /**
     * @param activeCharacterName the activeCharacterName to set
     */
    public void setActiveCharacterName(String activeCharacterName) {
        this.activeCharacterName = activeCharacterName;
    }

    /**
     * @return the activeCharacterID
     */
    private String getActiveCharacterID() {
        return activeCharacterID;
    }

    /**
     * @param activeCharacterID the activeCharacterID to set
     */
    public void setActiveCharacterID(String activeCharacterID) {
        this.activeCharacterID = activeCharacterID;
    }

    /**
     * @return the characters
     */
    public ArrayList<Character> getCharacters() {
        if (characters == null) {
            characters = new ArrayList<Character>();
        }
        return characters;
    }

    /**
     * @param characters the characters to set
     */
    private void setCharacters(ArrayList<Character> characters) {
        this.characters = characters;
    }

    /**
     * @return the activeCharacter
     */
    public Character getActiveCharacter() {
        return activeCharacter;
    }

    /**
     * @param activeCharacter the activeCharacter to set
     */
    public void setActiveCharacter(Character activeCharacter) {
        this.activeCharacter = activeCharacter;
        this.activeCharacterID = activeCharacter.getId();
    }

    /**
     * @return the activeProfileName
     */
    public String getActiveProfileName() {
        return activeProfileName;
    }

    /**
     * @param activeProfileName the activeProfileName to set
     */
    public void setActiveProfileName(String activeProfileName) {
        this.activeProfileName = activeProfileName;
    }

    /**
     * Delagate method for {@link characterManager}
     */
    public void identifyAllCharacters() {
        characterManager.identifyAllCharacters();
    }

    /**
     * Delagate method for {@link characterManager}
     */
    public void nameAllCharacters() {
        characterManager.nameAllCharacters();
    }

    public void setDefaultCharacter() {
        setActiveCharacter(findCharacter(getActiveCharacterName()));
        setActiveCharacterID(getActiveCharacter().getId());
    }

    /**
     * TODO: Consider making UnidentifiedCharactersFrame logic into a
     * ProfileManager method, and remove the class.
     *
     * <p>
     * Frame used to display all unidentified characters.</p>
     */
    private class UnidentifiedCharactersFrame extends JFrame {

        public UnidentifiedCharactersFrame() {
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

            int width = 300;
            int height = 500;
            setMinimumSize(new Dimension(width, height));
            setVisible(true);

        }  //  end UnidentifiedCharactersFrame constructor

        private JScrollPane buildIdentifiedCharacterList() {

            //Get a charactersList of isIdentified characters:
            ArrayList<JPanel> characterRadioButtonList = new ArrayList<>();
            for (Character character : getCharacters()) {
                if (character.isIdentified()) {
                    characterRadioButtonList.add(character.getCharacterPanel());
                }
            }

            JList<JPanel> charactersList = new JList<JPanel>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Dimension getPreferredSize() {
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

}  //  end class ProfileManager
