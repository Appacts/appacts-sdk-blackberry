/*
 * Analytics.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin;

import java.util.Vector;
import javax.bluetooth.UUID;

import AppActs.Plugin.Data.*;
import AppActs.Plugin.Data.Interfaces.*;
import AppActs.Plugin.Device.Interfaces.*;
import AppActs.Plugin.Handlers.*;
import AppActs.Plugin.Interfaces.IAnalytics;
import AppActs.Plugin.Models.*;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.CoverageStatusListener;

public final class Analytics implements IAnalytics {
    
    private String connectionString = "appacts/appacts.db";
    private String baseUrl = "";
    private boolean debugMode = false;
    
    private UUID applicationId;
    private String applicationVersion;
    private UUID sessionId;
    
    private ILogger iLogger;
    private ISettings iSettings;
    private IData iData;
    private IUpload iUpload;
    
    private IDeviceInformation iDeviceInformation;
    private IDeviceDynamicInformation iDeviceDynamicInformation;
    private IPlatform iPlatform;
    
    private final Vector vectorScreenOpen = new Vector();
    private final Vector vectoreContentLoading = new Vector();
    
    private boolean authenticationFailure = false;
    private boolean databaseExists = false;
    private boolean itemsWaitingToBeUploaded = true;
    private int numberOfItemsWaitingToBeUploaded = 0;
    private int optStatusType = OptStatusType.OptIn;
    private boolean uploadWhileUsing = false;
    
    //these three upload types only need to be uploaded once succesfully
    private boolean userProcessed = false;
    private boolean deviceLocationProcessed = false;
    private boolean upgradedProcessed = false;
    
    private Thread threadUpload = null;
    private Object threadUploadLock = new Object();
    private Object threadIsUploadingLock = new Object();
    private boolean threadIsUploading = false;
    private CoverageStatusListener coverageStatusListener;
    private Session session = null;
    private boolean threadUploadInterrupted = false;
    private boolean stopped = false;
    private boolean started = false;
    
    public Analytics() {    

    }
    
	public void Start(String baseUrl, String applicationId, int uploadType) throws Exception {
		if(!this.started) {
			
			if(baseUrl == null || baseUrl.length() == 0)
			{
				throw new Exception("You need to specify baseUrl, i.e. your server api url http://yoursite.com/api/");
			}
			this.baseUrl = baseUrl;
			String appVersion;
			
			try {
				appVersion = ApplicationDescriptor.currentApplicationDescriptor().getVersion();
			} catch(Exception ex) { 
				appVersion = "0";
			}	
			
			this.init(new UUID(applicationId, false), appVersion);
			
			this.setThreadUploadInterrupted(false);
			this.started = true;
			this.stopped = false;
			
			if(uploadType == UploadType.WhileUsingAsync) {
				this.UploadWhileUsingAsync();
			}			
		}
	}
	
	public void Start(String baseUrl, String applicationId) throws Exception {
		this.Start(baseUrl, applicationId, UploadType.WhileUsingAsync);
	}
    
    public void UploadWhileUsingAsync() {
        if(!this.getUploadWhileUsing()) {
            
            this.coverageStatusListener = new CoverageStatusListener() {
                public void coverageStatusChanged(int newCoverage) {
                    uploadIntelligent();
                }
            };

            CoverageInfo.addListener(Application.getApplication(), this.coverageStatusListener);
            this.setUploadWhileUsng(true);
        }
        this.uploadIntelligent();
    } 
    
    public void UploadManual() {
        if(this.getItemsWaitingToBeUploaded() && HttpConnectionManager.HasNetworkCoverage() && !this.getAuthenticationFailure() 
                && this.optStatusType == OptStatusType.OptIn 
                && !this.getThreadUploadInterrupted() && this.databaseExists && this.started) {
	    	synchronized(this.threadUploadLock) {
	            if(this.getThreadIsUploading()) {
		            this.threadUpload = new Thread(new Runnable() {
		                public void run() {
		                    Upload();
		                }
		            });
		            this.threadUpload.start();
		            this.threadUpload.setPriority(Thread.MIN_PRIORITY);
	            }
	    	} 
        }
    }
    
    public void LogError(String screenName, String eventName, String data, ExceptionDescriptive ex) {
         if(this.started && this.databaseExists) {
            try {
                ErrorItem errorItem = new ErrorItem(this.applicationId, screenName, data,
                    this.iDeviceDynamicInformation.GetDeviceGeneralInformation(), eventName, ex, this.sessionId, this.applicationVersion);
                this.iLogger.Save(errorItem);
                
                this.setItemsWaitingToBeUploaded();
                if(this.getUploadWhileUsing()) {
                    this.uploadIntelligent();
                }
                
            } catch(ExceptionDatabaseLayer exceptionDatabaseLayer) {
                this.logSystemError(exceptionDatabaseLayer);
            }
        }
    }
    
    public void LogEvent(String screenName, String eventName, String data) {
         if(this.started && this.databaseExists && this.optStatusType == OptStatusType.OptIn) {
            try {
                EventItem eventItem = new EventItem(this.applicationId, screenName, data, 
                    EventType.Event, eventName, 0, this.sessionId, this.applicationVersion);
                this.iLogger.Save(eventItem);
                
                this.setItemsWaitingToBeUploaded();
                if(this.getUploadWhileUsing()) {
                    this.uploadIntelligent();
                }
                
            } catch(ExceptionDatabaseLayer ex) {
                this.logSystemError(ex);
            }
        }
    }
    
    public void LogEvent(String screenName, String eventName) {
        this.LogEvent(screenName, eventName, null);
   }
    
    public void LogFeedback(String screenName, int rating, String comment) throws ExceptionDatabaseLayer {
        try {
            FeedbackItem feedbackItem = new FeedbackItem(this.applicationId, screenName, 
                comment, rating, this.sessionId, this.applicationVersion);
                
            this.iLogger.Save(feedbackItem);
            
            this.setItemsWaitingToBeUploaded();
            if(this.getUploadWhileUsing()) {
                this.uploadIntelligent();
            }
                
        } catch(ExceptionDatabaseLayer ex) {
            this.logSystemError(ex);
            throw ex;
        }
    }
    
    public void ScreenOpen(String screenName) {
         if(this.started && this.databaseExists && this.optStatusType == OptStatusType.OptIn) {
            try {
            	Session session = new Session(screenName);
            	
        		synchronized(this.vectorScreenOpen) {
	            	if(!this.vectorScreenOpen.contains(session)) {
	            		
	        			this.vectorScreenOpen.addElement(session);
	        			
		                EventItem eventItem = new EventItem(this.applicationId, screenName, null, 
		                    EventType.ScreenOpen, null, 0, this.sessionId, this.applicationVersion);
		                this.iLogger.Save(eventItem);
		                
		                this.setItemsWaitingToBeUploaded();
		                if(this.getUploadWhileUsing()) {
		                    this.uploadIntelligent();
		                }
	            	}
        		}
            } catch(ExceptionDatabaseLayer ex) {
                this.logSystemError(ex);
            }
        }
    }    
    
    
    public void ScreenClosed(String screenName) {
        if(this.started && this.databaseExists && this.optStatusType == OptStatusType.OptIn) {
            try {
                
                long miliSeconds = 0;
                int index = -1;
                
                synchronized(this.vectorScreenOpen) {
	                for(int i = 0; i < this.vectorScreenOpen.size(); i++) {
	                    Session session = (Session)this.vectorScreenOpen.elementAt(i);
	                    if(session.Name.equals(screenName)) {
	                        index = i;
	                        miliSeconds = session.End();
	                        break;
	                    }
	                }
	               
	                if(index != -1) {
	                    this.vectorScreenOpen.removeElementAt(index);
	                    
		                EventItem eventItem = new EventItem(this.applicationId, screenName, null, 
		                    EventType.ScreenClosed, null, miliSeconds, this.sessionId, this.applicationVersion);
		                this.iLogger.Save(eventItem);
		                
		                this.setItemsWaitingToBeUploaded();
		                if(this.getUploadWhileUsing()) {
		                    this.uploadIntelligent();
		                }                    
	                }
                }
            } catch(ExceptionDatabaseLayer ex) {
                this.logSystemError(ex);
            }
        }
    }
    
    public void ContentLoading(String screenName, String contentName) {
        if(this.started && this.databaseExists && this.optStatusType == OptStatusType.OptIn) {
            try {
            	//generates unique name based on user input, i.e. screen A, Loading User Info = screen ALoading User Info
            	String sessionName = screenName.concat(contentName);
            	
            	Session session = new Session(sessionName);
            	
             	synchronized(this.vectoreContentLoading) {           	
	            	if(!this.vectoreContentLoading.contains(session)) {
	            		
	            		this.vectoreContentLoading.addElement(session);
	            		
		                EventItem eventItem = new EventItem(this.applicationId, screenName, null, 
		                    EventType.ContentLoading, contentName, 0, this.sessionId, this.applicationVersion);
		                this.iLogger.Save(eventItem);
		                
		                this.setItemsWaitingToBeUploaded();
		                if(this.getUploadWhileUsing()) {
		                    this.uploadIntelligent();
		                }
	            	}
             	}
            } catch(ExceptionDatabaseLayer ex) {
                this.logSystemError(ex);
            }
        }
    }
    
    public void ContentLoaded(String screenName, String contentName) {
        if(this.started && this.databaseExists && this.optStatusType == OptStatusType.OptIn) {
            try {
                
                long miliSeconds = 0;
                int index = -1;
                
                synchronized(this.vectoreContentLoading) {
                	
                	String sessionName = screenName.concat(contentName);
                	
	                for(int i = 0; i < this.vectoreContentLoading.size(); i++) {
	                    Session session = (Session)this.vectoreContentLoading.elementAt(i);
	                    if(session.Name.equals(sessionName)) {
	                        index = i;
	                        miliSeconds = session.End();
	                        break;
	                    }
	                }
	                
	                if(index != -1) {
	                    this.vectoreContentLoading.removeElementAt(index);
	                    
		                 EventItem eventItem = new EventItem(this.applicationId, screenName, null, 
		                    EventType.ContentLoaded, contentName, miliSeconds, this.sessionId, this.applicationVersion);
		                this.iLogger.Save(eventItem);
		                
		                this.setItemsWaitingToBeUploaded();
		                if(this.getUploadWhileUsing()) {
		                    this.uploadIntelligent();
		                }                   
	                }
                }
            } catch(ExceptionDatabaseLayer ex) {
                this.logSystemError(ex);
            }
        }
    }
    
    public void SetUserInformation(int age, int sexType) throws ExceptionDatabaseLayer {
        try {
            User user = new User(age, sexType, StatusType.Pending, this.applicationId, this.sessionId, this.applicationVersion);
            this.iSettings.Save(user);
            
            this.setItemsWaitingToBeUploaded();
            if(this.getUploadWhileUsing()) {
                this.uploadIntelligent();
            }
            
        } catch(ExceptionDatabaseLayer ex) {
            this.logSystemError(ex);
            throw ex;
        }
    }
    
    public boolean IsUserInformationSet() {
        boolean isUserInformationSet = true;
       
        if(this.started && this.databaseExists) {
            try {
                if(this.iSettings.GetUser(this.applicationId, StatusType.All) == null) {
                    isUserInformationSet = false;
                }
            } catch(ExceptionDatabaseLayer ex) {
                this.logSystemError(ex);
            }
        }
        
        return isUserInformationSet;
    }
    
    public void SetOptStatus(int optStatusType) {
        try {
            this.optStatusType = optStatusType;
            if(this.started && this.databaseExists) {
            	ApplicationMeta applicationMeta = this.iSettings.LoadApplication(this.applicationId);
                applicationMeta.OptStatus = optStatusType;
                this.iSettings.Update(applicationMeta);
            }
        } catch(ExceptionDatabaseLayer ex) {
            this.logSystemError(ex);
        }
    }
    
    public int GetOptStatus() {
        int optStatusType = OptStatusType.None;
        try {
            if(this.started && this.databaseExists) {
            	ApplicationMeta applicationMeta = this.iSettings.LoadApplication(this.applicationId);
                optStatusType = applicationMeta.OptStatus;
            }
        } catch(ExceptionDatabaseLayer ex) {
            this.logSystemError(ex);
        }
        return optStatusType;
    }
    
    public void Stop() {
    	if(this.started) {
	        try {
	        	this.setThreadUploadInterrupted(true);
	        	
	            CoverageInfo.removeListener(Application.getApplication(), this.coverageStatusListener);
	            
	            if(this.getThreadIsUploading()) {
	                this.threadUpload.interrupt();
	            }
	        } catch(Exception ex) { 
	            System.out.println( ex.toString() ); 
	        }
	        
	        if(this.databaseExists) {
	            try {
	                EventItem eventItem = new EventItem(this.applicationId, null, 
	                    null, EventType.ApplicationClose, null, this.session.End(), this.sessionId, this.applicationVersion);
	                this.iLogger.Save(eventItem);
	                
	                ApplicationMeta applicationState = this.iSettings.LoadApplication(this.applicationId);
	                applicationState.State = ApplicationStateType.Close;
	                
	                if(debugMode) {
	                	applicationState.State = ApplicationStateType.Open;
	                }
	                
	                applicationState.SessionId = this.sessionId;
	                this.iSettings.Update(applicationState);
	                
	            } catch(ExceptionDatabaseLayer ex) {
	                this.logSystemError(ex);
	            }
	            
	            try {
	            	synchronized(this.threadIsUploadingLock) {
		            	if(this.getThreadIsUploading()) {
			            	while(this.getThreadIsUploading()) {
			            		this.threadIsUploadingLock.wait();
			            	}
		            	}
	            	}
	            } catch(Exception ex) { 
	            	
	            } finally {
	            	try {
	            		this.iData.Dispose();
	            	} catch(ExceptionDatabaseLayer ex) {
	            		System.out.println("Dispose"); 
	            		System.out.println(ex.toString()); 
	            	}
	            }
	        }
	        
	        this.stopped = true;
	        this.started = false;
	        
	        if(debugMode) {
	        	System.out.println( "analytics stopped" );      
	        }
    	}
    }
    
    private void init(UUID applicationId, String applicationVersion) { 
    	
        this.applicationId = applicationId;
        this.applicationVersion = applicationVersion;
        this.sessionId = new UUID(Utils.generateUUID(), false);
        
        if(debugMode) {
	        System.out.println("Session Id");
	        System.out.println(this.sessionId.toString());
        }
        
        this.iData = new DataDB(connectionString);   
        this.iUpload = new UploadWS(baseUrl);
        
        this.iDeviceInformation = new AppActs.Plugin.Device.DeviceInformation();
        this.iDeviceDynamicInformation = new AppActs.Plugin.Device.DeviceDynamicInformation();
        this.iPlatform = new AppActs.Plugin.Device.Platform();   	
    	
        try {
        	this.initDatabase();		
            
            this.databaseExists = true;
        } catch(ExceptionDatabaseLayer ex) {
        	System.out.println("Init");
        	System.out.println(ex.toString());
        } 
    	
        if(this.databaseExists) {

        	ApplicationMeta applicationMeta = null;
        	boolean applicationInitialSetup = false;
        	
        	try {
        		applicationMeta = this.iSettings.LoadApplication(this.applicationId);
        		
        		if(applicationMeta == null) {
        			applicationMeta = new ApplicationMeta(this.applicationId, ApplicationStateType.Close, 
        					Utils.GetDateTimeNow(), OptStatusType.OptIn);
        			
        			this.iSettings.Save(applicationMeta);
        			applicationInitialSetup = true;
        		} 
        		
        	} catch(ExceptionDatabaseLayer ex) {
                this.logSystemError(ex);
            }
            
        	this.optStatusType = applicationMeta.OptStatus;
        	
            try {
                if(applicationMeta.State == ApplicationStateType.Open) {
                    Crash crash = new Crash(this.applicationId, applicationMeta.SessionId, this.applicationVersion);
                    this.iLogger.Save(crash);
                }
                
                EventItem eventItem = new EventItem(this.applicationId, null, null, 
                    EventType.ApplicationOpen, null, 0, this.sessionId, this.applicationVersion);
                this.iLogger.Save(eventItem);
                
                applicationMeta.SessionId = this.sessionId;
                applicationMeta.State = ApplicationStateType.Open;
                
                if(applicationMeta.Version == null || 
                		!applicationMeta.Version.equals(this.applicationVersion)) {
                	applicationMeta.Version = this.applicationVersion;
            		applicationMeta.Upgraded = !applicationInitialSetup;
                }
                
                this.iSettings.Update(applicationMeta);
                
            } catch(ExceptionDatabaseLayer ex) {
                this.logSystemError(ex);
            }
        }
        
        this.session = new Session();
    }
    
    protected void logSystemError(ExceptionDescriptive ex) {
        try {
        	if(this.started && this.databaseExists) {
        		if(debugMode) {
            		System.out.println("errorIdentified"); 
            		System.out.println(ex.getMessage()); 
            		System.out.println(ex.StackTrace);
            		System.out.println(ex.Source);
        		}
	            this.iLogger.Save(new SystemError(this.applicationId, ex, 
	                new AnalyticsSystem(this.iDeviceInformation.GetDeviceType(), this.iDeviceInformation.GetPluginVersion()), this.applicationVersion));
	            
	            this.setItemsWaitingToBeUploaded();
        	}
        } catch(ExceptionDatabaseLayer exceptionDatabaseLayer) {  
    		if(debugMode) {
        		System.out.println("errorIdentified when logging an error"); 
        		System.out.println(exceptionDatabaseLayer.getMessage()); 
        		System.out.println(exceptionDatabaseLayer.StackTrace);
        		System.out.println(exceptionDatabaseLayer.Source);
    		}
        }
    }
    
    protected void setItemsWaitingToBeUploaded() {
    	this.setNumberOfItemsWaitingToBeUploaded(this.getNumberOfItemsWaitingToBeUploaded()+1);
    	this.setItemsWaitingToBeUploaded(true);
    }
    
    protected void uploadIntelligent() {
        if(this.getItemsWaitingToBeUploaded() && HttpConnectionManager.HasNetworkCoverage() && !this.getAuthenticationFailure() 
                && this.optStatusType == OptStatusType.OptIn && !this.stopped && this.databaseExists && this.started) {
        
            synchronized(this.threadUploadLock) {
            	
                System.out.println("upload thread is active:");
                System.out.println(this.getThreadIsUploading());
	            
                if(!this.getThreadIsUploading()) {    
                	
                  	this.setThreadIsUploading(true);
                  	
	                this.threadUpload = new Thread(new Runnable() {
	                    public void run() {
	                        Upload();
	                    }
	                });
	                this.threadUpload.setPriority(Thread.MIN_PRIORITY);
	                this.threadUpload.start();
                }
            }
        }
    }
    
    protected void Upload() {
        UUID deviceId = null; 
    	boolean exceptionWasRaised = false;
        
        if(!this.getThreadUploadInterrupted()) {

	        try {
	            deviceId = iSettings.GetDeviceId();
	            
	            if(deviceId == null) {
	                deviceId = iUpload.Device
	                    (applicationId, iDeviceInformation.GetModel(), iPlatform.GetOS(),  
	                        DeviceType.Blackberry, iPlatform.GetCarrier(), this.applicationVersion, Utils.TimeOffSet(),
	                        iDeviceInformation.GetManufacturer(), iDeviceInformation.GetScreenResolution(), iDeviceInformation.GetLocale());
	
	                iSettings.SaveDeviceId(deviceId, Utils.GetDateTimeNow());   
	            } else {
	            	if(!this.getUpgradeProcessed()) {
	            	  this.setUpgradeProcessed(this.uploadUpgraded(deviceId));
	            	}
	            }
	        } catch(ExceptionDatabaseLayer ex) {
	        	System.out.println( "device registration or upgrade" );
	            System.out.println( ex.toString() );
	            ex.printStackTrace();
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	        	System.out.println( "device registration or upgrade" );
	            System.out.println( ex.toString() );
	            ex.printStackTrace();
	            exceptionWasRaised = true;
	        }   
        }
        
    	//remember current count before we start processing items, so later we can
        //find out if we need to call upload thread again
        int numberOfItemsWaitingToBeUploadedBefore = this.getNumberOfItemsWaitingToBeUploaded();
        
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised) {
	        try {
	            this.uploadSystemError(deviceId);
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadSytemError" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadSytemError" );
	            exceptionWasRaised = true;
	        }   
        }
        
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised) {
	        try {
	            this.uploadCrash(deviceId);
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadCrash" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadCrash" );
	            exceptionWasRaised = true;
	        }   
        }
       
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised && !this.getUserProcessed()) {
	        try {
	            this.setUserProcessed(this.uploadUser(deviceId));
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadUser" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadUser" );
	            exceptionWasRaised = true;
	        }
        }
            
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised) {
	        try {
	            this.uploadError(deviceId);
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadError" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadError" );
	            exceptionWasRaised = true;
	        }  
        }
       
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised) {
	        try {
	            this.uploadEvent(deviceId);
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadEvent" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadEvent" );
	            exceptionWasRaised = true;
	        }  
        }
        
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised) {
	        try {
	            this.uploadFeedback(deviceId);
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadFeedback" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadFeedback" );
	            exceptionWasRaised = true;
	        }  
        }
        
        if(!this.getThreadUploadInterrupted() && !exceptionWasRaised && !this.getDeviceLocationProcessed()) {
	        try {
	            this.setDeviceLocationProcessed(this.uploadDeviceLocation(deviceId));
	        } catch(ExceptionDatabaseLayer ex) {
	            System.out.println( ex.toString() );
	            System.out.println( "uploadDeviceLocation" );
	            exceptionWasRaised = true;
	        } catch(ExceptionWebServiceLayer ex) { 
	            System.out.println( ex.toString() );
	            System.out.println( "uploadDeviceLocation" );
	            exceptionWasRaised = true;
	        }   
        }
        
        if(!exceptionWasRaised) {
        	this.uploadSuccesfullyCompleted(numberOfItemsWaitingToBeUploadedBefore);
        }
        
        synchronized(this.threadIsUploadingLock) {
        	this.setThreadIsUploading(false);
        	this.threadIsUploadingLock.notifyAll();
        }
    }
    
    private void initDatabase() throws ExceptionDatabaseLayer {
        if(!this.iData.ExistsDatabase()) {
            this.iData.CreateDatabase();
            this.iData.SetupSchema();
        }
              
        this.iLogger = new LoggerDB(this.iData.OpenReadWriteConnection(), this.iData.OpenReadOnlyConnection());
        this.iSettings = new SettingsDB(this.iData.OpenReadWriteConnection(), this.iData.OpenReadOnlyConnection());
        
        PluginMeta pluginMeta = null;
        
        try {
        	pluginMeta = this.iSettings.LoadPlugin();
        } catch(ExceptionDatabaseLayer ex) {  
        	System.out.println("LoadPlugin");
        	System.out.println(ex.toString());
        }
    	
    	int schemaVersionNumericCurrent = Integer.parseInt(Utils.replaceAll(this.iDeviceInformation.GetPluginVersion(), ".", ""));
    	
    	if(pluginMeta == null) {
    		pluginMeta = new PluginMeta(-1);
    	}
    	
        if(this.iData.UpdateSchema(schemaVersionNumericCurrent, pluginMeta.SchemaVersionNumeric)) {
        	if(pluginMeta.SchemaVersionNumeric == -1) {
        		pluginMeta.SchemaVersionNumeric = schemaVersionNumericCurrent;
        		this.iSettings.Save(pluginMeta);
        	} else {
        		pluginMeta.SchemaVersionNumeric = schemaVersionNumericCurrent;
        		this.iSettings.Update(pluginMeta);
        	}
        	
        	//need to reopen all connections to re-cache the schema, otherwise you will encounter issues
        	this.iData.CloseReadOnlyConnection();
        	this.iData.CloseReadWriteConnection();
        	
            this.iLogger = new LoggerDB(this.iData.OpenReadWriteConnection(), this.iData.OpenReadOnlyConnection());
            this.iSettings = new SettingsDB(this.iData.OpenReadWriteConnection(), this.iData.OpenReadOnlyConnection());
        }
    }
    
    private void uploadSuccesfullyCompleted(int numberOfItemsWaitingToBeUploadedBefore) {
    	if(!this.getThreadUploadInterrupted()) {
        	if(numberOfItemsWaitingToBeUploadedBefore == this.getNumberOfItemsWaitingToBeUploaded()) {
        		this.setItemsWaitingToBeUploaded(false);
        	}
        }
    }
    
    private void uploadSystemError(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        SystemError systemError = null;
        do
        {
            systemError = iLogger.GetSystemError(this.applicationId);
            
            if(systemError != null) {
                int responseCode = iUpload.SystemError(deviceId, systemError);
                    
                if(responseCode == WebServiceResponseCodeType.Ok) {
                    iLogger.Remove(systemError);
                } else if(responseCode == WebServiceResponseCodeType.InactiveAccount 
                    || responseCode ==  WebServiceResponseCodeType.InactiveApplication) {
                	this.setAuthenticationFailure(true);
                    return;
                } else if(responseCode == WebServiceResponseCodeType.GeneralError) {
                    return;
                }
            }
        } while(systemError != null && !this.getThreadUploadInterrupted());
    }
    
    private void uploadCrash(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        Crash crash = null;
        do
        {
            crash = iLogger.GetCrash(applicationId);
            
            if(crash != null) {
                int responseCode = iUpload.Crash(deviceId, crash);
                
                if(responseCode == WebServiceResponseCodeType.Ok) {
                    iLogger.Remove(crash);
                } else if(responseCode == WebServiceResponseCodeType.InactiveAccount 
                    || responseCode ==  WebServiceResponseCodeType.InactiveApplication) {
                	this.setAuthenticationFailure(true);
                    return;
                } else if(responseCode == WebServiceResponseCodeType.GeneralError) {
                    return;
                }
            }
        } while(crash != null && !this.getThreadUploadInterrupted());
    }
    
    private void uploadError(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        ErrorItem errorItem = null;
        do
        {
            errorItem = iLogger.GetErrorItem(this.applicationId);
            
            if(errorItem != null) {
        
                int responseCode = iUpload.Error(deviceId, errorItem);
                    
                if(responseCode == WebServiceResponseCodeType.Ok) {
                    iLogger.Remove(errorItem);
                } else if(responseCode == WebServiceResponseCodeType.InactiveAccount 
                    || responseCode ==  WebServiceResponseCodeType.InactiveApplication) {
                	this.setAuthenticationFailure(true);
                    return;
                } else if(responseCode == WebServiceResponseCodeType.GeneralError) {
                    return;
                }
            }
        } while(errorItem != null && !this.getThreadUploadInterrupted());
    }
    
    private void uploadEvent(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        EventItem eventItem = null;
        do
        {
            eventItem = iLogger.GetEventItem(this.applicationId);
            
            if(eventItem != null) {
                int responseCode = iUpload.Event(deviceId, eventItem);
                    
                if(responseCode == WebServiceResponseCodeType.Ok) {
                    iLogger.Remove(eventItem);
                } else if(responseCode == WebServiceResponseCodeType.InactiveAccount 
                    || responseCode ==  WebServiceResponseCodeType.InactiveApplication) {
                	this.setAuthenticationFailure(true);
                    return;
                } else if(responseCode == WebServiceResponseCodeType.GeneralError) {
                    return;
                }
              
            }
        } while(eventItem != null && !this.getThreadUploadInterrupted());
    }
    
    private void uploadFeedback(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
        FeedbackItem feedbackItem = null;
        do
        {
            feedbackItem = iLogger.GetFeedbackItem(this.applicationId);
            
            if(feedbackItem != null) {
                int responseCode = iUpload.Feedback(deviceId, feedbackItem);

                if(responseCode == WebServiceResponseCodeType.Ok) {
                    iLogger.Remove(feedbackItem);
                } else if(responseCode == WebServiceResponseCodeType.InactiveAccount 
                    || responseCode ==  WebServiceResponseCodeType.InactiveApplication) {
                	this.setAuthenticationFailure(true);
                    return;
                } else if(responseCode == WebServiceResponseCodeType.GeneralError) {
                    return;
                }
            }
        } while(feedbackItem != null && !this.getThreadUploadInterrupted());
    }
    
    private boolean uploadUser(UUID deviceId) 
	    throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
	   User user = iSettings.GetUser(this.applicationId, StatusType.Pending);
	
	   if(user != null && user.Status == StatusType.Pending) {
	        int responseCode = iUpload.User(deviceId, user);
	
	        if(responseCode == WebServiceResponseCodeType.Ok) {
	            iSettings.Update(user, StatusType.Processed);
	            return true;
	        } else if(responseCode == WebServiceResponseCodeType.InactiveAccount 
	            || responseCode ==  WebServiceResponseCodeType.InactiveApplication) {
	        	this.setAuthenticationFailure(true);
	        	return false;
	        }
	   } else if(user == null) {
		   return false;
	   }
	   
	   return true;
	}
    
    private boolean uploadDeviceLocation(UUID deviceId) 
        throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
           
        DeviceLocation deviceLocationProcessed = iSettings.GetDeviceLocation(StatusType.Processed);
        
        if(deviceLocationProcessed == null) {
           
            DeviceLocation deviceLocationPending = iSettings.GetDeviceLocation(StatusType.Pending);
            
            if(deviceLocationPending == null) {
                try {
                    deviceLocationPending = this.iDeviceDynamicInformation.GetDeviceLocation();
                    this.iSettings.Save(deviceLocationPending, StatusType.Pending);
                } catch(Exception ex) {
                    System.out.println( ex.toString() );
                    deviceLocationPending = null;
                }
            }
            
            if(deviceLocationPending != null) {
                int responseCode = iUpload.Location(deviceId, applicationId, deviceLocationPending);
                if(responseCode == WebServiceResponseCodeType.Ok) {
                    iSettings.Save(deviceLocationPending, StatusType.Processed);
                    return true;
                } else if(responseCode == WebServiceResponseCodeType.InactiveAccount || responseCode ==  WebServiceResponseCodeType.InactiveApplication) {
                	this.setAuthenticationFailure(true);
                    return false;
                } else if(responseCode == WebServiceResponseCodeType.GeneralError) {
                    return false;
                }
            } else {
            	return false;
            }
        }
        
        return true;
    }
    
    private boolean uploadUpgraded(UUID deviceId) 
    	throws ExceptionDatabaseLayer, ExceptionWebServiceLayer {
    	
    	ApplicationMeta applicationMeta = iSettings.LoadApplication(applicationId);
        
        if(applicationMeta.Upgraded) {
            int responseCode = iUpload.Upgrade(deviceId, applicationId, applicationMeta.Version);

            if(responseCode == WebServiceResponseCodeType.Ok) {
            	applicationMeta.Upgraded = false;
                iSettings.Update(applicationMeta);
                return true;
            } else if(responseCode == WebServiceResponseCodeType.InactiveAccount 
                || responseCode ==  WebServiceResponseCodeType.InactiveApplication) {
            	this.setAuthenticationFailure(true);
                return false;
            } else if(responseCode == WebServiceResponseCodeType.GeneralError) {
                return false;
            }
        }
        
        return true;
    }
    
    private synchronized int getNumberOfItemsWaitingToBeUploaded(){
    	return this.numberOfItemsWaitingToBeUploaded;
    }
    
    private synchronized void setNumberOfItemsWaitingToBeUploaded(int numberOfItemsWaitingToBeUploaded) {
    	this.numberOfItemsWaitingToBeUploaded = numberOfItemsWaitingToBeUploaded;
    }
    
    private synchronized boolean getThreadUploadInterrupted() {
    	return this.threadUploadInterrupted;
    }
    
    private synchronized void setThreadUploadInterrupted(boolean threadUploadInterrupted) {
    	this.threadUploadInterrupted = threadUploadInterrupted;
    }
    
    private synchronized boolean getItemsWaitingToBeUploaded(){
    	return this.itemsWaitingToBeUploaded;
    }    
    
    private synchronized void setItemsWaitingToBeUploaded(boolean itemsWaitingToBeUploaded) {
    	this.itemsWaitingToBeUploaded = itemsWaitingToBeUploaded;
    }
    
    private synchronized boolean getAuthenticationFailure() {
    	return this.authenticationFailure;
    }
    
    private synchronized void setAuthenticationFailure(boolean authenticationFailure) {
    	this.authenticationFailure = authenticationFailure;
    }
    
    private synchronized boolean getUploadWhileUsing(){
    	return this.uploadWhileUsing;
    }  
    
    private synchronized void setUploadWhileUsng(boolean uploadWhileUsing){
    	this.uploadWhileUsing = uploadWhileUsing;
    }
    
    private synchronized void setThreadIsUploading(boolean threadIsUploading) {
    	this.threadIsUploading = threadIsUploading;
    }
    
    private synchronized boolean getThreadIsUploading() {
    	return this.threadIsUploading;
    }
    
    private synchronized boolean getUserProcessed() {
    	return this.userProcessed;
    }
    
    private synchronized void setUserProcessed(boolean userProcessed) {
    	this.userProcessed = userProcessed;
    }
    
    private synchronized boolean getDeviceLocationProcessed(){
    	return this.deviceLocationProcessed;
    }
    
    private synchronized void setDeviceLocationProcessed(boolean deviceLocationProcessed) {
    	this.deviceLocationProcessed = deviceLocationProcessed;
    }
    
    private synchronized void setUpgradeProcessed(boolean upgradeProcessed) {
    	this.upgradedProcessed  = upgradeProcessed;
    }
    
    private synchronized boolean getUpgradeProcessed() {
    	return this.upgradedProcessed;
    }

} 
