package SampleApplicationBasic;

import AppActs.Plugin.AnalyticsSingleton;
import net.rim.device.api.ui.UiApplication;

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class Main extends UiApplication
{
    /**
     * Entry point for application
     * @param args Command line arguments (not used)
     */ 
    public static void main(String[] args)
    {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        Main theApp = new Main();       
        theApp.enterEventDispatcher();
    }
    
    /**
     * Creates a new MyApp object
     */
    public Main()
    {        
    	/*
    	 * Appacts
    	 * Application is starting lets start the session
    	 */
    	try {
			AnalyticsSingleton.GetInstance().Start("http://api-dev.appacts.com/", "84ddec93198a449c9069fa842536d25c");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        // Push a screen onto the UI stack for rendering.
        pushScreen(new ScreenBasic());
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
}
