
package AppActs.Plugin.Data;

import java.util.Date;

import javax.bluetooth.UUID;

import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.Row;
import net.rim.device.api.database.Statement;
import AppActs.Plugin.Data.Interfaces.ISettings;
import AppActs.Plugin.Models.ApplicationMeta;
import AppActs.Plugin.Models.DeviceLocation;
import AppActs.Plugin.Models.ExceptionDatabaseLayer;
import AppActs.Plugin.Models.User;
import AppActs.Plugin.Models.PluginMeta;

public final class SettingsDB implements ISettings {
    
	private final Database databaseReadWrite;
	private final Database databaseReadOnly;
    
    public SettingsDB(Database databaseRw, Database databaseR) {    
        this.databaseReadWrite = databaseRw;
        this.databaseReadOnly = databaseR;
    }
    
    public ApplicationMeta LoadApplication(UUID applicationId) throws ExceptionDatabaseLayer {
        ApplicationMeta applicationState = null;
        Statement statApplication = null;
        
        try {
            statApplication = this.databaseReadOnly.createStatement
                (("SELECT ApplicationGuid, ApplicationState, DateCreated, Version, OptStatus, SessionId, Upgraded FROM Application WHERE ApplicationGuid = ?"));
      	
            statApplication.prepare();     
            statApplication.bind(1, applicationId.toString());
            
            Cursor cursor = statApplication.getCursor();
            cursor.next();
            Row row = cursor.getRow();
            
            if(row != null) {
          
                applicationState = new ApplicationMeta
                            (
                            	new UUID(row.getString(0), false),
                                row.getInteger(1),
                                new UUID(row.getString(5), false),
                                new Date(row.getLong(2)),
                                row.getString(3),
                                row.getBoolean(6),
                                row.getInteger(4)
                            );
                
            }   
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        } finally {
        	try {
        		if(statApplication != null) {
        			statApplication.close();
        		}
			} catch (DatabaseException ex) {
				throw new ExceptionDatabaseLayer(ex);
			}
        }
        
        return applicationState;
    }
    
