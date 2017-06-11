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
public enum Mana implements ServerTypeInterface {
    ANIMA("Anima"),
    ASURA("Asura"),
    BELIAS("Belias"),
    CHOCOBO("Chocobo"),
    HADES("Hades"),
    IXION("Ixion"),
    MANDRAGORA("Mandragora"),
    MASAMUNE("Masamune"),
    PANDAEMONIUM("Pandaemonium"),
    SHINRYU("Shinryu"),
    TITAN("Titan");

    private final String name;

    private Mana(String serverName) {
    this.name = serverName;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
