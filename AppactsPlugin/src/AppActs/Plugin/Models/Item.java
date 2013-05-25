
package AppActs.Plugin.Models;

import AppActs.Plugin.Handlers.*;
import java.util.Date;

import javax.bluetooth.UUID;

abstract class Item {
    
    public final int Id;
    public final UUID ApplicationId;
    public final Date DateCreated;
    public final String Version;
    public final UUID SessionId;
        
    public Item(UUID applicationId, UUID sessionId, String version) {    
        this.ApplicationId = applicationId;
        this.DateCreated = Utils.GetDateTimeNow();
        this.Id = 0;
        this.SessionId = sessionId;
        this.Version = version;
    }
    
    public Item(int id, UUID applicationId, Date dateCreated, UUID sessionId, String version) {
        this.Id = id;
        this.ApplicationId = applicationId;
        this.DateCreated = dateCreated;
        this.SessionId = sessionId;
        this.Version = version;
    }
    
} 
