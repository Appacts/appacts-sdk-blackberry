/*
 * IData.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Data.Interfaces;


import net.rim.device.api.database.Database;

import AppActs.Plugin.Models.ExceptionDatabaseLayer;

public interface IData {
    void CreateDatabase() throws ExceptionDatabaseLayer;
    
    boolean ExistsDatabase();    
    void SetupSchema() throws ExceptionDatabaseLayer;
    boolean UpdateSchema(int pluginVersionNumericCurrent, int schemaVersionNumericOld) 
    	throws ExceptionDatabaseLayer;
  
    Database OpenReadWriteConnection() throws ExceptionDatabaseLayer;
    void CloseReadWriteConnection() throws ExceptionDatabaseLayer;
    
    Database OpenReadOnlyConnection() throws ExceptionDatabaseLayer;
    void CloseReadOnlyConnection() throws ExceptionDatabaseLayer;
    
    void Dispose() throws ExceptionDatabaseLayer;
} 
