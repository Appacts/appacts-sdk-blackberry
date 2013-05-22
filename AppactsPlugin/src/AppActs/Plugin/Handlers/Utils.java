/*
 * Utils.java
 *
 * © AppActs, 2012
 * Confidential and proprietary.
 */

package AppActs.Plugin.Handlers;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.Date;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.DeviceInfo;

public class Utils {
  
    public static Date GetDateTimeNow() {
    	
    	//legacy: we have used GMT time from the device, in the past this would have made sense,
    	//if we wanted to track to find common time, however what we want to do is find releative time to the device so we know
    	//when they used it in their time zone, we dont care in what GMT it happend. Therefore we now going to return actual time of the device
        //Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        //calendar.setTime(new Date(System.currentTimeMillis()));
        //return calendar.getTime();
    	//TODO: remove above code after 2 versions. ver: 0.9.300.100
    	
    	return new Date(System.currentTimeMillis());
    }
    
    public static String DateTimeFormat(Date date) {
    	
    	//legacy: as above GMT is no longer relevant
        //DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        //calendar.setTime(date);
    	//TODO: remove above code after 2 versions. ver: 0.9.300.100
    	
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
    	
        return dateFormat.format(calendar, new StringBuffer(), null).toString(); 
    }
    
    public static int TimeOffSet() {
    	return TimeZone.getDefault().getRawOffset();
    }
    
    public static String generateUUID()
    {  
        String part1 = Integer.toHexString(UIDGenerator.getUID());
        String part2 = Integer.toHexString(DeviceInfo.getDeviceId()).substring(0, 4);
        String part3 = Integer.toHexString(UIDGenerator.getUID()).substring(0,4);
        String part4 = Integer.toHexString(UIDGenerator.getUID()).substring(0,4);
        String part5 = Utils.fromLong(System.currentTimeMillis()).substring(6, 12);
        
        Random rand = new Random(System.currentTimeMillis());
        String part6 = Integer.toString((rand.nextInt(999999) + 100000)).substring(0, 6);
       
        return part1
				.concat(part2)
				.concat(part3)
				.concat(part4)
				.concat(part5)
				.concat(part6);
    }
    
    public static String replaceAll(String source, String pattern, String replacement)
    {    

        //If source is null then Stop
        //and retutn empty String.
        if (source == null)
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        //Intialize Index to -1
        //to check agaist it later 
        int idx = 0;
        //Search source from 0 to first occurrence of pattern
        //Set Idx equal to index at which pattern is found.

        String workingSource = source;
        
        //Iterate for the Pattern till idx is not be -1.
        while ((idx = workingSource.indexOf(pattern, idx)) != -1)
        {
            //append all the string in source till the pattern starts.
            sb.append(workingSource.substring(0, idx));
            //append replacement of the pattern.
            sb.append(replacement);
            //Append remaining string to the String Buffer.
            sb.append(workingSource.substring(idx + pattern.length()));
            
            //Store the updated String and check again.
            workingSource = sb.toString();
            
            //Reset the StringBuffer.
            sb.delete(0, sb.length());
            
            //Move the index ahead.
            idx += replacement.length();
        }

        return workingSource;
    }
    
    private final static char[] HEX = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
      };

    public static String fromLong(long value) {
        char[] hexs;
        int i;
        int c;

        hexs = new char[16];
        for (i = 0; i < 16; i++) {
          c = (int)(value & 0xf);
          hexs[16-i-1] = HEX[c];
          value = value >> 4;
        }
        return new String(hexs);
    }
} 
