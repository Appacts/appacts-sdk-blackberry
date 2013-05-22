/*
 * System.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Models;

public final class AnalyticsSystem {
    
    public final int DeviceType;
    public final String Version;
    
    public AnalyticsSystem(int deviceType, String version) {
        this.DeviceType = deviceType;
        this.Version = version;
    }
} 
