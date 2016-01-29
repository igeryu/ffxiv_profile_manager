/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import components.Profile;
import java.util.Date;

/**
 *
 * @author Alan
 */
public class Backup {
    private Date date;
    private Profile profile;
    
    public Backup (Date _date, Profile _profile) {
        date = _date;
        profile = _profile;
    }
}
