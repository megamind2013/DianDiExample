package net.itdiandi.java.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils
* @ClassName DateUtil
* @Description 日期工具
* @author 刘吉超
* @date 2016-02-24 10:08:31
*/
public class DateUtil {
	private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
	
	private static final String FORMAT_0 = "yyyy-MM-dd HH:mm:ss";
	private static final String FORMAT_1 = "yyyy-MM-dd";
	private static final String FORMAT_2 = "HH:mm:ss";
	private static final String FORMAT_3 = "yyyyMMddHHmmss";
	private static final String FORMAT_4 = "yyyy-MM-dd HH";
	
	/**
	 * 获得含有时间的id 字符串
	 * 
	 * @return
	 * @return String
	*/
	public static String getIdByTime(){
		Date now = new Date();
		SimpleDateFormat simple = new SimpleDateFormat(FORMAT_3);
		return simple.format(now);
	}
	
	/**
	 * 如果参数长度不为为0,则取第一个参数进行格式化，<br>
	 * 否则取当前日期时间，精确到秒 如:2010-4-15 9:36:38
	 * 
	 * @param date 可变参数
	 * @return String
	*/
	public static String toFull(Date... date) {
		SimpleDateFormat simple = new SimpleDateFormat(FORMAT_0);
		if (date != null && date.length > 0) {
			return simple.format(date[0]);
		}
		return simple.format(new Date());
	}

	/**
	 * 如果参数长度不为为0,则取第一个参数进行格式化，<br>
	 * 否则取当前日期 如:2010-4-15
	 * 
	 * @param date 可变参数
	 * @return String
	*/
	public static String toDate(Date... date) {
		SimpleDateFormat simple = new SimpleDateFormat(FORMAT_1);
		if (date.length > 0) {
			return simple.format(date[0]);
		}
		return simple.format(new Date());
	}

	/**
	 * 如果参数长度不为为0,则取第一个参数进行格式化，<br>
	 * 否则取当前日期时间，精确到秒<br>
	 * 如:9:36:38
	 * 
	 * @param date 可变参数
	 * @return String
	*/
	public static String toTime(Date... date) {
		SimpleDateFormat simple = new SimpleDateFormat(FORMAT_2);
		if (date.length > 0) {
			return simple.format(date[0]);
		}
		return simple.format(new Date());
	}

	/**
	 * 根据字符串格式去转换相应格式的日期和时间
	 * 
	 * @param date 必要参数
	 * @return Date
	*/
	public static Date reverseDate(String date) {
		if(StringUtils.isNotBlank(date)){
			SimpleDateFormat simple = null;
			date = date.trim();
			
			switch (date.length()) {
				case 19:// 日期+时间
					simple = new SimpleDateFormat(FORMAT_0);
					break;
				case 14:// 日期+时间
					simple = new SimpleDateFormat(FORMAT_3);
					break;
				case 10:// 仅日期
					simple = new SimpleDateFormat(FORMAT_1);
					break;
				case 8:// 仅时间
					simple = new SimpleDateFormat(FORMAT_2);
					break;
			}
			
			try {
				return simple.parse(date);
			} catch (ParseException e) {
				logger.error("",e);
			}
		}
		
		return null;
	}
	
	/**
	 * 将带有时、分、秒格式的日期转化为00:00:00<br>
	 * 方便日期推算,格式化后的是yyyy-MM-dd 00:00:00
	 * 
	 * @param dates date的长度可以为0,即不用给参数
	 * @return Date 返回日期
	*/
	public static Date startOfDay(Date... dates) {
		Date date = dates.length == 0 ? new Date() : dates[0];// 如果date为null则取当前时间
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		
		return cal.getTime();
	}
	
	/**
	 * 推算当前日期在一月内向前或向后偏移多少天，且严格按整天计算
	 * 
	 * @param amount 偏移量
	 * @return Date
	*/
	public static Date addDayOfMonth(Integer amount) {
		return addDayOfMonth(amount,true);
	}
	
