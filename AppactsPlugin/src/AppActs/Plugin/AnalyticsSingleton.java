/*
 * AnalyticsSingleton.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin;

import AppActs.Plugin.Interfaces.IAnalytics;

/*
 * Analytics Singleton 
 * Thread Safe
 */
public final class AnalyticsSingleton {
        
    private static IAnalytics iAnalytics;
    
    /*
    * Get Analytics Instance
    * @return iAnalytics new or cached instance
    * @see Integration Guidelines SDK Document
    */
    public static synchronized IAnalytics GetInstance() {
        if(iAnalytics == null) {
        	iAnalytics = new Analytics();
        }
        return iAnalytics;
    }
      
}

