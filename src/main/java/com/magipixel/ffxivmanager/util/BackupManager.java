/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magipixel.ffxivmanager.util;

import com.magipixel.ffxivmanager.components.Profile;
import static com.magipixel.ffxivmanager.ProfileManagerApplication.*;
import com.magipixel.ffxivmanager.server.DataCenter;
import com.magipixel.ffxivmanager.server.Primal;
import com.magipixel.ffxivmanager.server.ServerTypeInterface;
import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Alan
 */
public class BackupManager {
    private static final Logger logger =
            Logger.getLogger(BackupManager.class.getSimpleName());
    
    private static String  activeProfileName;
    private static String activeCharacterName;
    
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
    public static boolean backupActiveProfile (String activeCharacterName,
                                               Profile activeProfile,
                                               String activeCharacterID) {
        
        //  DEBUG:
        System.out.println("\nAttempting to backup active profile...");
        
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        String timestamp = dateFormat.format(cal.getTime());
        
        System.out.println("Timestamp: " + timestamp);
        
        String backupName = timestamp + " [" + activeCharacterName+ "] "
                                       + "[" + activeProfile.getName() + "] "
                                       + "Backup";
        
        //  TODO: backupActiveProfile() ~ Acknowledge user's inclusion/exclusion choices (includ gearsets, exclude keybinds, etc.)
        String characterDirectory
                = getFFXIV_FOLDER() + getFOLDER_DIVIDER() + activeCharacterID;
        File current = new File(characterDirectory);
        File backup= new File(characterDirectory
                              + getFOLDER_DIVIDER() + backupName);
        try {
            IOFileFilter txtSuffixFilter
                    = FileFilterUtils.suffixFileFilter(".DAT");
            IOFileFilter txtFiles
                    = FileFilterUtils.and(FileFileFilter.FILE, txtSuffixFilter);
            
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
        
        String characterDirectory = getFFXIV_FOLDER() + getFOLDER_DIVIDER() + character.getId();
        File current = new File(characterDirectory);
        File backup = new File(characterDirectory + getFOLDER_DIVIDER() + character.getActiveProfile().getLastBackup().getName());
        
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
     * <p>Searches through the FFXIV folder, isIdentified by
 <code>getFFXIV_FOLDER()</code>, and creates a <code>characters</code> object
     * for each folder it finds.</p>
     * 
     * <p>For each character/folder it finds, it will call
     * <code>Character.scanBackups()</code>.
     * 
     * @return    <code>true</code> if at least one folder was found (regardless
            of it being properly isIdentified)
     */
    public static boolean scanBackups (ArrayList<Character> characters) {
        
        System.out.println("\nScanning backups...");
        
        /**
         * Iterates through each folder in getFFXIV_FOLDER() and add a new Character
         * object for each one that matches the "FFXIV_CHR..." pattern.
         */
        
        File directory = new File(getFFXIV_FOLDER());
        File[] characterFolders = directory.listFiles(new FilenameFilter(){
            public boolean accept (File dir, String name) {
                return name.startsWith("FFXIV_CHR");
            }
        });
        
        //  Check if any character folders were found
        if (characterFolders.length > 0) {
            
            System.out.println("\nFolders found: " + characterFolders.length);
            
            for (File folder : characterFolders) {
                final String logPath = folder.getPath() + getFOLDER_DIVIDER()
                        + "log" + getFOLDER_DIVIDER();
                final File logFolder = new File(logPath);
                File[] logFiles = logFolder.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
//                        FileChannel channel = FileChannel.open(dir.toPath(), StandardOpenOption.READ);
                        return name.endsWith(".log");
                    }
                });
                ServerTypeInterface foundServer = DataCenter.UNKNOWN;
                for (File logFile : logFiles) {
                    StringBuilder sb = new StringBuilder();
                    InputStream fin = null;
                    byte[] fileContents = new byte[0];
                    try {
                    fin = new BufferedInputStream( new FileInputStream(logFile));
                    } catch (FileNotFoundException e) {
                        // TODO - exception
                    }
                    if (fin == null) {
                        continue;
                    }
                    int bufLen = 20000 * 1024;
                    byte[] buf = new byte[bufLen];
                    byte[] tmp = null;
                    int len = 0;
                    int currentByte = 0;
                    try {
//                        while ((len = fin.read(buf, 0, bufLen)) != -1) {
//                            // extend array
//                            tmp = new byte[fileContents.length + len];
//                            // copy data
//                            System.arraycopy(fileContents, 0, tmp, 0, fileContents.length);
//                            System.arraycopy(buf, 0, tmp, fileContents.length, len);
//                            fileContents = tmp;
//                            tmp = null;
//                        }
                        while ((currentByte = fin.read()) != -1) {
//                            System.out.println(currentByte);
                            if (java.lang.Character.isLetter(currentByte) ||
                                    java.lang.Character.isDigit(currentByte) ||
                                    java.lang.Character.isSpaceChar(currentByte)
                                    ) {
                                sb.append((char) currentByte);
                            }
                        }
//                        System.out.println("Buffer: " + sb);
                        final String bufferString = sb.toString();
                        for (DataCenter dataCenter : DataCenter.values()) {
                            ServerTypeInterface[] servers =
                                    DataCenter.getServers(dataCenter.getName());
                            for (ServerTypeInterface server : servers) {
                                if (bufferString.contains(
                                        "Welcome to " + server.getName())) {
                                    // Found the server for this character
                                    foundServer = server;
                                    break;
                                }
                            }
                            if (foundServer != DataCenter.UNKNOWN) {
                                break;
                            }
                            System.out.println("");
                        }
                    } catch (IOException e) {
                        // TODO - exception
                    }
                }
                // Create a new character using the id given by the folder name
                String id = folder.getName();
                Character newCharacter = Character.createCharacter(id, foundServer);
                characters.add(newCharacter);
                
                if (!newCharacter.scanBackups()) {
                    return false;
                }
            }
            
        } else {
            return false;
        }
        
