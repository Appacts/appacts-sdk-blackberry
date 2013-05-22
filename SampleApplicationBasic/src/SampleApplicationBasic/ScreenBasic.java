package SampleApplicationBasic;

import java.util.Random;

import AppActs.Plugin.AnalyticsSingleton;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.VirtualKeyboard;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class ScreenBasic extends MainScreen
{
	 protected LabelField LblName;
	
    public ScreenBasic()
    {           
        this.setTitle("Screen Basic");
       
        VerticalFieldManager vfmMain = 
            new VerticalFieldManager(VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.USE_ALL_WIDTH);
        
        {
            ButtonField btnGenerate = new ButtonField("Generate Random Dog Name", ButtonField.CONSUME_CLICK);
            btnGenerate.setChangeListener(this.btnGenerate_OnFieldChanged());
            
            vfmMain.add(btnGenerate);
        }
       
       this.add(vfmMain);
    }
    
   
    private FieldChangeListener btnGenerate_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
            	
            	/*
            	 * Appacts
            	 * To log an event just call LogEvent(screenName, eventName)
            	 */
            	AnalyticsSingleton.GetInstance().LogEvent("Screen Basic", "Generate Dog");
                
            	String[] names = { "Laimo", "Smokey", "Lucy", "Fred", "Boy", "Cute", "Butch", "Alpha" };
            	
                LblName.setText(names[new Random(System.currentTimeMillis()).nextInt(6)]);
            }
        };
    }
    
    /*
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() {
    	
    	
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
