

package jim.business.netsdk.util;

import java.util.Calendar;

/**
 * @author
 * @create 2022-03-22-11:13
 */
public class CommonUtil {

    //SDK时间解析
    public static String parseTime(int time)
    {
        int year=(time>>26)+2000;
        int month=(time>>22)&15;
        int day=(time>>17)&31;
        int hour=(time>>12)&31;
        int min=(time>>6)&63;
        int second=(time>>0)&63;
        String sTime=year+"-"+month+"-"+day+"-"+hour+":"+min+":"+second;
        return sTime;
    }
    
    public static long parseTimeMillis(int time) {
    	Calendar cal = Calendar.getInstance();
    	int year=(time>>26)+2000;
        int month=(time>>22)&15;
        int day=(time>>17)&31;
        int hour=(time>>12)&31;
        int min=(time>>6)&63;
        int second=(time>>0)&63;
    	cal.set(year, month-1, day, hour, min, second);
    	return cal.getTimeInMillis();
    }
}
