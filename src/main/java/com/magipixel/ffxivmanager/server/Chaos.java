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
public enum Chaos implements ServerTypeInterface {
    CERBERUS("Cerberus"),
    LICH("Lich"),
    MOOGLE("Moogle"),
    ODIN("Odin"),
    PHOENIX("Phoenix"),
    RAGNAROK("Ragnarok"),
    SHIVA("Shiva"),
    ZODIARK("Zodiark");

    private final String name;

    private Chaos(String serverName) {
    this.name = serverName;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
