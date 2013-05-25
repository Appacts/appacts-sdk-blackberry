
package AppActs.Plugin.Interfaces;

import javax.bluetooth.UUID;

import AppActs.Plugin.Models.ApplicationMeta;
import AppActs.Plugin.Models.DeviceLocation;
import AppActs.Plugin.Models.User;

public interface ISettings {
    
    ApplicationMeta GetApplicationState(UUID applicationId);
    
    UUID GetDeviceId();
    
    DeviceLocation GetDeviceLocation();
    
    User GetUser(UUID applicationId);
    
    void Save(UUID applicationId, ApplicationMeta applicationState);
    
    void Save(User user);
    
    void Save(DeviceLocation deviceLocation);
    
    void SaveDeviceId(UUID deviceId);
} 
