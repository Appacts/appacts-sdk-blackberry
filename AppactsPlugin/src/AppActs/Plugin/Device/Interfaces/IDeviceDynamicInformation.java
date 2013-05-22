/*
 * IDeviceDynamicInformation.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Device.Interfaces;

import AppActs.Plugin.Models.DeviceGeneralInformation;
import AppActs.Plugin.Models.DeviceLocation;

import javax.microedition.location.LocationException;

public interface IDeviceDynamicInformation {
    DeviceGeneralInformation GetDeviceGeneralInformation();
    DeviceLocation GetDeviceLocation() throws LocationException, InterruptedException;
} 