	/**
	 * 推算当前日期在一月内向前或向后偏移多少天
	 * 
	 * @param amount 偏移量
	 * @param b 是否先将date格式化为yyyy-MM-dd 00:00:00 即: 是否严格按整天计算
	 * @return Date
	*/
	public static Date addDayOfMonth(Integer amount, Boolean b) {
		return addDayOfMonth(null,amount,b);
	}
	
	/**
	 * 推算一个月内向前或向后偏移多少天
	 * 
	 * @param date 要被偏移的日期
	 * @param amount 偏移量
	 * @param b 是否先将date格式化为yyyy-MM-dd 00:00:00 即: 是否严格按整天计算
	 * @return Date
	*/
	public static Date addDayOfMonth(Date date, Integer amount, Boolean b) {
		// 如果date为null则取当前日期
		date = date == null ? new Date() : date; 
		
		// 是否严格按整天计算
		if (b) {
			date = startOfDay(date);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, amount);
		
		return cal.getTime();
	}

	/**
	 * 将带有秒格式的日期转化为00<br>
	 * 方便日期推算,格式化后的是yyyy-MM-dd HH:mm:00
	 * 
	 * @param dates date的长度可以为0,即不用给参数
	 * @return Date 返回日期
	*/
	public static Date startOfMinute(Date... dates) {
		// 如果date为null则取当前时间
		Date date = dates.length == 0 ? new Date() : dates[0];
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND,0);
		
