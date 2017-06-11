/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magipixel.ffxivmanager.ui;

import com.magipixel.ffxivmanager.components.Profile;
import static com.magipixel.ffxivmanager.ProfileManagerApplication.getFFXIV_FOLDER;
import com.magipixel.ffxivmanager.server.DataCenter;
import com.magipixel.ui.modal.AnswerDialog;
import com.magipixel.ffxivmanager.util.Character;
import com.magipixel.ffxivmanager.util.CharacterManager;
import com.magipixel.ffxivmanager.util.ProfileManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Alan
 */
public class MainStage {
    /**
     * 
     */
    Logger logger = Logger.getLogger(MainStage.class.getSimpleName());
    
    private static final String DATA_FILE_NAME = "data.json";
    
    private ProfileManager profileManager;
    
    private JSONObject jsonConfig;
    
    /**
     * 
     */
    Stage window;
    
    /**
     * 
     */
    private boolean hasBeenSetup = false;
    
    public MainStage() {
        profileManager = new ProfileManager();
    }

    /**
     * 
     * @return 
     */
    public boolean init() {
        //    ===================  Get File Handle  ====================
        File file = null;
        try {
            file = new File(DATA_FILE_NAME);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Exception creating file handle!");
        }
        
        //    =============  Check for  First-Time Setup  ==============
        boolean firstTimeStartup = profileManager.isFirstTimeStartup();
        logger.info("Character folders found: " + profileManager.getCharacters().size());
        if (firstTimeStartup) {
            logger.config("First time startup.");
            //  Check if this is first load:
            if (file.exists()) {
                logger.config(DATA_FILE_NAME + " exists.");
                loadConfig(file);
            } else {
                logger.config(DATA_FILE_NAME
                        + "does not exist. Creating the file.");
                //  TODO:  Since saveConfig() runs at the end of this method, see if this call can be eliminated.
                saveConfig(file);
            }
        } else {
            logger.severe("Scanning of backups failed, or no backups exist.");
            return false;
        }
        
        //    =================  Finish Initializing  ==================
        //  Check if the active character isn't chosen, and have the user choose
        //  it (if needed):
        if (NOT_SELECTED.equals(profileManager.getActiveCharacterName())
                || profileManager.getActiveCharacter() == null) {
            profileManager.setActiveCharacter(chooseActiveCharacter());
        }
        profileManager.init();
        // TODO - find out why this second run erases all the characters
        saveConfig(file);
        return true;
    }
    
    /**
   * <p>
   * Displays the main stage(window).</p>
   */
  public void display() {
      if (!hasBeenSetup) {
            setupGeneral();

//    buildShiftViewTable();
    // ===========================   Tab Area  ============================
//    fileTab = new Tab("File");
//    fileTab.setClosable(false);
//    fileTab.getStyleClass().add("file");
//    fileWindow = new VBox();
//    fileTab.setContent(fileWindow);

    //      =====================  Schedule Tab  =====================
//    buildScheduleTab();

    //      ======================  Manage Tab  ======================
//    buildManageTab();

    // ==========================  Filters Row   ==========================
//    buildFiltersBox();

    //      ====================  Finalize Tabs   ====================
//    tabPane = new TabPane();
//    tabPane.getTabs().addAll(fileTab, scheduleTab, manageTab);
//    tabPane.getSelectionModel().select(scheduleTab);
//    rootLayout.setTop(tabPane);
//    VBox displayBox = new VBox();
//    displayBox.getChildren().addAll(filtersBox, outputTable);
//    rootLayout.setCenter(displayBox);

        // =============================  Finish   ============================
    //  TODO: Change modality once this is actually the main window:
    window.initModality(Modality.APPLICATION_MODAL);
//    populateShiftViewTable();
//    resize();
//        window.setMaximized(true);
    hasBeenSetup = true;
      }
    window.showAndWait();

  }  //  end method display(Person)

