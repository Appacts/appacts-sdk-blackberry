/*
 * ScreenFeedback.java
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

public class ScreenFeedback extends ScreenBase {
    
    protected String ScreenName;
    protected ObjectChoiceField OcfRating;
    protected EditField edtfFeedback;
    
    public ScreenFeedback(String screenName) {   
        super("feedback");
        
        this.setTitle(screenName + " feedback");
        
        this.ScreenName = screenName;
        
        VerticalFieldManager vfmMain = 
            new VerticalFieldManager(VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.USE_ALL_WIDTH);
      
        {    
            LabelField lblHeader =
                new LabelField("We would love to hear what you think off " + screenName + ", give us your rating and comment below:", 
                LabelField.FIELD_HCENTER);
                
            vfmMain.add(lblHeader);
        }
          
        {
            String ratings[] = new String[6];
            
            ratings[0] = "Please select...";
            ratings[1] = "1";
            ratings[2] = "2";
            ratings[3] = "3";
            ratings[4] = "4";
            ratings[5] = "5";
            
            this.OcfRating = new ObjectChoiceField("Ratings:", ratings, 0);

            vfmMain.add(this.OcfRating);
        } 

        {
            LabelField lblFeedback = new LabelField("Feedback:");
            vfmMain.add(lblFeedback);
            
            this.edtfFeedback = new EditField();
            vfmMain.add(this.edtfFeedback);
        }
        
        ButtonField btnSubmit = new ButtonField("Send Feedback");
        btnSubmit.setChangeListener(this.btnSubmit_OnFieldChanged());
        vfmMain.add(btnSubmit);
    
        this.add(vfmMain);
    }
    

    private FieldChangeListener btnSubmit_OnFieldChanged() {
        return new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
                
            	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Submit", null);
                
                if(OcfRating.getSelectedIndex() == 0) {
                	AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "missed rating", null);
                    //todo: notify user
                    return;
                }
                
                try {
                	AnalyticsSingleton.GetInstance().LogFeedback(ScreenName, 
                            Integer.parseInt((String)OcfRating.getChoice(OcfRating.getSelectedIndex())), 
                            edtfFeedback.getText()
                     );
                     
                    close();
                } catch(Exception ex) {
                	AnalyticsSingleton.GetInstance().LogError(ScreenName, "Submit", null, new ExceptionDescriptive(ex));
                    //todo:notify user that there has been an issue
                }
            }
        };
    } 
    
} 