        return true;
    }  //  end method scanBackups()
    
    /**
     * <p>TODO:  Likely should rename this so that the return value makes more sense.</p>
     * <p>TODO:  Determine if the <code>found</code> value (and return value) is significant beyond whether it is zero or non-zero.</p>
     * 
     * <p>After <code>scanBackups()</code> has been run at least once, this
     * checks each {@link Character} object in <code>characters</code>
     * for one whose <code>isIdentified()</code> method returns false.  Each time
     * one is found, <code>found</code> is incremented.</p>
     * 
     * <p>After iterating through <code>characters</code>,
     * <code>unidentifiedCharacters</code> is set to <code>true</code> if at least one
     * unidentified character was discovered; <code>false</code> otherwise.</p>
     * 
     * @return     Number of unidentified characters found.
     */
    public static int getNumUnidentifiedCharacters(List<Character> characters) {
        logger.info("Verifying scanned backups...");
        int charsToBeIdentified = 0;
        
        logger.info("Getting names for " + characters.size() + " characters.");
        
        for (Character character : characters) {
            
            if (!character.isIdentified()) {
                
                charsToBeIdentified++;
                
            }  // if the character has no name (hasn't been identified)
        
        }  //  for each item in characters
        
        return charsToBeIdentified;
        
    }  //  end method verifiyScannedBackups()

    /**
     * <p>TODO:  Likely should rename this so that the return value makes more sense.</p>
     * <p>TODO:  Determine if the <code>found</code> value (and return value) is significant beyond whether it is zero or non-zero.</p>
     * 
     * <p>After <code>scanBackups()</code> has been run at least once, this
     * checks each {@link Character} object in <code>characters</code>
     * for one whose {@link Character#isNamed() isNamed()} method returns false.  Each time
     * one is found, <code>charsToBeNamed</code> is incremented.</p>
     * 
     * <p>After iterating through <code>characters</code>,
     * <code>unidentifiedCharacters</code> is set to <code>true</code> if at least one
     * unidentified character was discovered; <code>false</code> otherwise.</p>
     * 
     * @return     Number of unidentified characters found.
     */
    
    static int getNumUnnamedCharacters(List<Character> characters) {
        logger.info("Verifying scanned backups have names...");
        int charsToBeNamed = 0;
        
        logger.info("Getting names for " + characters.size() + " characters.");
        
        for (Character character : characters) {
            
            if (!character.isNamed()) {
                
                charsToBeNamed++;
                
            }  // if the character has no name (hasn't been identified)
        
        }  //  for each item in characters
        
        logger.info("Got names for " + characters.size() + " characters.");
        
        return charsToBeNamed;
        
    }  //  end method verifiyScannedBackups()

    
}

class FileMismatchException extends Exception {

    public FileMismatchException() {
    }
}
