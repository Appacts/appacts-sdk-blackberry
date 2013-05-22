/*
 * Platform.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Device;

import AppActs.Plugin.Device.Interfaces.IPlatform;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.DeviceInfo;

public final class Platform implements IPlatform {
    
    public Platform() {
    
    }
    
    public String GetCarrier() {
        return RadioInfo.getCurrentNetworkName();
    }
    
    public String GetOS() {
         return DeviceInfo.getPlatformVersion();
    }
    
} 
