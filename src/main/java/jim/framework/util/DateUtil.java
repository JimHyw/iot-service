package jim.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理
 */
public class DateUtil {

	/**
	 * 年份格式(yyyy)
	 */
	public final static String YEAR_PATTERN = "yyyy";
	/**
	 * 年月格式(yyyy-MM)
	 */
	public final static String YEAR_MONTH_PATTERN = "yyyy-MM";
	/**
	 * 时间格式(yyyy-MM-dd)
	 */
	public final static String DATE_PATTERN = "yyyy-MM-dd";

	/**
	 * 时间格式(yyyy/MM/dd)
	 */
	public final static String DATE_PATTERN_SLASH = "yyyy/MM/dd";

	/**
	 * 时间格式(yyyy-MM-dd HH:mm:ss)
	 */
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 数据库时间格式(yyyy-MM-dd HH:mm:ss.SSS)，对应datetime
	 */
	public final static String MYSQL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * 时间格式(yyyy年M月dd日 ah:mm:ss) 代码生成器使用
	 */
	public final static String DATE_TIME_CHN_PATTERN = "yyyy年M月dd日 ah:mm:ss";
	
	/** 
	* @Fields DATE_PATTERN_RFC3339 : rfc3339格式
	*/ 
	public final static String DATE_PATTERN_RFC3339 = "yyyy-MM-dd'T'HH:mm:ssXXX";
	/** 
	 * UTC格式化时间--监控使用
	 */ 
	public final static String DATE_PATTERN_UTC = "yyyyMMdd'T'HHmmss'Z'";

	/**
	 * @Fields ZONE_ID : 时区
	 */
	public final static String ZONE_ID = "Asia/Shanghai";