    public void Save(ApplicationMeta applicationMeta) throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) { 	
    		Statement statApplicationCreate = null;
	        try {
	            statApplicationCreate = databaseReadWrite.createStatement
	            ( "INSERT INTO Application (applicationGuid, ApplicationState, DateCreated, OptStatus) VALUES (?,?,?,?)" );
	            
		        statApplicationCreate.prepare();
		           
		        statApplicationCreate.bind(1, applicationMeta.Id.toString());
		        statApplicationCreate.bind(2, applicationMeta.State);              
		        statApplicationCreate.bind(3, applicationMeta.DateCreated.getTime());
		        statApplicationCreate.bind(4, applicationMeta.OptStatus);
		        statApplicationCreate.execute();
	        } catch(Exception ex) {
	        	throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
	        		if(statApplicationCreate != null) {
	        			statApplicationCreate.close();
	        		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
        }
    }
    
    public void Update(ApplicationMeta applicationMeta) throws ExceptionDatabaseLayer {
       synchronized(this.databaseReadWrite) {  	
    	   Statement statApplication = null;
		    try { 
		        statApplication = databaseReadWrite.createStatement   
		             ( "UPDATE Application SET ApplicationState = ?, SessionId = ?, Upgraded = ?, OptStatus = ?, Version = ? WHERE ApplicationGuid = ?");
		        
		        statApplication.prepare();    
		        statApplication.bind(1, applicationMeta.State);
		        statApplication.bind(2, applicationMeta.SessionId.toString());
		        statApplication.bind(3, applicationMeta.Upgraded);
		        statApplication.bind(4, applicationMeta.OptStatus);
		        statApplication.bind(5, applicationMeta.Version);
		        statApplication.bind(6, applicationMeta.Id.toString());
		        
		        statApplication.execute();
		    } catch (Exception ex) {
		        throw new ExceptionDatabaseLayer(ex);
		    } finally {
		    	try {
		    		if(statApplication != null) {
		    			statApplication.close();
		    		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
		    }
       }
    }

    public UUID GetDeviceId() throws ExceptionDatabaseLayer {
        UUID deviceId = null;
        Statement statDevice = null;
        try {
            statDevice = this.databaseReadOnly.createStatement("SELECT DeviceGuid FROM Device");
                
            statDevice.prepare();
            Cursor cursor = statDevice.getCursor();
            cursor.next();
            Row row = cursor.getRow();
            
            if(row != null) {
                deviceId = new UUID(row.getString(0), false);
            }
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        } finally {
        	try {
        		if(statDevice != null) {
        			statDevice.close();
        		}
			} catch (DatabaseException ex) {
				throw new ExceptionDatabaseLayer(ex);
			}
        }
        return deviceId;
    }
    
    public DeviceLocation GetDeviceLocation(int statusType) throws ExceptionDatabaseLayer {
        DeviceLocation deviceLocation = null;
        Statement statDevice = null;
        
        try {
        	statDevice = this.databaseReadOnly.createStatement
                ("SELECT Latitude, Longitude, CountryName, CountryCode, CountryAdminAreaName, CountryAdminAreaCode, DateCreated FROM Device WHERE Status = ?");
                
            statDevice.prepare();
            statDevice.bind(1, statusType);
            Cursor cursor = statDevice.getCursor();
            cursor.next();
            Row row = cursor.getRow();
            
            if(row != null) {
                deviceLocation = new DeviceLocation
                            (
                                row.getDouble(0),
                                row.getDouble(1),
                                row.getString(2),
                                row.getString(3),
                                row.getString(4),
                                row.getString(5),
                                new Date(row.getLong(6))
                            );
            }
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        } finally {
        	try {
        		if(statDevice != null) {
        			statDevice.close();
        		}
			} catch (DatabaseException ex) {
				throw new ExceptionDatabaseLayer(ex);
			}
        }
        
        return deviceLocation;
    }
    
    public User GetUser(UUID applicationId, int statusType) throws ExceptionDatabaseLayer {
        User user = null;
        Statement statUser = null;
        
        try {
            statUser = this.databaseReadOnly.createStatement
                ("SELECT ID, applicationGuid, DateCreated, Age, Sex, Status, SessionId, Version FROM User WHERE ApplicationGuid = ? AND (0 = ? OR Status = ?)");
            
            statUser.prepare();  
            statUser.bind(1, applicationId.toString());
            statUser.bind(2, statusType);
            statUser.bind(3, statusType);
            Cursor cursor = statUser.getCursor();
            cursor.next();
            Row row = cursor.getRow();
            
            if(row != null) {
                user = new User
                            (
                                row.getInteger(0),
                                row.getInteger(3),
                                row.getInteger(4),
                                row.getInteger(5),
                                new UUID(row.getString(1), false),
                                new Date(row.getLong(2)),
                                new UUID(row.getString(6), false),
                                row.getString(7)
                            );
            }
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        } finally {
        	try {
        		if(statUser != null) {
        			statUser.close();
        		}
			} catch (DatabaseException ex) {
				throw new ExceptionDatabaseLayer(ex);
			}
        }
        
        return user;
    }
    
    public void Save(User user) throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) {
    		Statement statUser = null;
		    try {
	            statUser = this.databaseReadWrite.createStatement   
	                 ( "INSERT INTO User (ApplicationGuid, DateCreated, Age, Sex, Status, Version, SessionId)"
	                    + " VALUES (?,?,?,?,?,?,?)" );
	                    
	            statUser.prepare();    
	            statUser.bind(1, user.ApplicationId.toString());
	            statUser.bind(2, user.DateCreated.getTime());
	            statUser.bind(3, user.Age);
	            statUser.bind(4, user.Sex);
	            statUser.bind(5, user.Status);
	            statUser.bind(6, user.Version);
	            statUser.bind(7, user.SessionId.toString());
	            
	            statUser.execute();
		    } catch (Exception ex) {
		        throw new ExceptionDatabaseLayer(ex);
		    } finally {
		    	try {
		    		if(statUser != null) {
		    			statUser.close();
		    		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
		    }
    	}
    }
    
    public void Update(User user, int statusType) throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) {
    		Statement statUser = null;
		    try {  
	            statUser = this.databaseReadWrite.createStatement( "UPDATE User SET Status = ? WHERE Id = ?" );
	            
	            statUser.prepare();
	            statUser.bind(1, statusType);
	            statUser.bind(2, user.Id);
	            
	            statUser.execute();
		    } catch (Exception ex) {
		        throw new ExceptionDatabaseLayer(ex);
		    } finally{
		    	try {
		    		if(statUser != null) {
		    			statUser.close();
		    		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
		    }
    	}
    }
    
    public void Save(DeviceLocation deviceLocation, int statusType) throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) {
    		Statement statDevice = null;
	        try {
	            statDevice = this.databaseReadWrite.createStatement   
	                 ( "UPDATE Device SET Latitude = ?, Longitude = ?, CountryName = ?, CountryCode = ?, CountryAdminAreaName = ?, CountryAdminAreaCode = ?, Status = ?" );
	            
	            statDevice.prepare();    
	            statDevice.bind(1, deviceLocation.Latitude);
	            statDevice.bind(2, deviceLocation.Longitude);
	            statDevice.bind(3, deviceLocation.CountryName);
	            statDevice.bind(4, deviceLocation.CountryCode);
	            statDevice.bind(5, deviceLocation.CountryAdminName);
	            statDevice.bind(6, deviceLocation.CountryAdminCode);
	            statDevice.bind(7, statusType);
	            statDevice.execute();
	        } catch (Exception ex) {
	            throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
		    		if(statDevice != null) {
		    			statDevice.close();
		    		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
    	}
    }

    public void SaveDeviceId(UUID deviceId, Date dateCreated) throws ExceptionDatabaseLayer {
 	    synchronized(this.databaseReadWrite) { 
 	    	Statement statDevice = null;
		    try {  

	            statDevice = databaseReadWrite.createStatement   
	                 ( "INSERT INTO Device (DeviceGuid, DateCreated, Status, Latitude, Longitude, CountryName, CountryCode, CountryAdminAreaName, CountryAdminAreaCode)"
	                    + "VALUES (?,?,0,0,0, null, null, null, null)" );
	                    
	            statDevice.prepare();   
	            statDevice.bind(1, deviceId.toString());
	            statDevice.bind(2, dateCreated.getTime());
	            statDevice.execute();
		    } catch (Exception ex) {
		        throw new ExceptionDatabaseLayer(ex);
		    } finally {
		    	try {
		    		if(statDevice != null) {
		    			statDevice.close();
		    		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
		    }
 	    }
    }

	public PluginMeta LoadPlugin() throws ExceptionDatabaseLayer {
    	PluginMeta pluginMeta = null;
    	Statement statPluginMeta = null;
    	
    	try { 		
            statPluginMeta = this.databaseReadOnly.createStatement("SELECT schemaVersionNumeric FROM Meta");
            
	        statPluginMeta.prepare();
	        Cursor cursor = statPluginMeta.getCursor();
	        cursor.next();
	        Row row = cursor.getRow();
	        
	        if(row != null) {
	        	pluginMeta = new PluginMeta(row.getInteger(0));
	        } 
    	} catch(Exception ex) {
    		throw new ExceptionDatabaseLayer(ex);
    	} finally {
    		try {
	    		if(statPluginMeta != null) {
	    			statPluginMeta.close();
	    		}
			} catch (DatabaseException ex) {
				throw new ExceptionDatabaseLayer(ex);
			}
    	}
    	
    	return pluginMeta;
	}

	public void Save(AppActs.Plugin.Models.PluginMeta pluginMeta) throws ExceptionDatabaseLayer {
		synchronized(this.databaseReadWrite) {
			Statement statPluginMeta = null;
		    try {
	            statPluginMeta = this.databaseReadWrite.createStatement
	            ( "INSERT INTO Meta (schemaVersionNumeric) VALUES (?)" );
	            
		        statPluginMeta.prepare();
		           
		        statPluginMeta.bind(1, pluginMeta.SchemaVersionNumeric);
		        statPluginMeta.execute();
		    } catch(Exception ex) {
		    	throw new ExceptionDatabaseLayer(ex);
		    } finally {
		    	try {
		    		if(statPluginMeta != null) {
		    			statPluginMeta.close();
		    		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
		    }
		}
	}

	public void Update(PluginMeta pluginMeta) throws ExceptionDatabaseLayer {	
		synchronized(this.databaseReadWrite) {		
			Statement statPluginMeta = null;
	        try {
	            statPluginMeta = this.databaseReadWrite.createStatement
	            ( "UPDATE Meta SET schemaVersionNumeric = ?" );
	            
		        statPluginMeta.prepare();
		        statPluginMeta.bind(1, pluginMeta.SchemaVersionNumeric);
		        statPluginMeta.execute();
	        } catch(Exception ex) {
	        	throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
		    		if(statPluginMeta != null) {
		    			statPluginMeta.close();
		    		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
	    }
	}
} 
