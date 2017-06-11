/**
 * changelog:
 */
package com.magipixel.ffxivmanager.util;

import com.magipixel.ffxivmanager.components.Profile;
import com.magipixel.ffxivmanager.server.ServerTypeInterface;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Alan
 */
public class Character {
    private ArrayList <Profile> profiles;
    private Profile activeProfile;
    private String name;
    private String id;
    
    private final ServerTypeInterface server;
    
    /**
     * Indicates whether this character's ID has been determined.
     */
    private boolean identified;
    
    /**
     * Indicates whether this character's name has been found.
     */
    private boolean named;
    
    private ProfilesFrame profilesFrame;
    private CharacterPanel characterPanel;
    
    /**
     * Creates a new character using the given ID (determined externally by
     * FFXIV).
     * 
     * @param _id
     *     ID of the character, given by FFXIV
     */
    private Character (String _id, ServerTypeInterface server) {
        this(null, _id, server);
    }
    
    
    
    private Character (String _name, String _id, ServerTypeInterface server) {
        name = _name;
        if (name == null || name.contains("@Unknown_")) {
            named = false;
        } else {
            named = true;
        }
        id = _id;
        identified = true;
        this.server = server;
        profilesFrame = new ProfilesFrame();
        profiles = new ArrayList<>();
    }
    
//    public boolean equals(Character other) {
//        return id.equals(other.getId())
//    }
    
    
    
    public void addProfile (Profile p) {
        profiles.add(p);
    }
    
    protected boolean equals(Character otherChar) {
        return (id.equals(otherChar.getId()) &&
                name.equals(otherChar.getName()));
    }
    
    /**
     * 
     * @return     isIdentified variable
     */
    public boolean isIdentified() {
        return identified;
    }
    
    
    
    /**
     * 
     * @param i    new value for <code>isIdentified</code>
     */
    public void setIdentified(boolean i) {
        identified = i;
    }
    
    /**
     * 
     * @return 
     */
    public String getName() {
        return name;
    }
    
    
    
    public ArrayList<Profile> getProfiles() {
        return profiles;
    }
    
    
    
    /**
     * 
     * @param n 
     */
    public void setName(String n) {
        name = n;
    }
    
    
    
    /**
     * 
     * @return 
     */
    public Profile getActiveProfile() {
        return activeProfile;
    }
    
    public static Character createCharacter(String _id, ServerTypeInterface server) {
        return new Character(_id, server);
    }
    
    public static Character createCharacter(String _name, String _id,
                                            ServerTypeInterface server) {
        return new Character(_name, _id, server);
    }
    
    
    public CharacterPanel getCharacterPanel() {
        if (characterPanel == null)
            characterPanel = new CharacterPanel(this);
        
        return characterPanel;
    }  //  end method getCharacterPanel()
    
    
    
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
 
 Scans own folder (whose name is isIdentified by <code>id</code>) for
     * backups.
     * 
     * @return   success of backup scanning
     */
    public boolean scanBackups() {
        return true;
    }

    /**
     * @return the named
     */
    public boolean isNamed() {
        return named;
    }

    /**
     * @param named the named to set
     */
    public void setNamed(boolean named) {
        this.named = named;
    }

    /**
     * @return the server
     */
    public ServerTypeInterface getServer() {
        return server;
    }
    
    
    
    
    private class CharacterPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        Character owner;
        
        /**
         *
         * @param o
         */
        public CharacterPanel(Character o) {
            super();
            if (o != null) {
                owner = o;
            }
            FlowLayout layout = new FlowLayout();
            setSize(100, 20);

            setLayout(layout);
            JLabel nameLabel = new JLabel(o.name);
            nameLabel.getInsets().set(1, 1, 1, 1);
            nameLabel.setPreferredSize(new Dimension(80, 15));
            add(nameLabel);

            
        }  //  end CharacterPanel constructor

        /**
         *
         * @param st
         */
        
    }  //  end class CharacterPanel
    
    
    
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

            int width = 300; int height = 200;
            setMinimumSize(new Dimension(width, height));
            setVisible(false);

        }  //  end GameFrame constructor
        
        
    }  //  end class ProfilesFrame
    
}  //  end class Character