  /**
     * <p>
 Asks the user to identify, from a list of all isIdentified characters,
 which character should be the current active one</p>
     *
     * @return Active character, chosen by user
     */
    private Character chooseActiveCharacter() {
        CharacterFrame charactersFrame;
        boolean madeSelection = false;
        Character characterSelected;
        
        List<String> characterNames = new ArrayList<>();
        for (Character character : profileManager.getCharacters()) {
            
            characterNames.add(character.getName());
        }
        
//        charactersFrame
//                = new CharacterFrame(profileManager.getCharacters());
//
//        charactersFrame.setVisible(true);

        //  DEBUG:
        //System.out.println("\nWaiting for user input...");
        String title = "Identify Active Character";
        String answer;
        do {
            answer = AnswerDialog.display(title, null, characterNames,
                AnswerDialog.Orientation.LANDSCAPE);
        } while (answer == null);

        //  DEBUG:
        //System.out.printf("\nUser gave input (\"%s\").\n", charactersFrame.getName());
        profileManager.setActiveCharacterName(answer);
        return profileManager.findCharacter(answer);

    }

  
  /**
     * <p><code>scanBackups()</code> is assumed to have already run before this
     * method runs.</p>
     * 
     * <p>Loads the configuration file, whose name is isIdentified by
 <code>DATA_FILE_NAME</code>, into memory.</p>
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
    //private String loadConfig(File file) {
    private boolean loadConfig(File file) {
        
        logger.info("Loading " + DATA_FILE_NAME);
        
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
 
            jsonConfig = (JSONObject) obj;
            
            // TODO - these need to be moved down to after the characters have all been created, so that the active character name doesn't have to be stored
            profileManager.setActiveProfileName((String) jsonConfig.get(ACTIVE_PROFILE));
            profileManager.setActiveCharacterName((String) jsonConfig.get(ACTIVE_CHARACTER));
            
            JSONObject charactersRootJSON =
                    (JSONObject) jsonConfig.get("Characters");
            
            ArrayList<JSONObject> charactersJSON = new ArrayList<>();
            Set<Map.Entry<String, Object>> characterSets =
                    charactersRootJSON.entrySet();
            
            for (Map.Entry<String, Object> characterEntry : characterSets) {
                //  DEBUG:
                //System.out.printf("\nnewCharacterName = \"%s\"", characterEntry.getKey());
                
                String newCharacterName = characterEntry.getKey();
                JSONObject newCharacterJSON =
                        (JSONObject) characterEntry.getValue();
                String newCharacterId = (String) newCharacterJSON.get(ID);
                Character newCharacter =
                        Character.createCharacter(newCharacterName, newCharacterId, DataCenter.PRIMAL);
                
                ArrayList<Profile> profiles = new ArrayList<>();
                Set<Map.Entry<String, Object>> profileSets =
                        newCharacterJSON.entrySet();
                for (Map.Entry<String, Object> profileEntry : profileSets) {
                    
                    //  TODO: Refactor this if into something more streamlined:
                    if (profileEntry.getKey().equals(ID) ||
                        profileEntry.getKey().equals(ACTIVE_PROFILE))
                        continue;
                    
                    String newProfileName = profileEntry.getKey();
                    JSONArray newProfileArray =
                            (JSONArray) profileEntry.getValue();
                    
                    ArrayList<String> backups = new ArrayList<>();
                    Iterator<?> profileIterator = newProfileArray.iterator();
                    while (profileIterator.hasNext()) {
                        backups.add((String) profileIterator.next());
                    }  // collect backup names
                    
                    Profile newProfile =
                            new Profile(newProfileName, newCharacterName,
                                    newCharacterId, getFFXIV_FOLDER(), backups);
                    newCharacter.addProfile(newProfile);
                    
                }  //  iterate through all profiles
                
                // TODO - How does profileManager already have characters here?
                profileManager.addCharacter(newCharacter);
                
                //  DEBUG:
                //System.out.printf("\nnewCharacter.getName() = \"%s\"", newCharacter.getName());
            }  //  iterate through characters
            
            
            
            //   DEBUG:  Display Results:
            System.out.println("\n\nActive Profile: "
                             + profileManager.getActiveProfileName());
            System.out.println("Active Character: "
                             + profileManager.getActiveCharacterName());
            System.out.println("\nCharacters:");
            
            for (Character character : profileManager.getCharacters()) {
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
        
        //  Verify that all characters are isIdentified, and take action if not:
        profileManager.identifyAllCharacters();
        profileManager.nameAllCharacters();
        
        profileManager.setDefaultCharacter();
        
        
        return true;
    }  //  end method loadConfig()
    
    /**
     * <p>Overwrites the configuration file, who's name was previously
     * determined by <code>DATA_FILE_NAME</code>.</p>
     * 
     * <p>Parameters beginning with a '<code>@</code>' are place holders.</p>
     * 
     * @param file     Configuration file, whose name is determined by
     *                 <code>DATA_FILE_NAME</code>.
     */
    //private void saveConfig (File file, String name) {
    private void saveConfig (File file) {
        
        logger.info("Saving configuration settings...");
        profileManager.identifyAllCharacters();
        profileManager.nameAllCharacters();
        
        //  =================  Begin JSON Processing  =================
        
        logger.info("Initializing " + DATA_FILE_NAME);
        logger.info("Writing JSON file...");
        
        // TODO - this logic may need to be changed, to handle characters created after the app has been in use for a while
        if (jsonConfig == null) {
            jsonConfig = jsonCreateNew();
        } else {
            jsonSetActiveCharacter(jsonConfig);
            jsonSetActiveProfile(jsonConfig);
        }
        
        //  =======================  Save JSON  =======================
        try (FileWriter fileWriter = new FileWriter(DATA_FILE_NAME)) {
            fileWriter.write(jsonConfig.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            
        } catch (IOException e) {
            System.out.println("IOException: " + e.getLocalizedMessage());
        }
        
    }  //  end method saveConfig()

    private JSONObject jsonCreateNew() {
        //  Root
        JSONObject rootJSON = new JSONObject();
        jsonSetActiveCharacter(rootJSON);
        jsonSetActiveProfile(rootJSON);
        
        JSONObject charactersRootJSON = new JSONObject();
        //  TODO:  This is just to make it work, for now...
        int increment = 0;
        for (Character character : profileManager.getCharacters()) {
            final String serverName = character.getServer().getName();
            
            JSONObject serverJSON =
                    (JSONObject) charactersRootJSON.get(serverName);
            if (serverJSON == null) {
                serverJSON = new JSONObject();
            }
            
            JSONObject characterJSON = new JSONObject();
            characterJSON.put(ID, character.getId());
            
            final String characterName = character.getName();
            String name = characterName != null
                    ? characterName : UNKNOWN_PREFIX + increment++;

            charactersRootJSON.put(serverName, serverJSON);
            serverJSON.put(name, characterJSON);
            
            //  DEBUG:
            //System.out.println("\nJSON Snapshot:\n" + charactersRootJSON);
            
        }
        rootJSON.put("Characters", charactersRootJSON);
        return rootJSON;
    }

    private void jsonSetActiveProfile(JSONObject rootJSON) {
        final String activeProfileName =
                profileManager.getActiveProfileName();
        
        if (profileManager.getActiveProfile() != null
                && activeProfileName != null) {
            rootJSON.put(ACTIVE_PROFILE, activeProfileName);
        } else {
            rootJSON.put(ACTIVE_PROFILE, NOT_SELECTED);
        }
    }

    private void jsonSetActiveCharacter(JSONObject rootJSON) {
        final String activeCharacterName =
                profileManager.getActiveCharacterName();
        
        if (profileManager.getActiveCharacter() != null
                && activeCharacterName != null) {
            rootJSON.put(ACTIVE_CHARACTER, activeCharacterName);
        } else {
            rootJSON.put(ACTIVE_CHARACTER, NOT_SELECTED);
        }
    }
    private static final String UNKNOWN_PREFIX = "@Unknown_";
    private static final String ID = "ID";
    private static final String ACTIVE_PROFILE = "Active Profile";
    private static final String NOT_SELECTED = "@Not_Selected";
    private static final String ACTIVE_CHARACTER = "Active Character";
    
  
  /**
   * 
   */
    private void setupGeneral() {
        try {
        logger.setLevel(Level.INFO);
        } catch (SecurityException e) {
            // do nothing
        }
        window = new Stage();
        window.setTitle("Final Fantasy XIV Profile Manager");
        BorderPane rootLayout = new BorderPane();
        int width = 650;
        int height = 300;
        Scene scene = new Scene(rootLayout, width, height);
        height = 0;
        scene.getStylesheets().add("css/mainStage.css");
        window.setScene(scene);
        
        window.setMinWidth(width);
        window.setMinHeight(height);
        // TODO - make this based on last known settings
        window.setMaximized(true);
        
        //    ======================  Menu  Bar   ======================
        
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu helpMenu = new Menu("Help");
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        rootLayout.setTop(menuBar);
        

    }
  
}
