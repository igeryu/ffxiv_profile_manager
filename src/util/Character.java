/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import components.Profile;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

/**
 *
 * @author Alan
 */
public class Character {
    private HashMap <String, ArrayList<Backup>> profileBackups;
    private Profile activeProfile;
    private String name;
    private String id;
    
    public Character (String _id) {
        name = null;
        id = _id;
    }
    
    /**
     * 
     * @return 
     */
    public String getName() {
        return name;
    }
    
    
    
    /**
     * 
     * @return 
     */
    public Profile getActiveProfile() {
        return activeProfile;
    }
    
    
    
    /**
     * 
     * @return 
     */
    public String getId() {
        return id;
    }
    
    
    
    /**
     * TODO:  Fill this in.
     * 
     * Loads profile by asking user to select one of the profile backups of held
     * by this character object.
     * 
     * @return     Latest profile backup of the profile selected by the user.
     */
    public Profile loadProfile () {
        
        //  TODO:  Ask user to select the profile they want to load
        
        
        /**
         *   TODO:  Ask the user if they want the latest backup of that profile,
         *          or an older one.
         */
        
        /**
         * TODO:  Load the profile selected by the user into memory and
         *        overwrite the active profile folder files with those in the
         *        backup folder.
        */
        
        return null;
    }
    
    
    
    /**
     * TODO:  Build this!
     * 
     * Scans own folder (whose name is identified by <code>id</code>) for
     * backups.
     * 
     * @return   success of backup scanning
     */
    public boolean scanBackups() {
        return true;
    }
    
    
    
    private class ProfilesFrame extends JFrame {
        private static final long serialVersionUID = 1L;
        
        public ProfilesFrame() {

            this.setTitle(name);

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
            

        }  //  end GameFrame constructor
        
        
    }  //  end class ProfilesFrame
    
}  //  end class Character
