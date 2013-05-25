

package AppActs.Plugin.Data.Interfaces;

import javax.bluetooth.UUID;

import AppActs.Plugin.Models.ApplicationMeta;
import AppActs.Plugin.Models.DeviceLocation;
import AppActs.Plugin.Models.ExceptionDatabaseLayer;
import AppActs.Plugin.Models.PluginMeta;
import AppActs.Plugin.Models.User;

import java.util.Date;

public interface ISettings {
	PluginMeta LoadPlugin() throws ExceptionDatabaseLayer;
	void Save(PluginMeta pluginMeta) throws ExceptionDatabaseLayer;
	void Update(PluginMeta pluginMeta) throws ExceptionDatabaseLayer;	
    ApplicationMeta LoadApplication(UUID applicationId) throws ExceptionDatabaseLayer;
    UUID GetDeviceId() throws ExceptionDatabaseLayer;
    DeviceLocation GetDeviceLocation(int statusType) throws ExceptionDatabaseLayer;
    User GetUser(UUID applicationId, int statusType) throws ExceptionDatabaseLayer;
    void Update(ApplicationMeta applicationState) throws ExceptionDatabaseLayer;
    void Save(ApplicationMeta applicationState) throws ExceptionDatabaseLayer;
    void Save(User user) throws ExceptionDatabaseLayer;
    void Save(DeviceLocation deviceLocation, int statusType) throws ExceptionDatabaseLayer;
    void SaveDeviceId(UUID deviceId, Date dateCreated) throws ExceptionDatabaseLayer;
    void Update(User user, int statusType) throws ExceptionDatabaseLayer;
} 
