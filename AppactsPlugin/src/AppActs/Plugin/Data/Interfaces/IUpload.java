

package AppActs.Plugin.Data.Interfaces;

import javax.bluetooth.UUID;

import AppActs.Plugin.Models.ErrorItem;
import AppActs.Plugin.Models.EventItem;
import AppActs.Plugin.Models.ExceptionWebServiceLayer;
import AppActs.Plugin.Models.FeedbackItem;
import AppActs.Plugin.Models.Crash;
import AppActs.Plugin.Models.ScreenResolution;
import AppActs.Plugin.Models.SystemError;
import AppActs.Plugin.Models.User;
import AppActs.Plugin.Models.DeviceLocation;

public interface IUpload {
    int Crash(UUID deviceId, Crash crash) throws ExceptionWebServiceLayer;
    UUID Device(UUID applicationId, String model,  String osVersion, int deviceType,
        String carrier, String applicationVersion, int timeZoneOffset, String manufacturer, ScreenResolution screenRes, 
        String locale) throws ExceptionWebServiceLayer;
    int Error(UUID deviceId, ErrorItem errorItem) throws ExceptionWebServiceLayer;
    int Event(UUID deviceId, EventItem eventItem) throws ExceptionWebServiceLayer;
    int Feedback(UUID deviceId, FeedbackItem feedbackItem) throws ExceptionWebServiceLayer;
    int SystemError(UUID deviceId, SystemError systemError) throws ExceptionWebServiceLayer;
    int User( UUID deviceId, User user) throws ExceptionWebServiceLayer;
    int Location(UUID deviceId, UUID applicationId, DeviceLocation deviceLocation)
        throws ExceptionWebServiceLayer;
    int Upgrade(UUID deviceId, UUID applicationId, String version) throws ExceptionWebServiceLayer;
} 
