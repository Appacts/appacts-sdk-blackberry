/*
 * ScreenSplash.java
 *
 * © Appacts, 2012
 * Confidential and proprietary.
 */

package SampleApplication;

import AppActs.Plugin.AnalyticsSingleton;
import AppActs.Plugin.Models.ExceptionDescriptive;
import AppActs.Plugin.Models.OptStatusType;

import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.UiApplication;

public final class ScreenSplash extends ScreenBase {
    
    public ScreenSplash() {
        /* Screen names should be unique for each screen */
        super("splash");
           
        /* When you are loading content from the web services, or using device to calculate something it might be usefull to see how long it takes
         *  for your content to load / process so you can get benchmarks and improve your process in the future. To do this, you must call contentloading and contentloaded when it
         *  content has loaded */
        AnalyticsSingleton.GetInstance().ContentLoading(ScreenName, "Splash Page");
        
        VerticalFieldManager vfmMain = 
            new VerticalFieldManager(VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.USE_ALL_WIDTH);
            
        LabelField lblTitle = new LabelField("Sample Application", 
            LabelField.FIELD_HCENTER | LabelField.FIELD_VCENTER);
         
        vfmMain.add(lblTitle);
        this.add(vfmMain);

        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Thread thread = new Thread() {
                    public void run() {
                        try
                        {
                            //call your webservice, do some data processing before showing application to the user
                        }
                        catch(Exception ex) {
                        	AnalyticsSingleton.GetInstance().LogError(ScreenName, "Loading splash page", null, new ExceptionDescriptive(ex));
                        }
                       
                        synchronized(UiApplication.getEventLock()) {
                            UiApplication.getUiApplication().invokeLater(new Runnable() {
                                public void run() {
                          
                                	AnalyticsSingleton.GetInstance().ContentLoaded(ScreenName, "Splash Page");
                    
                                    if(AnalyticsSingleton.GetInstance().GetOptStatus() == OptStatusType.None) {
                                        UiApplication.getUiApplication().pushScreen(new ScreenTermsAndConditions());
                                        close();
                                    } else {
                                        if(!AnalyticsSingleton.GetInstance().IsUserInformationSet()) {
                                            UiApplication.getUiApplication().pushScreen(new ScreenDemographic());
                                            close();
                                        } else {
                                            UiApplication.getUiApplication().pushScreen(new ScreenDog());
                                            close();
                                        }
                                    }
                                    
                                }
                            });
                        } 
                    }
                };
                thread.start();
            }
        }); 
    }
   
} 
