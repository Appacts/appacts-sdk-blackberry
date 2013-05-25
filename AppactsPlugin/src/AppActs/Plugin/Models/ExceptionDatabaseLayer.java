
package AppActs.Plugin.Models;

public final class ExceptionDatabaseLayer extends ExceptionDescriptive {
    
    public ExceptionDatabaseLayer(Exception ex) {    
        super(ex);
    }
    
    public String toString() {
        return "ExceptionDatabaseLayer: " + super.toString();
    }
    
} 
