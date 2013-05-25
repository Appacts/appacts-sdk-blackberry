
package AppActs.Plugin.Models;

public class ExceptionDescriptive extends Exception {
    
    public final String StackTrace;
    public final String Data;
    public final String Source;
    
    public ExceptionDescriptive(String message) {    
        super(message);
        
        this.StackTrace = "";
        this.Data = "";
        this.Source = "";
    }
    
    public ExceptionDescriptive(Exception ex) {  
        super(ex.getMessage());
        
        this.Source = ex.toString();
        this.StackTrace = "";
        this.Data = "";
    }
    
    public ExceptionDescriptive(Exception ex, String data) {  
        super(ex.getMessage());

        this.Source = ex.toString();
        this.Data = data;
        this.StackTrace = "";
    }

    public ExceptionDescriptive(String message, String stackTrace, String source, String data) {    
        super(message);
        
        this.StackTrace = stackTrace;
        this.Source = source;
        this.Data = data;
    }
} 
