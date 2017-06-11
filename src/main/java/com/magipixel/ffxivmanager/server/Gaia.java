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
public enum Gaia implements ServerTypeInterface {
    ALEXANDER("Alexander"),
    BAHAMUT("Bahamut"),
    DURANDAL("Durandal"),
    FENRIR("Fenrir"),
    IFRIT("Ifrit"),
    RIDILL("Ridill"),
    TIAMAT("Tiamat"),
    ULTIMA("Ultima"),
    VALEFOR("Valefor"),
    YOJIMBO("Yojimbo"),
    ZEROMUS("Zeromus");

    private final String name;

    private Gaia(String serverName) {
    this.name = serverName;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
