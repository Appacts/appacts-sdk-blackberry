/*
 * UploadWS.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Data;

import javax.bluetooth.UUID;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.Connector;
import java.io.InputStream;
import net.rim.device.api.xml.parsers.*;
import org.w3c.dom.*;

import AppActs.Plugin.Data.Interfaces.IUpload;
import AppActs.Plugin.Handlers.*;
import AppActs.Plugin.Models.*;

public final class UploadWS implements IUpload {
    
    private final String baseUrl;
    
    public UploadWS(String baseUrl) { 
        this.baseUrl = baseUrl;
    }
    
    public int Crash(UUID deviceId, Crash crash) throws ExceptionWebServiceLayer {
         KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, crash.ApplicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(crash.DateCreated)),
            new KeyValuePair(QueryStringKeyType.SESSION_ID, crash.SessionId.toString()),
            new KeyValuePair(QueryStringKeyType.VERSION, crash.Version)
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.Crash, keyValuePairs);
    }
    
    public UUID Device(UUID applicationId, String model, String osVersion, int deviceType,
        String carrier, String applicationVersion, int timeZoneOffset, String manufacturer, ScreenResolution screenRes, 
        String locale) throws ExceptionWebServiceLayer {

        UUID deviceGuid = null;
        int responseCode = 0;
        HttpConnection httpConnection = null;
        InputStream inputStream = null;
        
        try {
            if(HttpConnectionManager.HasNetworkCoverage()) {
                String url = this.baseUrl + WebServices.Device;
                url = this.addToUrl(url, QueryStringKeyType.APPLICATION_GUID, applicationId.toString());
                url = this.addToUrl(url, QueryStringKeyType.MODEL, model);
                url = this.addToUrl(url, QueryStringKeyType.PLATFORM_TYPE, Integer.toString(deviceType));
                url = this.addToUrl(url, QueryStringKeyType.CARRIER, carrier);
                url = this.addToUrl(url, QueryStringKeyType.OPERATING_SYSTEM, osVersion);
                url = this.addToUrl(url, QueryStringKeyType.VERSION, applicationVersion);
                url = this.addToUrl(url, QueryStringKeyType.TIME_ZONE_OFFSET, Integer.toString(timeZoneOffset));
                url = this.addToUrl(url, QueryStringKeyType.MANUFACTURER, manufacturer);
                url = this.addToUrl(url, QueryStringKeyType.RESOLUTION_WIDTH, Integer.toString(screenRes.Width));
                url = this.addToUrl(url, QueryStringKeyType.RESOLUTION_HEIGHT, Integer.toString(screenRes.Height));
                url = this.addToUrl(url, QueryStringKeyType.LOCALE, locale);
                
                System.out.println( url );
                url += HttpConnectionManager.GetConnectionStringByNetworkCoverage();
                httpConnection = (HttpConnection)Connector.open(url, Connector.READ, true);
                
                httpConnection.setRequestMethod("GET");
                
                if(httpConnection.getResponseCode() == HttpConnection.HTTP_OK) {
                    
                    inputStream = httpConnection.openInputStream();
                    
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document documentXmlOutput = documentBuilder.parse(inputStream);

                    Element elementRoot = documentXmlOutput.getDocumentElement();
                    elementRoot.normalize();
                    NodeList nodeList = elementRoot.getChildNodes();
                    
                    Node nodeResponse = nodeList.item(0);
                    responseCode = Integer.parseInt(nodeResponse.getFirstChild().getNodeValue());
                    System.out.println("server response: " + Integer.toString(responseCode));
                    
                    Node nodeDevice = nodeList.item(1);
                    System.out.println("value from service:" + nodeDevice.getFirstChild().getNodeValue());
                    //BB UUID library code dosen't like dashes in GUID, need to remove them
                    String deviceGuidRaw =  nodeDevice.getFirstChild().getNodeValue();
                    
                    deviceGuidRaw = deviceGuidRaw.substring(0, 8) + 
                            deviceGuidRaw.substring(9, 13) + 
                            deviceGuidRaw.substring(14, 18) + 
                            deviceGuidRaw.substring(19, 23) + 
                            deviceGuidRaw.substring(24, 36);
                    
                    System.out.println("device uuid formated: " + deviceGuidRaw);
                    
                    deviceGuid = new UUID(deviceGuidRaw,  false);
                    System.out.println("device uuid: " + deviceGuid.toString());
                }
            }
        }
        catch(Exception ex) {
            throw new ExceptionWebServiceLayer(ex);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch(Exception exceptionInputStream) { }
            }
            if(httpConnection != null) {
                try {
                    httpConnection.close();
                } catch(Exception exceptionHttpConnection) {  }
            }
        }
        
        return deviceGuid;
    }
    
    public int Error(UUID deviceId, ErrorItem errorItem) throws ExceptionWebServiceLayer {     
        KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, errorItem.ApplicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(errorItem.DateCreated)),
            new KeyValuePair(QueryStringKeyType.DATA, errorItem.Data),
            new KeyValuePair(QueryStringKeyType.EVENT_NAME, errorItem.EventName),
            new KeyValuePair(QueryStringKeyType.AVAILABLE_FLASH_DRIVE_SIZE, Long.toString(errorItem.DeviceInformation.AvailableFlashDriveSize)),
            new KeyValuePair(QueryStringKeyType.AVAILABLE_MEMORY_SIZE, Long.toString(errorItem.DeviceInformation.AvailableMemorySize)),
            new KeyValuePair(QueryStringKeyType.BATTERY, Integer.toString(errorItem.DeviceInformation.Battery)),
            new KeyValuePair(QueryStringKeyType.ERROR_MESSAGE, errorItem.Error.getMessage()),
            new KeyValuePair(QueryStringKeyType.SCREEN_NAME, errorItem.ScreenName),
            new KeyValuePair(QueryStringKeyType.SESSION_ID, errorItem.SessionId.toString()),
            new KeyValuePair(QueryStringKeyType.VERSION, errorItem.Version)
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.Error, keyValuePairs);
    }
    
    public int Event(UUID deviceId, EventItem eventItem) throws ExceptionWebServiceLayer {     
        KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, eventItem.ApplicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(eventItem.DateCreated)),
            new KeyValuePair(QueryStringKeyType.DATA, eventItem.Data),
            new KeyValuePair(QueryStringKeyType.EVENT_TYPE, Integer.toString(eventItem.EventType)),
            new KeyValuePair(QueryStringKeyType.EVENT_NAME, eventItem.EventName),
            new KeyValuePair(QueryStringKeyType.LENGTH, Long.toString(eventItem.Length)),
            new KeyValuePair(QueryStringKeyType.SCREEN_NAME, eventItem.ScreenName),
            new KeyValuePair(QueryStringKeyType.SESSION_ID, eventItem.SessionId.toString()),
            new KeyValuePair(QueryStringKeyType.VERSION, eventItem.Version)
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.Event, keyValuePairs);
    }
    
    public int Feedback(UUID deviceId, FeedbackItem feedbackItem) throws ExceptionWebServiceLayer {
        KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, feedbackItem.ApplicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(feedbackItem.DateCreated)),
            new KeyValuePair(QueryStringKeyType.VERSION, feedbackItem.Version),
            new KeyValuePair(QueryStringKeyType.SCREEN_NAME, feedbackItem.ScreenName),
            new KeyValuePair(QueryStringKeyType.FEEDBACK, feedbackItem.Message),
            new KeyValuePair(QueryStringKeyType.FEEDBACK_RATING_TYPE, Integer.toString(feedbackItem.Rating)),
            new KeyValuePair(QueryStringKeyType.SESSION_ID, feedbackItem.SessionId.toString()),
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.Feedback, keyValuePairs); 
    }
    
    public int SystemError(UUID deviceId, AppActs.Plugin.Models.SystemError systemError) throws ExceptionWebServiceLayer {  
        KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, systemError.ApplicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(systemError.DateCreated)),
            new KeyValuePair(QueryStringKeyType.VERSION, systemError.Version),
            new KeyValuePair(QueryStringKeyType.ERROR_MESSAGE, systemError.Error.getMessage()),
            new KeyValuePair(QueryStringKeyType.PLATFORM_TYPE, Integer.toString(systemError.System.DeviceType)),
            new KeyValuePair(QueryStringKeyType.SYSTEM_VERSION, systemError.System.Version)
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.SystemError, keyValuePairs); 
    }
    
    public int User(UUID deviceId, AppActs.Plugin.Models.User user) throws ExceptionWebServiceLayer {
        KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, user.ApplicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DATE_CREATED, Utils.DateTimeFormat(user.DateCreated)),
            new KeyValuePair(QueryStringKeyType.VERSION, user.Version),
            new KeyValuePair(QueryStringKeyType.AGE, Integer.toString(user.Age)),
            new KeyValuePair(QueryStringKeyType.SEX_TYPE, Integer.toString(user.Sex)),
            new KeyValuePair(QueryStringKeyType.SESSION_ID, user.SessionId.toString()),
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.User, keyValuePairs); 
    }
    
    public int Location(UUID deviceId, UUID applicationId, DeviceLocation deviceLocation) 
        throws ExceptionWebServiceLayer {
        
        KeyValuePair[] keyValuePairs = new KeyValuePair[] {
            new KeyValuePair(QueryStringKeyType.LOCATION_LONGITUDE, Double.toString(deviceLocation.Longitude)),
            new KeyValuePair(QueryStringKeyType.LOCATION_LATITUDE, Double.toString(deviceLocation.Latitude)),
            new KeyValuePair(QueryStringKeyType.LOCATION_COUNTRY, deviceLocation.CountryName),
            new KeyValuePair(QueryStringKeyType.LOCATION_COUNTRY_CODE, deviceLocation.CountryCode),
            new KeyValuePair(QueryStringKeyType.LOCATION_ADMIN, deviceLocation.CountryAdminName),
            new KeyValuePair(QueryStringKeyType.LOCATION_ADMIN_CODE, deviceLocation.CountryAdminCode),
            new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, applicationId.toString()),
            new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString())
        };
        
        return this.webServiceCall(this.baseUrl, WebServices.Location, keyValuePairs); 
    }
    
	public int Upgrade(UUID deviceId, UUID applicationId, String version)
		throws ExceptionWebServiceLayer {
		
        KeyValuePair[] keyValuePairs = new KeyValuePair[] {
                new KeyValuePair(QueryStringKeyType.APPLICATION_GUID, applicationId.toString()),
                new KeyValuePair(QueryStringKeyType.DEVICE_GUID, deviceId.toString()),
                new KeyValuePair(QueryStringKeyType.VERSION, version)
            };
            
        return this.webServiceCall(this.baseUrl, WebServices.Upgrade, keyValuePairs);
	}
    
    private int webServiceCall
        (String baseUrl, String serviceUrl, KeyValuePair[] keyValuePair) throws ExceptionWebServiceLayer {
        
        int responseCode = WebServiceResponseCodeType.GeneralError;
        HttpConnection httpConnection = null;
        InputStream inputStream = null;
        
        try {
            if(HttpConnectionManager.HasNetworkCoverage()) {
                String url = baseUrl + serviceUrl;
                
                for(int i = 0; i < keyValuePair.length; i++) {
                    url = this.addToUrl(url, keyValuePair[i].Key, this.getValueNotNull(keyValuePair[i].Value));
                }
                System.out.println( url );
                url += HttpConnectionManager.GetConnectionStringByNetworkCoverage();
                
                httpConnection = (HttpConnection)Connector.open(url, Connector.READ, true);
                
                httpConnection.setRequestMethod("GET");
                
                if(httpConnection.getResponseCode() == HttpConnection.HTTP_OK) {
                    inputStream = httpConnection.openInputStream();
                    
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document documentXmlOutput = documentBuilder.parse(inputStream);
                    
                    Element elementRoot = documentXmlOutput.getDocumentElement();
                    elementRoot.normalize();
                    NodeList nodeList = elementRoot.getChildNodes();
                    Node node = nodeList.item(0);
                    
                    responseCode = Integer.parseInt(node.getFirstChild().getNodeValue());
                    System.out.println("response code: " + Integer.toString(responseCode));
                }
            }
        }
        catch(Exception ex) {
            throw new ExceptionWebServiceLayer(ex);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch(Exception exceptionInputStream) { }
            }
            if(httpConnection != null) {
                try {
                    httpConnection.close();
                } catch(Exception exceptionHttpConnection) {  }
            }
        }
        return responseCode;  
    }
    
    private String addToUrl(String url, String key, String value) {
        if(url.indexOf("?") > 0) {
            return url + "&" + key + "=" +  UrlUtf8Encoder.encode(value);
        }
        return  url + "?" + key + "=" +  UrlUtf8Encoder.encode(value);
    }
    
    private String getValueNotNull(String value) {
        if(value != null && !value.equals("null")) {
            return value;
        }
        return "";
    }
} 
