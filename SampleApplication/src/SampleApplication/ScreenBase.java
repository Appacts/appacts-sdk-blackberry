/*
 * ScreenBase.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package SampleApplication;

import net.rim.device.api.ui.VirtualKeyboard;
import net.rim.device.api.ui.container.MainScreen;
import AppActs.Plugin.AnalyticsSingleton;

public abstract class ScreenBase extends MainScreen  {

    public final String ScreenName;
    
    /*
     * Base Constructor
     */
    public ScreenBase(String screenName) {
        this.ScreenName = screenName;
        AnalyticsSingleton.GetInstance().ScreenOpen(this.ScreenName);
    }
    
    /*
     * @see net.rim.device.api.ui.Screen#onExposed()
     */
    protected void onExposed() {
    	AnalyticsSingleton.GetInstance().ScreenOpen(this.ScreenName);
    	super.onExposed();
    }
    
    protected void onObscured() {
    	super.onObscured();
    }
    
    public void onUiEngineAttached(boolean attached) {
    	super.onUiEngineAttached(attached);
    }
    
    /*
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() {
    	AnalyticsSingleton.GetInstance().ScreenClosed(this.ScreenName);
        
    	
		/*
		 * Appacts
		 * Check that there are no screens at the top, below and there is no virtual keyboard
		 * if there are no more screens, application is closing, stop the session tracking
		 */   
        if(VirtualKeyboard.isSupported()) {
        	this.getVirtualKeyboard().setVisibility(VirtualKeyboard.HIDE_FORCE);
        }
        if(this.getScreenBelow() == null && this.getScreenAbove() == null) {
        	
        	AnalyticsSingleton.GetInstance().Stop();
        	
        }
        
        
        
        super.close();
    }
    
    
} 
