/*
 * ScreenCat.java
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

public class ScreenCat extends ScreenBase {
    
    private MenuItem menuFeedback = new MenuItem("Feedback", 110, 10) {
        public void run() {
            UiApplication.getUiApplication().pushScreen(new ScreenFeedback(ScreenName));
        }
    };  
    
    protected LabelField LblName;
    
    public ScreenCat() { 
       super("cat");
       
       this.setTitle("Cat Name");
       this.addMenuItem(this.menuFeedback);
       
        VerticalFieldManager vfmMain = 
            new VerticalFieldManager(VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.USE_ALL_WIDTH);
        
        {
            ButtonField btnGenerate = new ButtonField("Generate Random Cat Name",  ButtonField.CONSUME_CLICK);
            btnGenerate.setChangeListener(this.btnGenerate_OnFieldChanged());
            
            vfmMain.add(btnGenerate);
        }
        
        {
            this.LblName = new LabelField();
            vfmMain.add(this.LblName);
            
            ButtonField btnCats = new ButtonField("Cats", ButtonField.VISUAL_STATE_DISABLED);
            vfmMain.add(btnCats);
            
            ButtonField btnDogs = new ButtonField("Dogs",  ButtonField.CONSUME_CLICK);
            btnDogs.setChangeListener(this.btnDogs_OnFieldChanged());
            vfmMain.add(btnDogs);
        }
       
       this.add(vfmMain);
    }
    
    private FieldChangeListener btnGenerate_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
      
            	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Generate Cat", null);
                
            	AnalyticsSingleton.GetInstance().ContentLoading(ScreenName, "Generating Cat");
                
                try {
                    String[] names = { "Lilly", "Bella", "Hun", "Queen", "Sleepy", "Cute", "PoP", "Beta" };
                    
                    LblName.setText(names[new Random(System.currentTimeMillis()).nextInt(8)-1]);
                }catch(Exception ex) {
                	AnalyticsSingleton.GetInstance().LogError(ScreenName, "Generate Cat", null, new ExceptionDescriptive(ex));
                    //todo: notify user of the error
                }
                
                AnalyticsSingleton.GetInstance().ContentLoaded(ScreenName, "Generating Cat");
            }
        };
    }
    
    private FieldChangeListener btnDogs_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
            	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Show Dog", null);
                UiApplication.getUiApplication().pushScreen(new ScreenDog());
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
