
package AppActs.Plugin.Data;

import javax.bluetooth.UUID;

import AppActs.Plugin.Data.Interfaces.ILogger;
import AppActs.Plugin.Models.*;

import java.util.Date;

import net.rim.device.api.database.*;

public final class LoggerDB implements ILogger {
    
    private final Database databaseReadWrite;
    private final Database databaseReadOnly;
    
    public LoggerDB(Database databaseRw, Database databaseR) {    
    	this.databaseReadWrite = databaseRw;
    	this.databaseReadOnly = databaseR;
    }
    
    public Crash GetCrash(UUID applicationId) throws ExceptionDatabaseLayer {
        Crash crash = null;
        Statement statCrash = null;
        try {            
            statCrash = this.databaseReadOnly.createStatement("SELECT ID, applicationGuid, DateCreated, SessionId, Version FROM Crash WHERE applicationGuid = ?");

            statCrash.prepare();
            statCrash.bind(1, applicationId.toString());
            Cursor cursor = statCrash.getCursor();
            cursor.next();
            Row row = cursor.getRow();
            
            if(row != null) {
                crash = new Crash
                            (
                                row.getInteger(0), 
                                new UUID(row.getString(1), false), 
                                new Date(row.getLong(2)),
                                new UUID(row.getString(3), false),
                                row.getString(4)
                            );
            }
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        } finally {
        	try {
        		if(statCrash != null) {
        			statCrash.close();
        		}
			} catch (DatabaseException ex) {
				throw new ExceptionDatabaseLayer(ex);
			}
        }
        
        return crash;
    }
    
    public ErrorItem GetErrorItem(UUID applicationId)  throws ExceptionDatabaseLayer {
        ErrorItem errorItem = null;
        Statement statErrorItem = null;
        
        try {
            statErrorItem = this.databaseReadOnly.createStatement("SELECT ID, applicationGuid, DateCreated, "  
            		+ "Data, EventName, AvailableFlashDriveSize, AvailableMemorySize, Battery, NetworkCoverage, ErrorMessage, ErrorStackTrace, ErrorSource,  "
            		+ "ErrorData, ScreenName, SessionId, Version "
            		+ "FROM Error WHERE applicationGuid = ?");
            
            statErrorItem.prepare();
            statErrorItem.bind(1, applicationId.toString());
            Cursor cursor = statErrorItem.getCursor();
            cursor.next();
            Row row = cursor.getRow();
            
            if(row != null) {
                errorItem = new ErrorItem
                            (
                                row.getInteger(0),
                                new UUID(row.getString(1), false),
                                row.getString(13),
                                row.getString(3),
                                new DeviceGeneralInformation(row.getLong(5), row.getLong(6), row.getInteger(7), row.getInteger(8)),
                                row.getString(4),
                                new ExceptionDescriptive(row.getString(9)),
                                new Date(row.getLong(2)),
                                new UUID(row.getString(14), false),
                                row.getString(15)
                            );
            }
            
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        } finally {
        	try {
        		if(statErrorItem != null) {
        			statErrorItem.close();
        		}
			} catch (DatabaseException ex) {
				throw new ExceptionDatabaseLayer(ex);
			}
        }
        
        return errorItem;
    }
    
    public EventItem GetEventItem(UUID applicationId)  throws ExceptionDatabaseLayer {
        EventItem eventItem = null;
        Statement statEventItem = null;
        try {
        	statEventItem = this.databaseReadOnly.createStatement("SELECT ID, applicationGuid, DateCreated, Data, "
            		+ "Event, EventName, Length, ScreenName, SessionId, Version "
            		+ "FROM Event WHERE applicationGuid = ?");
            
            statEventItem.prepare();
            statEventItem.bind(1, applicationId.toString());
            Cursor cursor = statEventItem.getCursor();
            cursor.next();
            Row row = cursor.getRow();
            
            if(row != null) {
                eventItem = new EventItem
                            (
                                row.getInteger(0),
                                new UUID(row.getString(1), false),
                                row.getString(7),
                                row.getString(3),
                                row.getInteger(4),
                                row.getString(5),
                                row.getLong(6),
                                new Date(row.getLong(2)),
                                new UUID(row.getString(8), false),
                                row.getString(9)
                            );
            }
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        } finally {
        	try {
        		if(statEventItem != null) {
        			statEventItem.close();
        		}
			} catch (DatabaseException ex) {
				throw new ExceptionDatabaseLayer(ex);
			}
        }
        
        return eventItem;
    }
    
