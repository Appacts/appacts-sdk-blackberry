/*
 * IDeviceInformation.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Interfaces;

import AppActs.Plugin.Models.DeviceType;

public interface IDeviceInformation {
    String GetDeviceId();
    DeviceType GetDeviceType();
    long GetFlashDriveSize();
    long GetMemorySize();
    String GetModel();
} 
