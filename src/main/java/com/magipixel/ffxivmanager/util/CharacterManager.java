/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magipixel.ffxivmanager.util;

import static com.magipixel.ffxivmanager.ProfileManagerApplication.getFFXIV_FOLDER;
import static com.magipixel.ffxivmanager.ProfileManagerApplication.getFOLDER_DIVIDER;
import com.magipixel.ui.modal.AlertDialog;
import com.magipixel.ui.modal.AnswerDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import org.apache.commons.lang3.text.WordUtils;

/**
 *
 * @author Alan
 */
public class CharacterManager {

    private static final Logger logger
            = Logger.getLogger(CharacterManager.class.getSimpleName());
    private static final String REGEX_CHAR_NAME =
            "[A-Za-z][a-z'-]{2,15} [A-Za-z][a-z'-]{2,15}";
    private List<Character> characters;

    private static final int NAME_MAX_CHARS = 20;
    private static final int NAME_MIN_CHARS_EACH = 2;
    private static final int NAME_MAX_CHARS_EACH = 15;
    
    private static final String NAME_HYPHENS_MSG =
            "Hypens cannot be used in succession or placed immediately before "
            + "or after apostrophes.";
    
    private static final String NAME_LENGTH_MSG =
            "Both forename and surname must be between " + NAME_MIN_CHARS_EACH
            + " and " + NAME_MAX_CHARS_EACH + " characters and not total more "
            + "than " + NAME_MAX_CHARS + " characters combined.";
    
    private static final String NAME_REGEX_MSG
            = "Only letters, hyphens, and apostrophes can be used. The first "
            + "letter of either name must be a letter.";
    
    private static final String NAME_RULES_MSG =
            NAME_LENGTH_MSG + "  " +
            NAME_REGEX_MSG + "  " +
            NAME_HYPHENS_MSG;
    
    /**
     *
     */
    public CharacterManager(List<Character> characters) {
        this.characters = characters;
    }

    /**
     * <p>
     * Loops every time <code>getNumUnidentifiedCharacters</code> returns a
     * value greater than 0. With each iteration,
     * <code>identifyCharacter()</code> is called.</p>
     */
    protected void identifyAllCharacters() {

        //  DEBUG:
        logger.info("Verifying all characters are identified...");

        while (BackupManager.getNumUnidentifiedCharacters(characters) > 0) {

            identifyCharacter();

        }  //  if there is at least one unverified character
    }  // end method identifyAllCharacters()

    /**
     * <p>
     * Lists unidentified characters, and asks the user to give the ID of a yet
     * to be unidentified character.</p>
     *
     * <p>
     * Then searches the folder of each <code>Character</code> object whose
     * <code>isIdentified</code> flag is <code>false</code>, until one is
     * isIdentified as being correct, none are found (error).</p>
     */
    protected void identifyCharacter() {

        logger.info("\nIdentifying Character...");

        String identifiedCharacterNames = "Identified Characters:\n";
        String unidentifiedCharacterNames = "\n\nUndentified Characters:\n";

        for (Character character : characters) {
            if (character.isIdentified()) {
                identifiedCharacterNames += character.getName() + "\n";
            } else {
                unidentifiedCharacterNames += character.getName() + "\n";
            }
        }  // for each character in characters
        
        //  TODO:  identifyCharacter() : need to ensure player enters character full name
        String nameInput = AnswerDialog.display("Identify New Character",
                identifiedCharacterNames + unidentifiedCharacterNames);

        identifyCharacterFolder(nameInput);

    }  //  end method identifyCharacter()

