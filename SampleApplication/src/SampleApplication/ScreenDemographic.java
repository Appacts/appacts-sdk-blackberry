/*
 * ScreenDemographic.java
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

public final class ScreenDemographic extends ScreenBase {
    
    protected ObjectChoiceField OcfAge;
    protected ObjectChoiceField OcfGender;
    
    public ScreenDemographic() {    
        super("demographic");
        this.setTitle("Customer Experience");
        
        VerticalFieldManager vfmMain = 
            new VerticalFieldManager(VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.USE_ALL_WIDTH);
        
        {    
            LabelField lblHeader =
                new LabelField("In order for us to improve your experience we would like to ask few anonymous questions:", 
                LabelField.FIELD_HCENTER);
                
            vfmMain.add(lblHeader);
        }
        
        {
            String ages[] = new String[110];
            
            ages[0] = "Please select...";
            
            for(int i = 1; i < 110; i++) {
                ages[i] = Integer.toString(i);
            }
            
            this.OcfAge = new ObjectChoiceField("Age:", ages, 0);
            
            vfmMain.add(this.OcfAge);
        }
        
        {
            String genders[] = new String[3];
            
            genders[0] = "Please select...";
            genders[1] = "Male";
            genders[2] = "Female";
            
            this.OcfGender = new ObjectChoiceField("Gender:", genders, 0);

            vfmMain.add(this.OcfGender);
        }
        
        {
            ButtonField btnSkip = new ButtonField("Skip");
            btnSkip.setChangeListener(this.btnSkip_OnFieldChanged());
            
            ButtonField btnNext = new ButtonField("Next");
            btnNext.setChangeListener(this.btnNext_OnFieldChanged());
            
            HorizontalFieldManager hfmButtons = new HorizontalFieldManager();
            hfmButtons.add(btnSkip);
            hfmButtons.add(btnNext);
            
            vfmMain.add(hfmButtons);
        }
        
        
        this.add(vfmMain);
        
    }
    
    private FieldChangeListener btnNext_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
                
            	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Next", null);
                
                if(OcfAge.getSelectedIndex() == 0) {
                	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Next missed age", null);
                    //todo: next notify user that age wasn't selected
                    return;
                }
                
                if(OcfGender.getSelectedIndex() == 0) {
                	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Next missed gender", null);
                    //todo: next notify user that gender wasn't selected
                    return;
                }
                
                try {
                	AnalyticsSingleton.GetInstance().SetUserInformation(OcfAge.getSelectedIndex(), OcfGender.getSelectedIndex());
                        
                    UiApplication.getUiApplication().pushScreen(new ScreenDog());
                    close(); 
                } catch(Exception ex) {
                	AnalyticsSingleton.GetInstance().LogError(ScreenName, "Next", null, new ExceptionDescriptive(ex));
                    
                    //tell user that there was an issue, and we couldn't save their data
                    //this could have happend due to flash drive being full, device corypt
                }
                
            }
        };
    }
    
    private FieldChangeListener btnSkip_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
            	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Skip", null);
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
