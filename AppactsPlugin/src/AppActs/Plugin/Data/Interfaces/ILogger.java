/*
 * ILogger.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Data.Interfaces;

import javax.bluetooth.UUID;

import AppActs.Plugin.Models.Crash;
import AppActs.Plugin.Models.ErrorItem;
import AppActs.Plugin.Models.EventItem;
import AppActs.Plugin.Models.ExceptionDatabaseLayer;
import AppActs.Plugin.Models.FeedbackItem;
import AppActs.Plugin.Models.SystemError;

public interface ILogger {
    Crash GetCrash(UUID applicationId) throws ExceptionDatabaseLayer;
    ErrorItem GetErrorItem(UUID applicationId) throws ExceptionDatabaseLayer;
    EventItem GetEventItem(UUID applicationId) throws ExceptionDatabaseLayer;
    FeedbackItem GetFeedbackItem(UUID applicationId) throws ExceptionDatabaseLayer;
    SystemError GetSystemError(UUID applicationId) throws ExceptionDatabaseLayer;
    
    void Remove(EventItem eventItem)  throws ExceptionDatabaseLayer;
    void Remove(FeedbackItem feedbackItem) throws ExceptionDatabaseLayer;
    void Remove(ErrorItem errorItem) throws ExceptionDatabaseLayer;
    void Remove(SystemError systemError) throws ExceptionDatabaseLayer;
    void Remove(Crash crash) throws ExceptionDatabaseLayer;
    
    void Save(EventItem eventItem) throws ExceptionDatabaseLayer;
    void Save(ErrorItem errorItem) throws ExceptionDatabaseLayer;
    void Save(FeedbackItem feedbackItem) throws ExceptionDatabaseLayer;
    void Save(SystemError systemError) throws ExceptionDatabaseLayer;
    void Save(Crash crash) throws ExceptionDatabaseLayer;
} 