    /**
     * Attempts to find the folder for the character name given in the
     * parameter.
     *
     * @param name
     * @return
     */
    protected boolean identifyCharacterFolder(String name) {

        System.out.printf("\nLocating character: \"%s\"\n", name);

        /**
         * 1. Verify that the character is not already identified. Return false
         * if the character is already verified.
         *
         * NOTE: Currently commented out because I have two characters with the
         * same name on different servers. TODO: Figure out how to make this
         * work for two like-named characters.
         */
//        for (Character character : characters) {
//            if (character.getName() != null && character.getName().equals(name)) {
//                System.out.println("\nCharacter " + name + " already isIdentified...");
//                return false;
//            }
//        }
        /**
         * 2. Locate folder by character name. If successful, create a new
         * Character object, call identifyProfiles() on it and add it to
         * characters ArrayList, then return true.
         *
         * TODO: Add support for multiple characters of same name, but different
         * servers. TODO: Situation where a list of unidentified characters
         * would be more efficient:
         */
        for (Character character : characters) {
            if (!character.isIdentified()) {
                return searchCharacterFolder(character, name);
            }  //  if the character is not isIdentified
        }  // for each item in characters

        return false;
    }  // end method identifyCharacterFolder()

    /**
     * <p>
     * Loops every time <code>getNumUnidentifiedCharacters</code> returns a
     * value greater than 0. With each iteration,
     * <code>identifyCharacter()</code> is called.</p>
     */
    protected void nameAllCharacters() {

        //  DEBUG:
        logger.info("Verifying all characters are nameed...");

        while (BackupManager.getNumUnnamedCharacters(characters) > 0) {

            nameCharacter();

        }  //  if there is at least one unverified character
    }  // end method identifyAllCharacters()

