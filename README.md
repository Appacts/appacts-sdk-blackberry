Integration Examples
===============

If you need help referencing / deploying the binary files check out our Wiki:

https://github.com/Appacts/appacts-sdk-blackberry/wiki/Referencing-Blackberry-Java-SDK

https://github.com/Appacts/appacts-sdk-blackberry/wiki/Simulator-&-Deployment

##Basic Integration##


###Methods###

For basic integration, you can use the following methods:

    void Start(String applicationId, String serverUrl);
    void LogEvent(String screenName, String eventName);
    void Stop();
    

####Sample Usage####

#####Main#####

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
            AnalyticsSingleton.GetInstance().Start("9baa4776ed8f42ecb7dc94f1e2a8ac87", "http://yourserver.com/api/");
            
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
            AnalyticsSingleton.GetInstance().Start("9baa4776ed8f42ecb7dc94f1e2a8ac87", "http://yourserver.com/api/");
            
            
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
#####Screen#####

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
    
##Advanced Integration##

###Methods###

For advanced integration, you can use the following methods:

    void LogError(String screenName, String eventName, String data, ExceptionDescriptive ex);
    void LogEvent(String screenName, String eventName, String data);
    void LogFeedback(String screenName, int ratingType, String comment) throws ExceptionDatabaseLayer;
    void ScreenOpen(String screenName);
    void ScreenClosed(String screenName);
    void ContentLoading(String screenName, String contentName);   
    void ContentLoaded(String screenName, String contentName);
    void SetUserInformation(int age, int sexType) throws ExceptionDatabaseLayer;
    boolean IsUserInformationSet();
    void SetOptStatus(int optStatusType);
    int GetOptStatus();
    void UploadWhileUsingAsync();
    void UploadManual();
  
####Getting an instance####

When your application is opened you need to obtain a new instance of IAnalytics. You can do this easily by using AnalyticsSingleton. For example:

######Import######

    import AppActs.Plugin.*;


######Methods######

    IAnalytics AnalyticsSingleton.GetInstance()


######Sample######

    IAnalytics iAnalytics = AnalyticsSingleton.GetInstance();


######Optional######

We suggest that you add an abstract base class into your application so that you have a common area from where everything derives. For example:
    
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
        
        /*
         * @see net.rim.device.api.ui.Screen#close()
         */
        public void close() {
            AnalyticsSingleton.GetInstance().ScreenClosed(this.ScreenName);
            if(VirtualKeyboard.isSupported()) {
        this.getVirtualKeyboard().setVisibility(VirtualKeyboard.HIDE_FORCE);
            }
            if(this.getScreenBelow() == null && this.getScreenAbove() == null) {
                //there are no more screens, so application is closing, dispose of analytics
                AnalyticsSingleton.GetInstance().Stop();
            }
            super.close();
        }
    }


Important: You must override the On Exposed method so that anytime your app & screen comes into the foreground the open interface will be called. Now that app users can have several applications open at the same time (and jump between them) it’s really important that you call this to get precise statistics.


####Session Management####

######Import######

  import AppActs.Plugin.*;


######Methods######

void IAnalytics.Start(String applicationId, String serverUrl)
void IAnalytics.Stop()


######Sample - main.java######
  
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
              AnalyticsSingleton.GetInstance().Start("9baa4776ed8f42ecb7dc94f1e2a8ac87", "http://yourserver.com/api/");
              
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
              AnalyticsSingleton.GetInstance().Start("9baa4776ed8f42ecb7dc94f1e2a8ac87", "http://yourserver.com/api/");
              
              
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
  
Note: Please make sure that .Start & .Stop is always called from main thread, these two methods need to hook in to network coverage event handler.

####Opt In or Opt out?####

Every app is different. Your business needs to make a decision about how it wants to deal with its users. You can be really nice and ask your users whether or not they would like to participate in your customer experience improvement program. If they say yes you can use our services and log their experience. Alternatively you can make them opt in automatically by accepting your terms and conditions. Either way here is how you control opt in/ out in the terms and conditions scenario:

######Import######

      import AppActs.Plugin.Models.OptStatusType;
    

######Methods######

      int GetOptStatus();
      void SetOptStatus(int optStatusType);
    

