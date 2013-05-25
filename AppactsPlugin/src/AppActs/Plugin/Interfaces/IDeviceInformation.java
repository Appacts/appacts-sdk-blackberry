

package AppActs.Plugin.Interfaces;

import AppActs.Plugin.Models.DeviceType;

public interface IDeviceInformation {
    String GetDeviceId();
    DeviceType GetDeviceType();
    long GetFlashDriveSize();
    long GetMemorySize();
    String GetModel();
} 
