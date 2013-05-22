/*
 * DeviceInformation.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Device;

import AppActs.Plugin.Device.Interfaces.IDeviceInformation;
import AppActs.Plugin.Models.DeviceType;
import AppActs.Plugin.Models.ScreenResolution;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.Memory;
import net.rim.device.api.system.MemoryStats;

public final class DeviceInformation implements IDeviceInformation {
        
    private final static String pluginVersion = "0.9.990.2929";
        
    public DeviceInformation() {    
    
    }
    
    
    public int GetDeviceType() {
        return DeviceType.Blackberry;
    }
    
    public long GetFlashDriveSize() {
        return DeviceInfo.getTotalFlashSize();
    }
    
    public String GetModel() {
        return DeviceInfo.getDeviceName();
    }
    
    public ScreenResolution GetScreenResolution() {
    	return new ScreenResolution(Display.getWidth(), Display.getHeight());
    }
    
    public String GetLocale() {
    	return Locale.getDefaultForSystem().toString();
    }
    
    public String GetManufacturer() {
    	return DeviceInfo.getManufacturerName();
    }
    
    public long GetMemorySize() {
        MemoryStats memoryStats = Memory.getRAMStats();
        return memoryStats.getAllocated() + memoryStats.getFree();
    }
    
    public String GetPluginVersion() {
        return pluginVersion;
    }
} 
