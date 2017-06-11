/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magipixel.ffxivmanager.server;

/**
 *
 * @author Alan
 */
public enum Aether implements ServerTypeInterface {
    ADAMANTOISE("Adamantoise"),
    BALMUNG("Balmung"),
    CACTUAR("Cactuar"),
    COEURL("Coeurl"),
    FAERIE("Faerie"),
    GILGAMESH("Gilgamesh"),
    GOBLIN("Goblin"),
    JENOVA("Jenova"),
    MATEUS("Mateus"),
    MIDGARDSORMR("Midgardsormr"),
    SARGATANAS("Sargatanas"),
    SIREN("Siren"),
    ZALERA("Zalera");

    private final String name;

    private Aether(String serverName) {
    this.name = serverName;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
