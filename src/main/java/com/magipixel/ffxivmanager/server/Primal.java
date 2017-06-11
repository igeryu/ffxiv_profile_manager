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
public enum Primal implements ServerTypeInterface {
    BEHEMOTH("Behemoth"),
    BRYNHILDR("Brynhildr"),
    DIABOLOS("Diabolos"),
    EXCALIBUR("Excalibur"),
    EXODUS("Exodus"),
    FAMFRIT("Famfrit"),
    HYPERION("Hyperion"),
    LAMIA("Lamia"),
    LEVIATHAN("Leviathan"),
    MALBORO("Malboro"),
    ULTROS("Ultros"),;

    private final String name;

    private Primal(String serverName) {
    this.name = serverName;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
