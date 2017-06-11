/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magipixel.ffxivmanager;

import com.magipixel.ffxivmanager.ui.MainStage;
import com.magipixel.ffxivmanager.util.CharacterManager;
import com.magipixel.ffxivmanager.util.ProfileManager;
import java.io.File;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author Alan
 */
public class ProfileManagerApplication extends Application {

    private static final Logger logger
            = Logger.getLogger(ProfileManagerApplication.class.getName());

    private static String FFXIV_FOLDER;

    //  TODO: determine if folderDivider is needed:
    private static final String FOLDER_DIVIDER = File.separator;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            manager = new ProfileManager();

            initialSetup();
            MainStage window = new MainStage();
            if (window.init()) {
                window.display();
            }
        } catch (Exception e) {
            logger.severe("Exception thrown");
        } finally {
            System.exit(0);
        }

    }

    /**
     *
     */
    private void initialSetup() {
        if (System.getProperty("os.name").equals("Mac OS X")) {
            FFXIV_FOLDER = System.getProperty("user.home") + FOLDER_DIVIDER
                    + "documents" + FOLDER_DIVIDER
                    + "Final Fantasy XIV - A Realm Reborn" + FOLDER_DIVIDER;
//            FOLDER_DIVIDER = "/";
        } else {
            FFXIV_FOLDER = System.getProperty("user.home") + FOLDER_DIVIDER
                    + "Documents" + FOLDER_DIVIDER + "My Games" + FOLDER_DIVIDER
                    + "FINAL FANTASY XIV - A Realm Reborn" + FOLDER_DIVIDER;
//            FOLDER_DIVIDER = "\\";
        }

        logger.info("\nRunning on " + System.getProperty("os.name"));

    }
    private ProfileManager manager;

    /**
     * @return the FFXIV_FOLDER
     */
    public static String getFFXIV_FOLDER() {
        return FFXIV_FOLDER;
    }

    /**
     * @return the FOLDER_DIVIDER
     */
    public static String getFOLDER_DIVIDER() {
        return FOLDER_DIVIDER;
    }

}
