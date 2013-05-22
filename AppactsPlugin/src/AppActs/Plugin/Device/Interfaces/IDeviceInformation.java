/*
 * IDeviceInformation.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Device.Interfaces;

import AppActs.Plugin.Models.ScreenResolution;

public interface IDeviceInformation {
    int GetDeviceType();
    long GetFlashDriveSize();
    long GetMemorySize();
    String GetModel();
    String GetPluginVersion();
    String GetManufacturer();
    String GetLocale();
    ScreenResolution GetScreenResolution();
} 
