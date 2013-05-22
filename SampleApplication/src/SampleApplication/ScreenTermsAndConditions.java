/*
 * ScreenSplash.java
 *
 * © Appacts, 2012
 * Confidential and proprietary.
 */

package SampleApplication;

import AppActs.Plugin.AnalyticsSingleton;
import AppActs.Plugin.Models.OptStatusType;

import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;

public final class ScreenTermsAndConditions extends ScreenBase {
    
    public ScreenTermsAndConditions() {    
        super("Terms and Conditions");
        
        VerticalFieldManager vfmMain = 
            new VerticalFieldManager(VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.USE_ALL_WIDTH);
        
        {    
            LabelField lblHeader =
                new LabelField("Do you agree to our terms and conditions?", 
                LabelField.FIELD_HCENTER);
                
            vfmMain.add(lblHeader);
        }
        
        {
            ButtonField btnAccept = new ButtonField("I Agree");
            btnAccept.setChangeListener(this.btnAccept_OnFieldChanged());
            
            ButtonField btnDontAccept = new ButtonField("I Don't Agree");
            btnDontAccept.setChangeListener(this.btnDontAccept_OnFieldChanged());
            
            HorizontalFieldManager hfmButtons = new HorizontalFieldManager();
            hfmButtons.add(btnAccept);
            hfmButtons.add(btnDontAccept);
            
            vfmMain.add(hfmButtons);
        }
        
        this.add(vfmMain);
    }
    
    private FieldChangeListener btnAccept_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
            	AnalyticsSingleton.GetInstance().SetOptStatus(OptStatusType.OptIn);
                UiApplication.getUiApplication().pushScreen(new ScreenDemographic());
                close();
            }
        };
    }
    
    private FieldChangeListener btnDontAccept_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
            	AnalyticsSingleton.GetInstance().SetOptStatus(OptStatusType.OptOut);
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