		return cal.getTime();
	}
	
	/**
	 * 推算当前日期在一个小时内向前或向后偏移多少分钟，且严格按整分钟计算
	 * 
	 * @param amount 偏移量
	 * @return Date 返回日期
	*/
	public static Date addMinuteOfHour(Integer amount) {
		return addMinuteOfHour(amount,true);
	}
	
	/**
	 * 推算当前日期在一个小时内向前或向后偏移多少分钟
	 * 
	 * @param amount 偏移量
	 * @param b 是否先将date格式化为yyyy-MM-dd HH:mm:00 即: 是否严格按整分钟计算
	 * @return Date 返回日期
	*/
	public static Date addMinuteOfHour(Integer amount, Boolean b) {
		return addMinuteOfHour(null,amount,b);
	}
	
	/**
	 * 推算一个小时内向前或向后偏移多少分钟,除了秒、毫秒不可以以外,其他都可以
	 * 
	 * @param date 要被偏移的日期
	 * @param amount 偏移量
	 * @param b 是否先将date格式化为yyyy-MM-dd HH:mm:00 即: 是否严格按整分钟计算
	 * @return Date 返回日期
	*/
	public static Date addMinuteOfHour(Date date, Integer amount, Boolean b) {
		// 如果date为null则取当前日期
		date = date == null ? new Date() : date;
		
		if (b) {
			date = startOfMinute(date);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, amount);
		
		return cal.getTime();
	}
	
	/**
     * 获得本周的第一天，周一
     * 
     * @return
     */
    public Date getCurrentWeekDayStartTime() {
        Calendar c = Calendar.getInstance();
        try {
            int weekday = c.get(Calendar.DAY_OF_WEEK) - 2;
            c.add(Calendar.DATE, -weekday);
            c.setTime(new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_1).format(c.getTime()) + " 00:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }
    
    /**
     * 获得本周的最后一天，周日
     * 
     * @return
     */
    public   Date getCurrentWeekDayEndTime() {
        Calendar c = Calendar.getInstance();
        try {
            int weekday = c.get(Calendar.DAY_OF_WEEK);
            c.add(Calendar.DATE, 8 - weekday);
            c.setTime(new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_1).format(c.getTime()) + " 23:59:59"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }
    
    /**
     * 获得本天的开始时间，即2012-01-01 00:00:00
     * 
     * @return
     */
    public Date getCurrentDayStartTime() {
        Date now = new Date();
        try {
            now = new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_1).format(now));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 获得本天的结束时间，即2012-01-01 23:59:59
     * 
     * @return
     */
    public Date getCurrentDayEndTime() {
        Date now = new Date();
        try {
            now = new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_1).format(now) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 获得本小时的开始时间，即2012-01-01 23:59:59
     * 
     * @return
     */
    public Date getCurrentHourStartTime() {
        Date now = new Date();
        try {
            now = new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_1).format(now));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 获得本小时的结束时间，即2012-01-01 23:59:59
     * 
     * @return
     */
    public Date getCurrentHourEndTime() {
        Date now = new Date();
        try {
            now = new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_4).format(now) + ":59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 获得本月的开始时间，即2012-01-01 00:00:00
     * 
     * @return
     */
    public Date getCurrentMonthStartTime() {
        Calendar c = Calendar.getInstance();
        Date now = null;
        try {
            c.set(Calendar.DATE, 1);
            now = new SimpleDateFormat(FORMAT_1).parse(new SimpleDateFormat(FORMAT_1).format(c.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 当前月的结束时间，即2012-01-31 23:59:59
     * 
     * @return
     */
    public   Date getCurrentMonthEndTime() {
        Calendar c = Calendar.getInstance();
        Date now = null;
        try {
            c.set(Calendar.DATE, 1);
            c.add(Calendar.MONTH, 1);
            c.add(Calendar.DATE, -1);
            now = new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_1).format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 当前年的开始时间，即2012-01-01 00:00:00
     * 
     * @return
     */
    public   Date getCurrentYearStartTime() {
        Calendar c = Calendar.getInstance();
        Date now = null;
        try {
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DATE, 1);
            now = new SimpleDateFormat(FORMAT_1).parse(new SimpleDateFormat(FORMAT_1).format(c.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 当前年的结束时间，即2012-12-31 23:59:59
     * 
     * @return
     */
    public   Date getCurrentYearEndTime() {
        Calendar c = Calendar.getInstance();
        Date now = null;
        try {
            c.set(Calendar.MONTH, 11);
            c.set(Calendar.DATE, 31);
            now = new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_1).format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 当前季度的开始时间，即2012-01-1 00:00:00
     * 
     * @return
     */
    public   Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 4);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            now = new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_1).format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 当前季度的结束时间，即2012-03-31 23:59:59
     * 
     * @return
     */
    public   Date getCurrentQuarterEndTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3) {
                c.set(Calendar.MONTH, 2);
                c.set(Calendar.DATE, 31);
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 5);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                c.set(Calendar.MONTH, 8);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DATE, 31);
            }
            now = new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_1).format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 获取前/后半年的开始时间
     * @return
     */
    public   Date getHalfYearStartTime(){
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 6){
                c.set(Calendar.MONTH, 0);
            }else if (currentMonth >= 7 && currentMonth <= 12){
                c.set(Calendar.MONTH, 6);
            }
            c.set(Calendar.DATE, 1);
            now = new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_1).format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 获取前/后半年的结束时间
     * @return
     */
    public Date getHalfYearEndTime(){
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 6){
                c.set(Calendar.MONTH, 5);
                c.set(Calendar.DATE, 30);
            }else if (currentMonth >= 7 && currentMonth <= 12){
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DATE, 31);
            }
            now = new SimpleDateFormat(FORMAT_0).parse(new SimpleDateFormat(FORMAT_1).format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    /**
     * 获取前一天星期
     */
    public String getPreWeek(){
    	String[] weekDays = {"周日","周一","周二","周三","周四","周五","周六"};
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DATE,-1);
    	int i = cal.get(Calendar.DAY_OF_WEEK)-1;
    	if(i<0){
    		i=0;
    	}
//    	String yesterday = new SimpleDateFormat("yyyy年MM月dd日").format(cal.getTime());
    	
    	return weekDays[i]; 
    }
	
	public static void main(String[] args) {
		System.out.println(addDayOfMonth(2));
	}
}