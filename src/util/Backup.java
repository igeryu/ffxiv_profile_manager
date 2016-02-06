/*
 * changelog:
2016-02-05 : Added name field
2016-02-05 : Added getName() method.
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
    private String name;
    
    public Backup (Date _date, Profile _profile) {
        date = _date;
        profile = _profile;
    }
    
    
    
    /**
     * 
     * @return 
     */
    public String getName() {
        return name;
    }
    
}