    /**
     * <p>
     * Searches unidentified character folders for the string provided in
     * <code>name</code>. Once found:
     * <ol>
     * <li> The <code>Character</code> object who owns that folder has its name
     * set to <code>name</code></li>
     * <li> That same <code>Character</code> object's <code>isIdentified</code>
     * flag is set to <code>true</code></li>
     * <li> The <code>Character</code> object is returned.</li>
     * </ol></p>
     *
     * @param character     <code>Character</code> object, whose folder is to be
     * searched.
     * @param name Name given by user, to check against character folder.
     *
     * @return              <code>true</code> if user-given name was found in the folder,
     * <code>false</code> otherwise.
     */
    protected boolean searchCharacterFolder(Character character, String name) {
        
        String characterLogDirectoryString
                = getFFXIV_FOLDER() + getFOLDER_DIVIDER() + character.getId()
                + getFOLDER_DIVIDER() + "log" + getFOLDER_DIVIDER();
        File characterLogDirectory = new File(characterLogDirectoryString);

        File[] logFiles = characterLogDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("log");
            }
        });

        if (logFiles != null && logFiles.length > 0) {

            for (File file : logFiles) {

                try {

                    byte[] encoded
                            = Files.readAllBytes(Paths
                                    .get(file.getAbsolutePath()));
                    String fileContents
                            = new String(encoded, StandardCharsets.US_ASCII);

                    if (Pattern.compile(
                            Pattern.quote(name),
                            Pattern.CASE_INSENSITIVE)
                            .matcher(fileContents).find()) {
                        System.out.println("Log file found.  "
                                + "Identified character " + name + ".");
                        character.setName(name);
                        character.setNamed(true);
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
     * <p>
     * Lists isIdentified characters, and asks the user to give a name of a yet
     * to be isIdentified character.</p>
     *
     * <p>
     * Then searches the folder of each <code>Character</code> object whose
     * <code>isIdentified</code> flag is <code>false</code>, until one is
     * isIdentified as being correct, none are found (error).</p>
     */
    private void nameCharacter() {
        logger.info("\nNaming Character...");

        String namedCharacterIds = "Named Characters:\n";
        String unnamedCharacterIds = "\n\nUnnamed Characters:\n";
        boolean noNamedCharacterFound = true;
        boolean noUnnamedCharacterFound = true;

        for (Character character : characters) {
            if (character.isNamed()) {
                namedCharacterIds += character.getName() + "\n";
                noNamedCharacterFound = false;
            } else {
                unnamedCharacterIds += character.getId() + "\n";
                noUnnamedCharacterFound = false;
            }
        }  // for each character in characters
        
        if (noNamedCharacterFound) {
            namedCharacterIds += "[None Found]";
        }
        
        if (noUnnamedCharacterFound) {
            unnamedCharacterIds += "[None Found]";
        }

        //  TODO:  nameCharacter() : need to ensure player enters character full name
        String nameInput = null;
        String tempName = null;
        final String dialogTitle = "Name New Character";
        final String errorTitle = "Name Invalid";
        do {
            TextInputDialog dialog = new TextInputDialog(tempName);
            dialog.setTitle(dialogTitle);
            dialog.setContentText(namedCharacterIds + unnamedCharacterIds);
            Optional<String> result = dialog.showAndWait();
            if(result.isPresent()) {
                tempName = result.get();
            }
//            tempName = AnswerDialog.display("Name New Character", namedCharacterIds
//                    + unnamedCharacterIds);
            
            if (tempName != null && tempName.length() > 0) {
                tempName.trim();
                
                if (!tempName.contains("--")
                        && !tempName.contains("-'")
                        && !tempName.contains("'-")) {
                    if (tempName.length() <= NAME_MAX_CHARS) {
                        // Ensure that no non-beginning letters are capitalized
                        WordUtils.uncapitalize(tempName);
                        // Capitalize first and last name 
                        WordUtils.capitalize(tempName);
                        if (tempName.matches(REGEX_CHAR_NAME)) {
                            logger.info("Name '" + tempName + "' matches regex.");
                            nameInput = tempName;
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "The name you have entered is not valid:"
                                  + "\n\n" + tempName + "\n\n"
                                  + NAME_RULES_MSG);
                            alert.setTitle(errorTitle);
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "The name you have "
                                + "entered is too long:\n\n" + tempName
                                + "\n\n" + NAME_LENGTH_MSG);
                        alert.setTitle(errorTitle);
                        alert.showAndWait();

//                            AlertDialog.display(errorTitle, "The name you have "
//                                    + "entered is too long:\n\n" + tempName
//                                    + "\n\n" + NAME_LENGTH_MESSAGE);
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "The name you have entered contains "
                            + "invalid use of apostrophes (') and/or "
                            + "hyphens (-):\n\n" + tempName + "\n\n"
                            + NAME_HYPHENS_MSG);
                    alert.setTitle(errorTitle);
                    alert.showAndWait();
            
//                        AlertDialog.display(errorTitle, "The name you have "
//                                + "entered contains invalid use of apostrophes "
//                                + "(') and/or hyphens (-):\n\n" + tempName
//                                + "\n\n" + MISUSED_SYMBOLS_MESSAGE);
                }
                            
//                    AlertDialog.display(errorTitle, "The name you have "
//                                + "entered is not valid:\n\n" + tempName
//                                + "\n\n" + NAME_ALLOWED_CHARS);
                
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Please enter a name.");
                alert.setTitle(errorTitle);
                alert.showAndWait();
                            
//                    AlertDialog.display(errorTitle, "Please enter a name.");
            }
        } while (nameInput == null);
        
        // TODO - need to sanitize name, then in searchCharacterFolder(), identify whether this is matches the "[A-Z][a-z']* [A-Z][a-z']*"
        

        nameCharacterFolder(nameInput);

    }  //  end method nameCharacter()

    /**
     * Attempts to find the folder for the character name given in the
     * parameter.
     *
     * @param name
     * @return
     */
    protected boolean nameCharacterFolder(String name) {

        logger.info("Locating character: " + name);

        /**
         * 1. Verify that the character is not already identified. Return false
         * if the character is already verified.
         *
         * NOTE: Currently commented out because I have two characters with the
         * same name on different servers. TODO: Figure out how to make this
         * work for two like-named characters.
         */
//        for (Character character : characters) {
//            if (character.getName() != null && character.getName().equals(name)) {
//                System.out.println("\nCharacter " + name + " already isIdentified...");
//                return false;
//            }
//        }
        /**
         * 2. Locate folder by character name. If successful, create a new
         * Character object, call identifyProfiles() on it and add it to
         * characters ArrayList, then return true.
         *
         * TODO: Add support for multiple characters of same name, but different
         * servers. TODO: Situation where a list of unidentified characters
         * would be more efficient:
         */
        for (Character character : characters) {
            if (!character.isNamed()) {
                logger.info("Searching in character, ID: " + character.getId());
                if(searchCharacterFolder(character, name)) {
                    return true;
                }
            }  //  if the character is not isIdentified
        }  // for each item in characters

        return false;
    }  // end method identifyCharacterFolder()
}