    public FeedbackItem GetFeedbackItem(UUID applicationId)  throws ExceptionDatabaseLayer {
        FeedbackItem feedbackItem = null;
        Statement statFeedback = null;
        try {            
        	statFeedback = this.databaseReadOnly.createStatement("SELECT ID, applicationGuid, DateCreated, ScreenName, "
            		+ "Feedback, FeedbackRating, SessionId, Version FROM Feedback WHERE applicationGuid = ?");
                
            statFeedback.prepare();
            statFeedback.bind(1, applicationId.toString());
            Cursor cursor = statFeedback.getCursor();
            cursor.next();
            Row row = cursor.getRow();
            
            if(row != null) {
                feedbackItem = new FeedbackItem
                            (
                                row.getInteger(0),
                                new UUID(row.getString(1), false),
                                row.getString(3),
                                row.getString(4),
                                row.getInteger(5),
                                new Date(row.getLong(2)),
                                new UUID(row.getString(6), false),
                                row.getString(7)
                            );
            }    
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        } finally {
        	try {
        		if(statFeedback != null) {
        			statFeedback.close();
        		}
			} catch (DatabaseException ex) {
				throw new ExceptionDatabaseLayer(ex);
			}
        }
        
        return feedbackItem;
    }
    
    public SystemError GetSystemError(UUID applicationId)  throws ExceptionDatabaseLayer {
        SystemError systemError = null;
        Statement statSystemError = null;
        
        try {
            statSystemError = this.databaseReadOnly.createStatement(
            	("SELECT ID, applicationGuid, DateCreated, ErrorMessage, Platform, SystemVersion, Version FROM SystemError WHERE applicationGuid = ?"));
                
            statSystemError.prepare();
            statSystemError.bind(1, applicationId.toString());
            
            Cursor cursor = statSystemError.getCursor();
            cursor.next();
            Row row = cursor.getRow();
            
            if(row != null) {
                systemError = new SystemError
                            (
                                row.getInteger(0),
                                new UUID(row.getString(1), false),
                                new ExceptionDescriptive(row.getString(3)),
                                new AnalyticsSystem(row.getInteger(4), row.getString(5)),
                                new Date(row.getLong(2)),
                                row.getString(6)
                            );
            }
        } catch (Exception ex) {
            throw new ExceptionDatabaseLayer(ex);
        } finally {
        	try {
        		if(statSystemError != null) {
        			statSystemError.close();
        		}
			} catch (DatabaseException ex) {
				throw new ExceptionDatabaseLayer(ex);
			}
        }
        
        return systemError;
    }
    
