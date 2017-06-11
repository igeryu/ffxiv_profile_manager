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
public enum Elemental implements ServerTypeInterface {
    AEGIS("Aegis"),
    ATOMOS("Atomos"),
    CARBUNCLE("Carbuncle"),
    GARUDA("Garuda"),
    GUNGNIR("Gungnir"),
    KUJATA("Kujata"),
    RAMUH("Ramuh"),
    TONBERRY("Tonberry"),
    TYPHON("Typhon"),
    UNICORN("Unicorn");

    private final String name;

    private Elemental(String serverName) {
    this.name = serverName;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
