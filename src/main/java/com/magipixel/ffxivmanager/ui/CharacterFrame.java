/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magipixel.ffxivmanager.ui;

import com.magipixel.ffxivmanager.util.Character;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import static javax.swing.SwingConstants.CENTER;

/**
 *
 * @author Alan
 */
public class CharacterFrame extends JFrame {

    private JScrollPane pane;
//    private JList charactersList;
    private ArrayList<Character> characters;
    private JButton selectButton;
    private JButton cancelButton;
    private Character character;
    private String activeCharacterName;

    ArrayList<JRadioButton> characterRadioButtons;
    ButtonGroup characterRadioGroup;

    /**
     * 
     * @param cList 
     */
    public CharacterFrame(ArrayList<Character> cList) {
        super();
        characters = cList;
        setTitle("Identify Active Character");
        setActiveCharacterName("");
        setLocationRelativeTo(null);
        characterRadioButtons = new ArrayList<>();
        characterRadioGroup = new ButtonGroup();

        pane = new JScrollPane(buildCharacterPanel());
        selectButton = new JButton("Select");
        cancelButton = new JButton("Cancel");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String characterName = characterRadioGroup.getSelection().getActionCommand();
                if (characterName != null) {
                    setName(characterName);
                    setVisible(false);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
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
            characterRadioGroup.add(newRadioButton);
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

    public static CharacterFrame createFrame(ArrayList<Character> cList) {
        return new CharacterFrame(cList);
    }
    
    /**
     * @return the activeCharacterName
     */
    public String getActiveCharacterName() {
        return activeCharacterName;
    }

    /**
     * @param activeCharacterName the activeCharacterName to set
     */
    public void setActiveCharacterName(String activeCharacterName) {
        this.activeCharacterName = activeCharacterName;
    }

}
