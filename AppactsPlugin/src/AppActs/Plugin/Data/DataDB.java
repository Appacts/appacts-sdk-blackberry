

package AppActs.Plugin.Data;


import java.util.Vector;

import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.DatabaseSecurityOptions;
import net.rim.device.api.database.Statement;
import net.rim.device.api.io.URI;
import AppActs.Plugin.Data.Interfaces.IData;
import AppActs.Plugin.Models.ExceptionDatabaseLayer;

public class DataDB implements IData {
    
    private final String connectionString;
    private final static String baseConnectionString = "file:///SDCard/Databases/";
    private Database databaseReadWrite = null;
    private Database databaseReadOnly = null;
    private final Object objectInstanceLock = new Object();
    
    public DataDB(String connectionString) {
        this.connectionString = connectionString;
    }
    
    public void CreateDatabase() throws ExceptionDatabaseLayer {
        try {
            URI uri = URI.create(baseConnectionString + this.connectionString);
            DatabaseSecurityOptions databaseSecurityOptions = new DatabaseSecurityOptions(true);
            databaseReadWrite = DatabaseFactory.create(uri, databaseSecurityOptions);
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        } 
    }
    
    public boolean ExistsDatabase() {
        boolean exists = false;
        
        try {
            this.OpenReadOnlyConnection();
            exists = true;
        } catch(Exception ex) {
            exists = false;
        }
        return exists;
    }
    
    public void SetupSchema() throws ExceptionDatabaseLayer {
    	
        Database databaseSetup = this.OpenReadWriteConnection();
        
        String[] sqlBase = new String[] {
        	"CREATE TABLE 'Crash' ( 'ID' INTEGER PRIMARY KEY, 'applicationGuid' NVARCHAR(36),  'DateCreated' TIMESTAMP, 'Version' NVARCHAR(64))" ,
        	
        	"CREATE TABLE 'Error' ( 'ID' INTEGER PRIMARY KEY, 'applicationGuid' VARCHAR(36),  'DateCreated' TIMESTAMP," +
            " 'Data' NVARCHAR(256), 'EventName' NVARCHAR(256),  'AvailableFlashDriveSize' INTEGER, " +
            " 'AvailableMemorySize' INTEGER, 'Battery' INTEGER, 'NetworkCoverage' INTEGER," +
            " 'ErrorMessage' NVARCHAR(1024), 'ErrorStackTrace' TEXT, 'ErrorSource' NVARCHAR(1024), 'ErrorData' NVARCHAR(256)," +
             " 'ScreenName' NVARCHAR(256), 'Version' NVARCHAR(64) " +
             "  )",
             
             "CREATE TABLE 'Event' ( 'ID' INTEGER PRIMARY KEY, 'applicationGuid' NVARCHAR(36), 'DateCreated' TIMESTAMP," +
                        " 'Data' NVARCHAR(256), 'Event' INTEGER, 'EventName' NVARCHAR(256), 'Length' INTEGER, 'ScreenName' NVARCHAR(256), 'Version' NVARCHAR(64))",
                        
             "CREATE TABLE 'Feedback' ( 'ID' INTEGER PRIMARY KEY, 'applicationGuid' NVARCHAR(36), 'DateCreated' TIMESTAMP," +
                        " 'ScreenName' NVARCHAR(256), 'Feedback' TEXT, 'FeedbackRating' INTEGER, 'Version' NVARCHAR(64))",
                        
             "CREATE TABLE 'SystemError' ( 'ID' INTEGER PRIMARY KEY, 'applicationGuid' NVARCHAR(36),  'DateCreated' TIMESTAMP," +
                        " 'ErrorMessage' NVARCHAR(1024), 'ErrorStackTrace' TEXT, 'ErrorSource' NVARCHAR(1024), 'ErrorData' NVARCHAR(256)," +
                        " 'Platform' INTEGER, 'SystemVersion' NVARCHAR(64), 'Version' NVARCHAR(64) )",
                        
            "CREATE TABLE 'User' (  'ID' INTEGER PRIMARY KEY, 'applicationGuid' NVARCHAR(36),  'DateCreated' TIMESTAMP," +
            " 'Age' INTEGER, 'Sex' INTEGER, 'Status' INTEGER, 'Version' NVARCHAR(64))",
            
            "CREATE TABLE 'Application' ( 'applicationGuid' NVARCHAR(36), 'DateCreated' TIMESTAMP, 'ApplicationState' INTEGER, 'OptStatus' INTEGER)",
            
            "CREATE TABLE 'Device' ( 'DeviceGuid' NVARCHAR(36), 'DateCreated' TIMESTAMP, " + 
                      " 'Status' INTEGER, 'Latitude' NUMERIC(9,6), 'Longitude' NUMERIC(9, 6)," +
                      " 'CountryName' NVARCHAR(256), 'CountryCode' NVARCHAR(256), 'CountryAdminAreaName' NVARCHAR(256), 'CountryAdminAreaCode' NVARCHAR(256))"
        };
        
        Statement statDevice = null;
        try {
        	for(int i = 0; i < sqlBase.length; i++) {
                statDevice = databaseSetup.createStatement(sqlBase[i]);
                
                statDevice.prepare();
                statDevice.execute();
                statDevice.close();
        	}
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        } finally {
        	try {
				statDevice.close();
			} catch (DatabaseException ex) {
				throw new ExceptionDatabaseLayer(ex);
			}
        }
    }
    