    public void Remove(EventItem eventItem) throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) {
    		Statement statEventItem = null;
	        try {
            
                statEventItem = databaseReadWrite.createStatement("DELETE FROM Event WHERE ID = ?");
                statEventItem.prepare();
                statEventItem.bind(1, eventItem.Id);
                statEventItem.execute();
	        } catch (Exception ex) {
	            throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
	        		if(statEventItem != null) {
	        			statEventItem.close();
	        		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
    	}
    }
    
    public void Remove(FeedbackItem feedbackItem)  throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) {   
    		Statement statFeedbackItem = null;
	        try {
                statFeedbackItem = databaseReadWrite.createStatement("DELETE FROM Feedback WHERE ID = ?");
                statFeedbackItem.prepare();
                statFeedbackItem.bind(1, feedbackItem.Id);
                statFeedbackItem.execute();
	        } catch (Exception ex) {
	            throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
	        		if(statFeedbackItem != null) {
	        			statFeedbackItem.close();
	        		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
    	}
    }
    
    public void Remove(ErrorItem errorItem) throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) {  
    		Statement statErrorItem = null;
	        try {
	        	statErrorItem = databaseReadWrite.createStatement("DELETE FROM Error WHERE ID = ?");
                statErrorItem.prepare();
                statErrorItem.bind(1, errorItem.Id);
                statErrorItem.execute();
	        } catch (Exception ex) {
	            throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
	        		if(statErrorItem != null) {
	        			statErrorItem.close();
	        		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
    	}  
    }
    
    public void Remove(SystemError systemError) throws ExceptionDatabaseLayer {
         synchronized(this.databaseReadWrite) {  
        	Statement statSytemError = null;
	        try {
                statSytemError = databaseReadWrite.createStatement("DELETE FROM SystemError WHERE ID = ?");
                statSytemError.prepare();
                statSytemError.bind(1, systemError.Id);
                statSytemError.execute();
	        } catch (Exception ex) {
	            throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
	        		if(statSytemError != null) {
	        			statSytemError.close();
	        		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
         }
    }
    
    public void Remove(Crash crash) throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) {    	
    		Statement statCrash = null;
	        try {
                statCrash = databaseReadWrite.createStatement("DELETE FROM Crash WHERE ID = ?");
                statCrash.prepare();
                statCrash.bind(1, Integer.toString(crash.Id));
                statCrash.execute();
	        } catch (Exception ex) {
	            throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
	        		if(statCrash != null) {
	        			statCrash.close();
	        		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
    	}
    }
    
    public void Save(EventItem eventItem) throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) {   
        	Statement statEventItem = null;    		
	        try {
                statEventItem = databaseReadWrite.createStatement   
                    ("INSERT INTO Event (applicationGuid, DateCreated, Data, Event, EventName, Length, ScreenName, SessionId, Version) VALUES (?,?,?,?,?,?,?,?,?)");
                    
                statEventItem.prepare();
                statEventItem.bind(1, eventItem.ApplicationId.toString());
                statEventItem.bind(2, eventItem.DateCreated.getTime());
                statEventItem.bind(3, eventItem.Data);
                statEventItem.bind(4, eventItem.EventType);
                statEventItem.bind(5, eventItem.EventName);
                statEventItem.bind(6, eventItem.Length);
                statEventItem.bind(7, eventItem.ScreenName);
                statEventItem.bind(8, eventItem.SessionId.toString());
                statEventItem.bind(9, eventItem.Version);
                
                statEventItem.execute();
	        } catch (Exception ex) {
	            throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
	        		if(statEventItem != null) {
	        			statEventItem.close();
	        		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
    	}
    }
    
    public void Save(ErrorItem errorItem)  throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) {       	
    		Statement statErrorItem = null;
	        try {
                statErrorItem = databaseReadWrite.createStatement   
                    ( "INSERT INTO Error (applicationGuid, DateCreated, ErrorMessage, ErrorStackTrace, ErrorSource, ErrorData, "
                    + "Data, EventName, AvailableFlashDriveSize, AvailableMemorySize, Battery, NetworkCoverage, ScreenName, SessionId, Version)"
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
                    
                statErrorItem.prepare();
                statErrorItem.bind(1, errorItem.ApplicationId.toString());
                statErrorItem.bind(2, errorItem.DateCreated.getTime());
                statErrorItem.bind(3, errorItem.Error.getMessage());
                statErrorItem.bind(4,  errorItem.Error.StackTrace);
                statErrorItem.bind(5,  errorItem.Error.Source);
                statErrorItem.bind(6, errorItem.Error.Data);
                statErrorItem.bind(7, errorItem.Data);
                statErrorItem.bind(8, errorItem.EventName); 
                statErrorItem.bind(9, errorItem.DeviceInformation.AvailableFlashDriveSize); 
                statErrorItem.bind(10, errorItem.DeviceInformation.AvailableMemorySize); 
                statErrorItem.bind(11, errorItem.DeviceInformation.Battery); 
                statErrorItem.bind(12, errorItem.DeviceInformation.NetworkCoverage); 
                statErrorItem.bind(13, errorItem.ScreenName); 
                statErrorItem.bind(14, errorItem.SessionId.toString());   
                statErrorItem.bind(15, errorItem.Version);     
                statErrorItem.execute();
                
	        } catch (Exception ex) {
	            throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
	        		if(statErrorItem != null) {
	        			statErrorItem.close();
	        		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
    	} 
    }
    
    public void Save(FeedbackItem feedbackItem)  throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) {   
    		Statement statFeedbackItem = null;
	        try {
                statFeedbackItem = databaseReadWrite.createStatement   
                    ( "INSERT INTO Feedback (applicationGuid, DateCreated, ScreenName, Feedback, FeedbackRating, SessionId, Version)"
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)" );
                
                statFeedbackItem.prepare();
                statFeedbackItem.bind(1, feedbackItem.ApplicationId.toString());
                statFeedbackItem.bind(2, feedbackItem.DateCreated.getTime());
                statFeedbackItem.bind(3, feedbackItem.ScreenName);
                statFeedbackItem.bind(4, feedbackItem.Message);
                statFeedbackItem.bind(5, feedbackItem.Rating);
                statFeedbackItem.bind(6, feedbackItem.SessionId.toString());
                statFeedbackItem.bind(7, feedbackItem.Version);
                    
                statFeedbackItem.execute();
	        } catch (Exception ex) {
	            throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
	        		if(statFeedbackItem != null) {
	        			statFeedbackItem.close();
	        		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
    	}
    }
    
    public void Save(SystemError systemError) throws ExceptionDatabaseLayer {
        synchronized(this.databaseReadWrite) {  
        	Statement statSystemError = null;
	        try {
                statSystemError = databaseReadWrite.createStatement   
                    ( "INSERT INTO SystemError (applicationGuid, DateCreated, ErrorMessage, ErrorStackTrace, ErrorSource, ErrorData, "
                    + "Platform,  SystemVersion, Version)"
                        + "VALUES (?,?,?,?,?,?,?,?,?)" );
                
                statSystemError.prepare();
                statSystemError.bind(1, systemError.ApplicationId.toString());
                statSystemError.bind(2, systemError.DateCreated.getTime());
                statSystemError.bind(3, systemError.Error.getMessage());
                statSystemError.bind(4, systemError.Error.StackTrace);
                statSystemError.bind(5, systemError.Error.Source);
                statSystemError.bind(6, systemError.Error.Data);
                statSystemError.bind(7, systemError.System.DeviceType);
                statSystemError.bind(8, systemError.System.Version);   
                statSystemError.bind(9, systemError.Version); 
                    
                statSystemError.execute();
	        } catch (Exception ex) {
	            throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
	        		if(statSystemError != null) {
	        			statSystemError.close();
	        		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
        }
    }
    
    public void Save(Crash crash) throws ExceptionDatabaseLayer {
    	synchronized(this.databaseReadWrite) {
    		Statement statCrash = null;
	        try {
                statCrash = databaseReadWrite.createStatement
                    ( "INSERT INTO Crash (applicationGuid, DateCreated, SessionId, Version) VALUES (?,?,?,?)" );
                    
                statCrash.prepare();
                statCrash.bind(1, crash.ApplicationId.toString());
                statCrash.bind(2, crash.DateCreated.getTime());
                statCrash.bind(3, crash.SessionId.toString());
                statCrash.bind(4, crash.Version);
                
                statCrash.execute();
	        } catch (Exception ex) {
	            throw new ExceptionDatabaseLayer(ex);
	        } finally {
	        	try {
	        		if(statCrash != null) {
	        			statCrash.close();
	        		}
				} catch (DatabaseException ex) {
					throw new ExceptionDatabaseLayer(ex);
				}
	        }
    	}
    }
} 
