package com.magipixel.ffxivmanager;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import static org.junit.Assert.*;
//import jdk.nashorn.internal.parser.JSONParser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alan
 */
public class JSONTest {
    
    @SuppressWarnings("unchecked")
    public static void main (String[] args) {
        
        writeJSON();
        
    }
    
//    @Test
    public static void writeJSON () {
        
        System.out.println("Writing JSON file...");
        
        JSONObject obj = new JSONObject();
        obj.put("Active Character", "Feyen Aen");
        obj.put("Active Profile", "MyProfile");

        JSONObject characters = new JSONObject();
        JSONObject feyenAen = new JSONObject();
        feyenAen.put("ID", "FFXIV_CHR00400000009D4722");
        JSONArray myProfile = new JSONArray();
        myProfile.add("Backup1 Timestamp");
	myProfile.add("Backup2 Timestamp");
        feyenAen.put("MyProfile", myProfile);
        
        characters.put("Feyen Aen", feyenAen);
        obj.put("Characters", characters);

        // try-with-resources statement based on post comment below :)
        try (FileWriter file = new FileWriter("jsontest.txt")) {
            file.write(obj.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + obj);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getLocalizedMessage());
        }
        
    }
        
//    @Test
    public static void readJSON () {
        
        System.out.println("Reading JSON file...");
        
        JSONParser parser = new JSONParser();
        
        try {
 
            Object obj = parser.parse(new FileReader(
                    "jsontest.txt"));
 
            JSONObject jsonObject = (JSONObject) obj;
 
//            String name = (String) jsonObject.get("Name");
//            String author = (String) jsonObject.get("Author");
//            JSONArray charactersList = (JSONArray) jsonObject.get("Company List");
            
            String activeProfile = (String) jsonObject.get("Active Profile");
            String activeCharacter = (String) jsonObject.get("Active Character");
            JSONObject characters = (JSONObject) jsonObject.get("Characters");
            JSONObject feyenAen = (JSONObject) characters.get("Feyen Aen");
            String feyenAenId = (String) feyenAen.get("ID");
            JSONArray feyenAenProfiles = (JSONArray) feyenAen.get("MyProfile");
 
//            System.out.println("Name: " + name);
//            System.out.println("Author: " + author);
//            System.out.println("\nCompany List:");
//            Iterator<String> iterator = charactersList.iterator();
//            while (iterator.hasNext()) {
//                System.out.println(iterator.next());
//            }
            
            String indent = "";
            
            System.out.println("Active Profile: " + activeProfile);
            System.out.println("Active Character: " + activeProfile);
            System.out.println("\nCharacters:");
            
            System.out.println("    Feyen Aen:");
            System.out.println("        ID: " + feyenAenId);
            System.out.println("        MyProfile:");
            Iterator<String> iterator = feyenAenProfiles.iterator();
            while (iterator.hasNext()) {
                System.out.println("            " + iterator.next());
            }
 
        } catch (Exception e) {
            e.printStackTrace();
    }
    }
    
}