######Sample - GetOptStatus######

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


######Sample - SetOptStatus######

       private FieldChangeListener btnAgree_OnFieldChanged() {
          return new FieldChangeListener() {
              public void fieldChanged(Field field, int context) {
                  AnalyticsSingleton.GetInstance().SetOptStatus(OptStatusType.OptIn);
                  UiApplication.getUiApplication().pushScreen(new ScreenDemographic());
                  close();
              }
          };
      }
      private FieldChangeListener btnDontAgree_OnFieldChanged() {
          return new FieldChangeListener() {
              public void fieldChanged(Field field, int context) {
                  AnalyticsSingleton.GetInstance().SetOptStatus(OptStatusType.OptOut);
                  AnalyticsSingleton.GetInstance().Stop();
                  close();
              }
          };
      }


####Demographics####

To improve your app you need to know who is using it, how old they are, what their gender is & where they are from. We have made it easy for you to capture this information:

######Import######

    import AppActs.Plugin.Models.*;


######Methods######

     IsUserInformationSet();
     void SetUserInformation(int age, int sexType) throws ExceptionDatabaseLayer
  

######Sample - IsUserInformationSet######
    
      if(!AnalyticsSingleton.GetInstance().IsUserInformationSet()) {
             UiApplication.getUiApplication().pushScreen(new ScreenDemographic());
             close();
      } else {
         UiApplication.getUiApplication().pushScreen(new ScreenDog());
         close();
      }


######Sample - SetUserInformation######

  try {
      AnalyticsSingleton.GetInstance().SetUserInformation(OcfAge.getSelectedIndex(), OcfGender.getSelectedIndex());
          UiApplication.getUiApplication().pushScreen(new ScreenDog());
          close(); 
  } catch(Exception ex) {
      AnalyticsSingleton.GetInstance().LogError(ScreenName, "Next", null, new ExceptionDescriptive(ex));
      //tell user that there was an issue, and we couldn't save their data
          //this could have happend due to flash drive being full, device corypt
  }
  
Note: Our plugin throws exception if it can't save users information. Normally this happens when user’s storage card is not present, it was corrupt, or device is full. As we throw an error you can notify a user that there was an issue or just handle it using in your app as per your business requirements.



####Logging and uploading your customers experience####

######Import######

    import com.appacts.plugin.Models.ExceptionDescriptive;


######Methods######

    void LogError(String screenName, String eventName, String data, ExceptionDescriptive ex);
    void LogEvent(String screenName, String eventName, String data);
    void LogFeedback(String screenName, int ratingType, String comment) throws ExceptionDatabaseLayer;
    void ScreenOpen(String screenName);
    void ScreenClosed(String screenName);
    void ContentLoading(String screenName, String contentName);   
    void ContentLoaded(String screenName, String contentName);
    void UploadWhileUsingAsync();
    void UploadManual();


######Sample - LogError######

                try {
                    String[] names = { "Lilly", "Bella", "Hun", "Queen", "Sleepy", "Cute", "PoP", "Beta" };
                    LblName.setText(names[new Random(System.currentTimeMillis()).nextInt(8)-1]);
                }catch(Exception ex) {
                    AnalyticsSingleton.GetInstance().LogError(ScreenName, "Generate Cat", null, new ExceptionDescriptive(ex));
                    //todo: notify user of the error
                }


######Sample - LogEvent######

      private FieldChangeListener btnGenerate_OnFieldChanged() {
          return new FieldChangeListener() {
              public void fieldChanged(Field field, int context) {
                  AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Generate Cat", null);
           }
        }
      }


######Sample - LogFeedback######
    
      try {
                AnalyticsSingleton.GetInstance().LogFeedback(ScreenName, 
                  Integer.parseInt( (String)OcfRating.getChoice(OcfRating.getSelectedIndex())), 
                  edtfFeedback.getText());
                   close();
          } catch(Exception ex) {
               AnalyticsSingleton.GetInstance().LogError(ScreenName, "Submit", null, new ExceptionDescriptive(ex));
               //todo:notify user that there has been an issue
           }
           
Note: Our plugin throws exception if it can't save users information. Normally this happens when user’s storage card is not present, it was corrupt, or device is full. As we throw an error you can notify a user that there was an issue or just handle it using in your app as per your business requirements.