    public boolean UpdateSchema(int pluginVersionNumericCurrent, int schemaVersionNumericOld) 
    	throws ExceptionDatabaseLayer {
    	
    	boolean upgraded = false;
    	
    	try {   
	    	if(pluginVersionNumericCurrent != schemaVersionNumericOld) {
	    		
	    		Vector sqlAlters = null;
	    		
		    	if(schemaVersionNumericOld == -1) {
		    		sqlAlters = this.upgradeSchemaAddSessionAndMeta();
		    	}
		    	
		    	if(sqlAlters != null) {
			    	Database databaseUpdate = this.OpenReadWriteConnection();
			    	
			    	synchronized(databaseUpdate) {
			    		Statement statement = null;
						try {
							for(int i = 0; i < sqlAlters.size(); i++) {
				                statement = databaseUpdate.createStatement((String)sqlAlters.elementAt(i));
				                statement.prepare();
				                statement.execute();
							}	
							upgraded = true;
						} catch(Exception ex) {
							throw new ExceptionDatabaseLayer(ex);
						} finally {
							try {
								statement.close();
							} catch (DatabaseException ex) {
								throw new ExceptionDatabaseLayer(ex);
							}
						}
			    	}
		    	} else {
		    		upgraded = true;	
		    	}
	    	}
	    	
		} catch(Exception ex) {
			throw new ExceptionDatabaseLayer(ex);
		}  	
		
		return upgraded;
    }
    
    public Database OpenReadWriteConnection() throws ExceptionDatabaseLayer {
    	try {
    		if(this.databaseReadWrite == null) {
		    	URI uri = URI.create(baseConnectionString + this.connectionString); 
		    	this.databaseReadWrite = DatabaseFactory.open(uri, false);
    		}
    	} catch(Exception ex) {
    		throw new ExceptionDatabaseLayer(ex);
    	}
    	return this.databaseReadWrite;
    }
    
    public void CloseReadWriteConnection() throws ExceptionDatabaseLayer {
    	try {
    		if(this.databaseReadWrite != null) {
    			this.databaseReadWrite.close();
    			this.databaseReadWrite = null;
    		}
    	} catch(Exception ex) {
    		throw new ExceptionDatabaseLayer(ex);
    	}
    }
    
    public Database OpenReadOnlyConnection() throws ExceptionDatabaseLayer {
    	try {
    		if(this.databaseReadOnly == null) {
		    	URI uri = URI.create(baseConnectionString + this.connectionString); 
		    	this.databaseReadOnly = DatabaseFactory.open(uri, true);
    		}
    	} catch(Exception ex) {
    		throw new ExceptionDatabaseLayer(ex);
    	}
    	return this.databaseReadOnly;
    }
    
    public void CloseReadOnlyConnection() throws ExceptionDatabaseLayer {
    	try {
    		if(this.databaseReadOnly != null) {
    			this.databaseReadOnly.close();
    			this.databaseReadOnly = null;
    		}
    	} catch(Exception ex) {
    		throw new ExceptionDatabaseLayer(ex);
    	}
    }
    
    public void Dispose() throws ExceptionDatabaseLayer {
		try {
			synchronized (this.objectInstanceLock) {
				this.CloseReadWriteConnection();
				this.CloseReadOnlyConnection();
			}
		}
		catch(Exception ex) { 
			throw new ExceptionDatabaseLayer(ex);
		}
    }
    
    private Vector upgradeSchemaAddSessionAndMeta() 
    	throws ExceptionDatabaseLayer {
    	
    	Vector sqlAlters = new Vector();
    	sqlAlters.addElement("ALTER TABLE 'Crash' ADD 'SessionId' NVARCHAR(36)");
    	sqlAlters.addElement("ALTER TABLE 'Error' ADD 'SessionId' NVARCHAR(36)");
		sqlAlters.addElement("ALTER TABLE 'Event' ADD 'SessionId' NVARCHAR(36)");
		sqlAlters.addElement("ALTER TABLE 'Feedback' ADD 'SessionId' NVARCHAR(36)");
		sqlAlters.addElement("ALTER TABLE 'User' ADD 'SessionId' NVARCHAR(36)");
		sqlAlters.addElement("ALTER TABLE Application ADD SessionId NVARCHAR(36)");
		sqlAlters.addElement("ALTER TABLE Application ADD Version NVARCHAR(64)");
		sqlAlters.addElement("ALTER TABLE Application ADD Upgraded BOOLEAN");
		sqlAlters.addElement("CREATE TABLE Meta ('schemaVersionNumeric' INTEGER)");
    	
    	return sqlAlters;
    }
    
} 
