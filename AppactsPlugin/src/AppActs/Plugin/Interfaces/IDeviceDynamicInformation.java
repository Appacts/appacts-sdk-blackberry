

package AppActs.Plugin.Interfaces;

import AppActs.Plugin.Models.DeviceGeneralInformation;
import AppActs.Plugin.Models.DeviceLocation;

public interface IDeviceDynamicInformation {
    DeviceGeneralInformation GetDeviceGeneralInformation();
    DeviceLocation GetDeviceLocation();
} 