######Sample - ScreenOpen & ScreenClosed######

    public final String ScreenName;
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
      public void close() {
          AnalyticsSingleton.GetInstance().ScreenClosed(this.ScreenName);
          super.close();
      }


Note: Screen names need to be unique for each screen. We collect screen names in our plugin so that when Screen Closed is called we can calculate how long the user was on the screen for. You can use ScreenOpen many times but it will only register each unique screen name once. This is why ScreenBase & onExposed sometimes call ScreenOpen twice and it works fine.

######Sample - ContentLoading & ContentLoaded######

      private FieldChangeListener btnGenerate_OnFieldChanged() {
          return new FieldChangeListener() {
              public void fieldChanged(Field field, int context) {                
                  AnalyticsSingleton.GetInstance().ContentLoading("Generating Dog");
                  try {
                      String[] names = { "Laimo", "Smokey", "Lucy", "Fred", "Boy", "Cute", "Butch", "Alpha" };
                      LblName.setText(names[new Random(System.currentTimeMillis()).nextInt(8)-1]);
                  }catch(Exception ex) {
                      AnalyticsSingleton.GetInstance().LogError(ScreenName, "Generate Dog", null, new ExceptionDescriptive(ex));
                      //todo: notify user of the error
                  }
                  //calculating screen/content loading speed
                  AnalyticsSingleton.GetInstance().ContentLoaded("Generating Dog");
              }
          };
      }


#####Sample - UploadWhileUsingAsync & UploadManual#####

We have created two methods for two different scenarios:

*UploadWhileUsingAsync – use this when you are creating a light application, i.e. utilities, forms, etc.. Using this method we will take care of all data uploading. As soon as the user creates an event we will try and upload this event to our servers and present it to you in your reports. The aim of this approach is to prevent waiting and obtain data straight away. Using this approach is recommended by our team as this will monitor network coverage, event queues and it will do its best to get data to our servers immediately.

*UploadManual – use this when you have a very event heavy application i.e. game. Using this method you will need to raise the upload event manually when you are ready. This is a very light approach and popular among some app makers, however data might not be uploaded to our servers for days/ weeks (depending on the app use) therefore statistics will be delayed.

*UploadWhileUsingAsync & UploadManual – you could always use both together. You can specify that you want to upload manually and later call UploadWhileUsingAsync. The example below will demonstrate this.

######UploadWhileUsingAsync######

    AnalyticsSingleton.GetInstance().Start("9baa4776ed8f42ecb7dc94f1e2a8ac87", "http://yourserver.com/api/", UploadType.WhileUsingAsync)


By specifying Upload Type While Using Async during the initial singleton request, the plugin will automatically start uploading data while a user is using the app.

######UploadManual######

    AnalyticsSingleton.GetInstance().Start("9baa4776ed8f42ecb7dc94f1e2a8ac87", "http://yourserver.com/api/", UploadType.Manual)

By specifying Upload Type Manual during the initial singleton request, the plugin will not upload any data. It will just collect it and you will need to manually trigger either “UploadManual” or “UploadWhileUsingAsync” i.e.

      private FieldChangeListener btnCats_OnFieldChanged() {
          return new FieldChangeListener() {
              public void fieldChanged(Field field, int context) {
                  AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Show Cat");
          AnalyticsSingleton.GetInstance().UploadManual();
                  UiApplication.getUiApplication().pushScreen(new ScreenCat());
                  close();
              }
          };
      }


You have to manually trigger upload if you want an upload to take place at a certain point. As mentioned before it has many draw backs, although it must be used in heavy data collection scenarios.

     private FieldChangeListener btnCats_OnFieldChanged() {
          return new FieldChangeListener() {
              public void fieldChanged(Field field, int context) {
                  AnalyticsSingleton.GetInstance().LogEvent(ScreenName, "Show Cat");
          AnalyticsSingleton.GetInstance().UploadWhileUsingAsync();
                  UiApplication.getUiApplication().pushScreen(new ScreenCat());
                  close();
              }
          };
      }


You can call UploadWhileUsingAsync manually later if you called your singleton with “UploadType.Manual”. This can be useful in different scenarios but this approach should be rarely used.
