
package AppActs.Plugin.Models;

import java.util.Date;

import javax.bluetooth.UUID;

public final class Crash extends Item {
    
    public Crash(UUID applicationId, UUID sessionId, String version) {
        super(applicationId, sessionId, version);    
    }
    
    public Crash(int id, UUID applicationId, Date dateCreated, UUID sessionId, String version) {
        super(id, applicationId, dateCreated, sessionId, version);
    }
    
} 
