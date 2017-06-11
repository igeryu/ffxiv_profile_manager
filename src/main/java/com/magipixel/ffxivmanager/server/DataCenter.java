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
public enum DataCenter implements ServerTypeInterface {
    ELEMENTAL("Elemental"),
    GAIA("Gaia"),
    MANA("Mana"),
    AETHER("Aether"),
    PRIMAL("Primal"),
    CHAOS("Chaos"),
    UNKNOWN("Unknown");
    
    private final String name;
    
    private DataCenter(final String dataCenter) {
        this.name = dataCenter;
    }
    
    public String getName() {
        return name;
    }
    
    public void getDataCenter() {
        
    }
    
    public static ServerTypeInterface[] getServers(String dataCenterName) {
        dataCenterName = dataCenterName.toUpperCase();
        DataCenter dataCenter = DataCenter.valueOf(dataCenterName);
        switch (dataCenter) {
            case ELEMENTAL:
                return Elemental.values();
            case GAIA:
                return Gaia.values();
            case MANA:
                return Mana.values();
            case AETHER:
                return Aether.values();
            case PRIMAL:
                return Primal.values();
            case CHAOS:
                return Chaos.values();
        }
        return DataCenter.values();
    }
}