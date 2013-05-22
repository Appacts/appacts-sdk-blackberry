/*
 * ScreenDog.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package SampleApplication;

import AppActs.Plugin.AnalyticsSingleton;
import AppActs.Plugin.Models.*;

import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import java.util.Random;
import net.rim.device.api.ui.MenuItem;

public final class ScreenDog extends ScreenBase {
    
    private MenuItem menuFeedback = new MenuItem("Feedback", 110, 10) {
        public void run() {
            UiApplication.getUiApplication().pushScreen(new ScreenFeedback(ScreenName));
        }
    };  
    
    protected LabelField LblName;
    
    public ScreenDog() { 
        super("dog");
        
        this.setTitle("Dog Name");
        this.addMenuItem(this.menuFeedback);
        
        VerticalFieldManager vfmMain = 
            new VerticalFieldManager(VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.USE_ALL_WIDTH);
        
        {
            ButtonField btnGenerate = new ButtonField("Generate Random Dog Name", ButtonField.CONSUME_CLICK);
            btnGenerate.setChangeListener(this.btnGenerate_OnFieldChanged());
            
            vfmMain.add(btnGenerate);
        }
        
        {
            this.LblName = new LabelField();
            vfmMain.add(this.LblName);
            
            ButtonField btnDogs = new ButtonField("Dogs", ButtonField.VISUAL_STATE_DISABLED);
            vfmMain.add(btnDogs);
            
            ButtonField btnCats = new ButtonField("Cats",  ButtonField.CONSUME_CLICK);
            btnCats.setChangeListener(this.btnCats_OnFieldChanged());
            vfmMain.add(btnCats);
        }
       
       this.add(vfmMain);
    }
    
    private FieldChangeListener btnGenerate_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
      
            	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Generate Dog", null);
                
            	AnalyticsSingleton.GetInstance().ContentLoading(ScreenName, "Generating Dog");
                
                try {
                    String[] names = { "Laimo", "Smokey", "Lucy", "Fred", "Boy", "Cute", "Butch", "Alpha" };
                    
                    LblName.setText(names[new Random(System.currentTimeMillis()).nextInt(8)-1]);
                    
                }catch(Exception ex) {
                	AnalyticsSingleton.GetInstance().LogError(ScreenName, "Generate Dog", null, new ExceptionDescriptive(ex));
                    //todo: notify user of the error
                }
                
                //calculating screen/content loading speed
                AnalyticsSingleton.GetInstance().ContentLoaded(ScreenName, "Generating Dog");
            }
        };
    }
    
    private FieldChangeListener btnCats_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
               // GetAnalytics().LogEvent(ScreenName, "Show Cat", null);
                UiApplication.getUiApplication().pushScreen(new ScreenCat());
                close();
            }
        };
    }
    
     /**
     * On Save Promot, no need to save changes so return true by default
     * @return <description>
     */
    public boolean onSavePrompt() {
        return true;
    }
} 
