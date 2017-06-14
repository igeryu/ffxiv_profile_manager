package com.magipixel.ffxivmanager.util;


import com.magipixel.ffxivmanager.util.CharacterManager;
import com.magipixel.ffxivmanager.util.BackupManager;
import com.magipixel.ffxivmanager.util.Character;
import com.magipixel.ffxivmanager.ProfileManagerApplication;
import com.magipixel.ffxivmanager.server.Primal;
import com.magipixel.ffxivmanager.ui.CharacterFrame;
import com.magipixel.ffxivmanager.util.ProfileManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import mockit.Expectations;

import mockit.MockUp;
import mockit.Mock;
import mockit.Mocked;
import mockit.Tested;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
//import org.mockito.Mockito;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alan
 */
public class ProfileManagerTest {
    private static final String DATA_FILE_NAME = "data.json";
    @Tested
    private ProfileManager theManager;
    private static final String ESPRA_LAROB = "Espra Larob";
    private static final String FEYEN_AEN = "Feyen Aen";
    private static final String ESPRA_ID = "FFXIV_CHR004000000099AB02";
    private static final String FEYEN_ID = "FFXIV_CHR00400000009D4722";
    private static final String NOT_SELECTED = "@Not_Selected";
    private static final String CHARACTERS = "Characters";
    private static final String ACTIVE_CHARACTER = "Active Character";
    private static final String ACTIVE_PROFILE = "Active Profile";
    
    @Mocked
    private FileReader fileReader;
    
    @Mocked
    private JSONParser jsonParser;
    
    @Mocked
    private Scanner scanner;
    
    @Mocked
    CharacterManager characterManager;
    
//    @Mocked
//    private File file;
    
//    @Mocked
//    private CharacterFrame characterFrame;
    
    @BeforeClass
    public static void beforeClass() {
        new MockUp<ProfileManagerApplication>() {
          @Mock
          public String getFFXIV_FOLDER() {
              return System.getProperty("user.home") + "\\Documents\\My Games\\FINAL FANTASY XIV - A Realm Reborn\\";
          }
          
          @Mock
          public String getFOLDER_DIVIDER() {
              return "\\";
          }
        };
        
        new MockUp<BackupManager>() {
          @Mock
          public boolean scanBackups (ArrayList<Character> characters) {
              characters.add(Character.createCharacter(FEYEN_AEN, FEYEN_ID, Primal.EXCALIBUR));
              characters.add(Character.createCharacter(ESPRA_LAROB, ESPRA_ID, Primal.EXCALIBUR));
              return true;
          }
        };
        
        new MockUp<CharacterFrame>() {
            @Mock
            public void $init() {
                // Do nothing
            }
            
            @Mock
            public String getActiveCharacterName() {
//                setVisible(false);
                return "test string";
            }
        };
    
        
    }
    
    /**
     * Ensures that the application is told that "data.json" exists.
     */
    private void setupDataExists() {
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }
        };
        
        new MockUp<JSONObject>() {
          @Mock
          public Object get(Object key) {
              String keyString = (String) key;
              if (ACTIVE_PROFILE.equals(keyString)) {
                  return NOT_SELECTED;
              } else if (ACTIVE_CHARACTER.equals(keyString)) {
                  return FEYEN_AEN;
              } else if (CHARACTERS.equals(keyString)) {
                  return new JSONObject();
              }
              return null;
          }
        };
    }
    
    
    /**
     * Ensures that the application is told that "data.json" exists.
     */
    private void setupDataNotExists() {
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return false;
            }
        };
    }
    
    /**
     * Sets up the file system for testing.
     * <ul>
     * <li>Deletes the</li>
     * </ul>
     */
    @Before
    public void setupTest() {
        theManager = new ProfileManager();
        
        // Delete 'data.json'
        File file = null;
        try {
            file = new File(DATA_FILE_NAME);
        } catch (Exception e) {
            System.out.println("Exception!");
        }
        file.delete();
        
        
    }
    
    /**
     * Creates a default <code>data.json</code> file for testing.
     */
    private void createDataJson() {
        
        System.out.println("Writing JSON file...");
        
        JSONObject obj = new JSONObject();
        
        // Assign active character
        obj.put(ACTIVE_CHARACTER, FEYEN_AEN);
        obj.put(ACTIVE_PROFILE, "MyProfile");

        // =====  Add character data  =====
        JSONObject characters = new JSONObject();
        
        // Feyen Aen
        JSONObject feyenAen = new JSONObject();
        feyenAen.put("ID", FEYEN_ID);
        JSONArray myProfile = new JSONArray();
        myProfile.add("Backup1 Timestamp");
	myProfile.add("Backup2 Timestamp");
        feyenAen.put("MyProfile", myProfile);
        
        // Espra Larob
        JSONObject espraLarob = new JSONObject();
        espraLarob.put("ID", ESPRA_ID);
        myProfile = new JSONArray();
        myProfile.add("Backup1 Timestamp");
	myProfile.add("Backup2 Timestamp");
        espraLarob.put("MyProfile", myProfile);
        
        // Add all characters to 'obj'
        characters.put(FEYEN_AEN, feyenAen);
        characters.put(ESPRA_LAROB, espraLarob);
        obj.put(CHARACTERS, characters);

        // try-with-resources statement based on post comment below :)
        try (FileWriter file = new FileWriter("jsontest.txt")) {
            file.write(obj.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + obj);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getLocalizedMessage());
        }
        
    }
    
    
    /**
     * 
     */
    @Test
    public void test_dataExists() {
        createDataJson();
        setupDataExists();
//        Mockito.doReturn(NOT_SELECTED).when(this)
        theManager.init();
        
        // Set up expected results
        List<Character> charactersExpected = new ArrayList<>();
        charactersExpected.add(Character.createCharacter(FEYEN_AEN, FEYEN_ID, Primal.EXCALIBUR));
        charactersExpected.add(Character.createCharacter(ESPRA_LAROB, ESPRA_ID, Primal.EXCALIBUR));
        
        List<Character> charactersResult = theManager.getCharacters();
        
        boolean resultsMatch = false;
        
        if (charactersResult.size() != 2) {
            resultsMatch = false;
        } else {
            for (int i = 0; i < 2; i++) {
                Character characterExpected = charactersExpected.get(i);
                Character characterResult = charactersResult.get(i);
                
                if (!characterExpected.equals(characterResult)) {
                    resultsMatch = false;
                }
            }
        }
        
        new Expectations() {
        {
//            theManager.loadConfig();
//            times = 1;
        }
    };
        
        assertTrue(resultsMatch);
    }
    
    /**
     * 
     */
    @Test
    public void test_dataNotExists() {
        
        assertFalse("Need to implement this test.", true);
    }
}
