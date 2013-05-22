/*
 * ExceptionWebServiceLayer.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Models;

public final class ExceptionWebServiceLayer extends ExceptionDescriptive {
    
    public ExceptionWebServiceLayer(Exception ex) {    
        super(ex);
    }
    
    public String toString() {
        return "ExceptionWebServiceLayer: " + super.toString();
    }
} 
