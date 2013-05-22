/*
 * Main.java
 *
 * © Appacts, 2012
 * Confidential and proprietary.
 */

package SampleApplication;

import AppActs.Plugin.AnalyticsSingleton;
import net.rim.device.api.ui.UiApplication;

public class Main extends UiApplication  {
    
    public Main() {   
    	
    	/*
    	 * Appacts
    	 * Application is starting lets start the session
    	 */
    	try {
			AnalyticsSingleton.GetInstance().Start("http://api-dev.appacts.com/", "84ddec93198a449c9069fa842536d25c");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        this.pushScreen(new ScreenSplash());
    }
    
    /*
     * @see net.rim.device.api.system.Application#deactivate()
     */
    public void activate() {
    	
    	/*
    	 * Appacts
    	 * Application has came back to the foreground, lets start the session
    	 */
    	try {
			AnalyticsSingleton.GetInstance().Start("http://api-dev.appacts.com/", "84ddec93198a449c9069fa842536d25c");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	super.activate();
    }
    
    /*
     * @see net.rim.device.api.system.Application#deactivate()
     */
    public void deactivate() {
    	
    	/*
    	 * Appacts
    	 * Application has gone in to the background, lets stop the session
    	 */
    	AnalyticsSingleton.GetInstance().Stop();

    	super.deactivate();
    }
    
    public static void main(String[] args) {
        new Main().enterEventDispatcher();
    }
} 

