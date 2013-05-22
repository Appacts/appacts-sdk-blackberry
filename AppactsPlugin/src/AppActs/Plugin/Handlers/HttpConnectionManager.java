/*
 * HttpConnectionManager.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Handlers;

import net.rim.device.api.system.*;

public class HttpConnectionManager   
{
    /** this gets appended to the URI before a 3g connection is made */
    public static final String CellularConnectionDirectives =
        DeviceInfo.isSimulator() ? "" : ";deviceside=false;ConnectionType=mds-public";
    
    /** this gets appended to the URI before a WiFi connection is made */
    public static final String WiFiConnectionDirectives =
        DeviceInfo.isSimulator() ? "" : ";deviceside=true;interface=wifi";
    
    public HttpConnectionManager()
    {
    
    }
    
    public static boolean HasNetworkCoverage() 
    {
    // attach listener...
    //  CoverageInfo.addListener(Application.getApplication(), new CoverageStatusListener() {
    //    /**
    //     * Indicates that the coverage status has changed.
    //     * Note that a CoverageInfo.COVERAGE_* flag being set in newCoverage is not a guarantee that a
    //     *  connection of that type will succeed. For example, it is possible for the device to lose coverage
    //     * between the time this notification was fired and a subsequent connection is attempted, or for
    //     * a destination server to be unresponsive.
    //     *
    //     * @param newCoverage - The new coverage status, consisting of a bitwise OR of one or more CoverageInfo.COVERAGE_* flags.
    //     */
    //    public void coverageStatusChanged(int newCoverage) {
    //    }
    //  });
       
        boolean hasNetworkCoverage = false;
        
        if(!DeviceInfo.isSimulator())
        {
            if (!CoverageInfo.isOutOfCoverage()) 
            {
                //wifi enabled
                if(WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED)
                {
                    hasNetworkCoverage = true;
                } 
                else
                {
                    int coverageStatus = CoverageInfo.getCoverageStatus();
                    
                    if(coverageStatus == CoverageInfo.COVERAGE_NONE)
                    {
                        hasNetworkCoverage = false;
                    }
                    else if(coverageStatus == (CoverageInfo.COVERAGE_BIS_B +  CoverageInfo.COVERAGE_DIRECT))
                    {
                        hasNetworkCoverage = true;
                    }
                    else if(coverageStatus == (CoverageInfo.COVERAGE_BIS_B + CoverageInfo.COVERAGE_MDS + CoverageInfo.COVERAGE_DIRECT))
                    {
                        hasNetworkCoverage = true;
                    }
                }
            }
            else 
            {
                hasNetworkCoverage = false;
            }
        }
        else
        {
            hasNetworkCoverage = true;
        }
        
        return hasNetworkCoverage;
    }
    
    public static String GetConnectionStringByNetworkCoverage() {
        
        if(WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED)
        {
            return WiFiConnectionDirectives;
        } 
        
        return CellularConnectionDirectives;
    }
    
} 
