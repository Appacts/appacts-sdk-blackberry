/*
 * DeviceDynamicInformation.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Device;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Memory;

import javax.microedition.location.LocationProvider;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.QualifiedCoordinates;
import javax.microedition.location.LocationException;
import javax.microedition.location.AddressInfo;

import AppActs.Plugin.Device.Interfaces.IDeviceDynamicInformation;
import AppActs.Plugin.Handlers.Utils;
import AppActs.Plugin.Models.DeviceGeneralInformation;
import AppActs.Plugin.Models.DeviceLocation;

public final class DeviceDynamicInformation
    implements IDeviceDynamicInformation{
        
    public DeviceDynamicInformation() {    
    
    }
    
    public DeviceGeneralInformation GetDeviceGeneralInformation() {
        DeviceGeneralInformation deviceGeneralInformation =
            new DeviceGeneralInformation
            (
                DeviceInfo.getTotalFlashSize(), 
                Memory.getRAMStats().getFree(), 
                DeviceInfo.getBatteryLevel(), 
                RadioInfo.getSignalLevel()
            );
            
        return deviceGeneralInformation;
        
    }
    
    public DeviceLocation GetDeviceLocation() throws LocationException, InterruptedException {
        
        DeviceLocation deviceLocation = null;
        
        Criteria criteria = new Criteria();
        criteria.setHorizontalAccuracy(50);
        criteria.setVerticalAccuracy(50);
        criteria.setCostAllowed(true);
        criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW);
          
        try {
            LocationProvider locationProvider = LocationProvider.getInstance(criteria);
              
            Location location = locationProvider.getLocation(30);
            
            QualifiedCoordinates qualifiedCoordinates = location.getQualifiedCoordinates();
            
            AddressInfo addressInfo = location.getAddressInfo();
            
            if(addressInfo != null) {
                deviceLocation = new DeviceLocation
                    (
                        qualifiedCoordinates.getLatitude(), 
                        qualifiedCoordinates.getLongitude(),
                        location.getAddressInfo().getField(AddressInfo.COUNTRY),
                        location.getAddressInfo().getField(AddressInfo.COUNTRY_CODE),
                        location.getAddressInfo().getField(AddressInfo.STATE),
                        null,
                        Utils.GetDateTimeNow()
                    );
            } else {
                deviceLocation = new DeviceLocation
                    (
                        qualifiedCoordinates.getLatitude(), 
                        qualifiedCoordinates.getLongitude(),
                        Utils.GetDateTimeNow()
                    );
            }
                
        } catch (LocationException locationException) {
            throw locationException;
        } catch(InterruptedException interruptedException) {
            throw interruptedException;
        }
        
        return deviceLocation;
    }
    
} 