	/**
	 * Date转为时间格式(yyyy-MM-dd)的字符串
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		return format(date, DATE_PATTERN);
	}

	/**
	 * Date转为时间格式(yyyy-MM)的字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getYearMonth(Date date) {
		return format(date, YEAR_MONTH_PATTERN);
	}

	/**
	 * Long转为Date(yyyy)
	 * 
	 * @param m
	 * @return
	 */
	public static Date getYear(Long m) {
		if (m != null) {
			SimpleDateFormat df = new SimpleDateFormat(YEAR_PATTERN);
			String time = df.format(new Date(m));
			Date dateTime = null;
			try {
				dateTime = df.parse(time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return dateTime;
		}
		return null;
	}
	
	/**
	 * Long转为Date(yyyy-MM)
	 * 
	 * @param m
	 * @return
	 */
	public static Date getYearMonth(Long m) {
		if (m != null) {
			SimpleDateFormat df = new SimpleDateFormat(YEAR_MONTH_PATTERN);
			String time = df.format(new Date(m));
			Date dateTime = null;
			try {
				dateTime = df.parse(time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return dateTime;
		}
		return null;
	}

	/**
	 * Date根据格式字符串转为字符串
	 * @param date
	 * @param pattern
	 * @return 时间字符串
	 */
	public static String format(Date date, String pattern) {
		if (date != null) {
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			return df.format(date);
		}
		return null;
	}

	/**
	 * 当前日期加天数
	 * @param startDate
	 * @param days
	 * @return
	 */
	public static Date addDays(Date startDate, int days) {
		Calendar cl = Calendar.getInstance();
		cl.setTime(startDate);
		cl.add(Calendar.DATE, days);
		return cl.getTime();
	}

	/**
	 * 当前时间加分钟
	 * @param startDate
	 * @param days
	 * @return
	 */
	public static Date addMinutes(Date startDate, int minutes) {
		Calendar cl = Calendar.getInstance();
		cl.setTime(startDate);
		cl.add(Calendar.MINUTE, minutes);
		return cl.getTime();
	}
	/**
	 * 当前当前系统年份
	 * @return
	 */
	public static String getCurrentYear() {
		SimpleDateFormat sdf = new SimpleDateFormat(YEAR_PATTERN);
		Date date = new Date();
		return sdf.format(date);
	}

	public static Date getdate(String date) throws ParseException {
		return getdate(date, DATE_PATTERN_SLASH);
	}
	
	public static Date getdate(String date, String pattern) throws ParseException {
		if (date == null || "".equals(date))
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(date);
	}

	/**
	 * 将时间戳转成date
	 * @param date
	 * @return
	 */
	public static Date getDate(Long date) {
		if (date == null) {
			return null;
		}
		Date d = new Date(date);
		return d;
	}

	/*
	 * 获取当前天的起始时间
	 */
	public static Date getStartTime(Calendar day) {
		day.set(Calendar.HOUR_OF_DAY, 0);
		day.set(Calendar.MINUTE, 0);
		day.set(Calendar.SECOND, 0);
		day.set(Calendar.MILLISECOND, 0);
		return day.getTime();
	}
	public static Date getStartTime(Date date) {
		Calendar day = Calendar.getInstance();
		day.setTime(date);
		return getStartTime(day);
	}
	/*
	 * 获取当前天的结束时间
	 */
	public static Date getEndTime(Calendar day) {
		day.set(Calendar.HOUR_OF_DAY, 23);
		day.set(Calendar.MINUTE, 59);
		day.set(Calendar.SECOND, 59);
		day.set(Calendar.MILLISECOND, 999);
		return day.getTime();
	}
	public static Date getEndTime(Date date) {
		Calendar day = Calendar.getInstance();
		day.setTime(date);
		return getEndTime(day);
	}

	/**
	 * LocalDateTime转换为Date
	 * @param localDateTime
	 */
	public static Date localDateTime2Date(LocalDateTime localDateTime) {
		if(localDateTime == null)
			return null;
		ZoneId zoneId = ZoneId.of(ZONE_ID);
        ZonedDateTime zdt = localDateTime.atZone(zoneId);//Combines this date-time with a time-zone to create a  ZonedDateTime.
		Date date = Date.from(zdt.toInstant());
		return date;
	}
	
	public static Long localDateTime2Time(LocalDateTime localDateTime) {
		if(localDateTime == null)
			return null;
		ZoneId zoneId = ZoneId.of(ZONE_ID);
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return zdt.toInstant().toEpochMilli();
	}
	
	public static LocalDateTime time2LocalDataTime(Long time) {
		if(time == null)
			return null;
		return date2LocalDateTime(new Date(time));
	}
	
	public static LocalDateTime dateStr2LocalDateTime(String dateStr, String pattern) {
		Date date = null;
		try {
			date = getdate(dateStr, pattern);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date2LocalDateTime(date);
	}

	/**
	 * Date转换为LocalDateTime
	 * @param date
	 */
	public static LocalDateTime date2LocalDateTime(Date date) {
		if(date == null)
			return null;
		Instant instant = date.toInstant();// An instantaneous point on the time-line.(时间线上的一个瞬时点。)
		ZoneId zoneId = ZoneId.of(ZONE_ID);
		LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
		return localDateTime;
	}
	/**
     * time2 - time1(计算两个时间戳之间间隔的月份数)
     * @param time1
     * @param time2
     * @return
     */
	public static int spacingTime(long time1, long time2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String timeStr1 = format.format(new Date(time1));
        String timeStr2 = format.format(new Date(time2));
        Calendar timeCalendar1 = Calendar.getInstance();
        Calendar timeCalendar2 = Calendar.getInstance();
        try {
            timeCalendar1.setTime(format.parse(timeStr1));
            timeCalendar2.setTime(format.parse(timeStr2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int result = timeCalendar2.get(Calendar.MONTH) - timeCalendar1.get(Calendar.MONTH);//结束月份减开始月份
        int month = (timeCalendar2.get(Calendar.YEAR) - timeCalendar1.get(Calendar.YEAR)) * 12;//结束年份减开始年份*12
        return result + month;
    }
	
	public static Long nextMonth(Long mon,int flag) {
		Long res = 0L;
		Date date = mon==null ? new Date():new Date(mon);//当前日期
		Calendar calendar = Calendar.getInstance();//日历对象
		calendar.setTime(date);//设置当前日期
		calendar.add(Calendar.MONTH, flag);//月份减一为-1，加一为1
		res = calendar.getTimeInMillis();
		return res;
	}
}
