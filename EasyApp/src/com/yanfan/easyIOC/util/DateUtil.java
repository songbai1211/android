package com.yanfan.easyIOC.util;

import java.text.ParseException;   
import java.text.SimpleDateFormat;   
import java.util.Calendar;   
import java.util.Date;   
/**
 * 时间帮助类
 * @author Administrator
 *
 */
public class DateUtil {   

	private static Calendar calendar=Calendar.getInstance();   

	/**  
	 * 得到当前的时间，时间格式yyyy-MM-dd  HH:mm:ss
	 * @return  
	 */  
	public static String nowToString(){   
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		return sdf.format(new Date());   
	}
	/**  
	 * 得到当前的时间,自定义时间格式  
	 * y 年 M 月 d 日 H 时 m 分 s 秒  
	 * @param dateFormat 输出显示的时间格式  
	 * @return  
	 */  
	public static String getCurrentDate(String dateFormat){   
		SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);   
		return sdf.format(new Date());   
	}   

	/**  
	 * 日期格式化，默认日期格式yyyy-MM-dd  HH:mm:ss
	 * @param object  
	 * @return  
	 */  
	public static String format(Object object){   
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		return sdf.format(object);   
	}   

	/**  
	 * 日期格式化，自定义输出日期格式  
	 * @param date  
	 * @return  
	 */  
	public static String getFormatDate(Date date,String dateFormat){   
		SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);   
		return sdf.format(date);   
	}   
	/**  
	 * 返回当前日期的前一个时间日期，amount为正数 当前时间后的时间 为负数 当前时间前的时间  
	 * 默认日期格式yyyy-MM-dd  
	 * @param field 日历字段  
	 * y 年 M 月 d 日 H 时 m 分 s 秒  
	 * @param amount 数量  
	 * @return 一个日期  
	 */  
	public static String getPreDate(String field,int amount){   
		calendar.setTime(new Date());   
		if(field!=null&&!field.equals("")){   
			if(field.equals("y")){   
				calendar.add(Calendar.YEAR, amount);   
			}else if(field.equals("M")){   
				calendar.add(Calendar.MONTH, amount);   
			}else if(field.equals("d")){   
				calendar.add(Calendar.DAY_OF_MONTH, amount);   
			}else if(field.equals("H")){   
				calendar.add(Calendar.HOUR, amount);   
			}   
		}else{   
			return null;   
		}          
		return format(calendar.getTime());   
	}   

	/**  
	 * 某一个日期的前一个日期  
	 * @param d,某一个日期  
	 * @param field 日历字段  
	 * y 年 M 月 d 日 H 时 m 分 s 秒  
	 * @param amount 数量  
	 * @return 一个日期  
	 */  
	public static String getPreDate(Date d,String field,int amount){   
		calendar.setTime(d);   
		if(field!=null&&!field.equals("")){   
			if(field.equals("y")){   
				calendar.add(Calendar.YEAR, amount);   
			}else if(field.equals("M")){   
				calendar.add(Calendar.MONTH, amount);   
			}else if(field.equals("d")){   
				calendar.add(Calendar.DAY_OF_MONTH, amount);   
			}else if(field.equals("H")){   
				calendar.add(Calendar.HOUR, amount);   
			}   
		}else{   
			return null;   
		}          
		return format(calendar.getTime());   
	}   

	/**  
	 * 某一个时间的前一个时间  HH:mm:ss
	 * @param date  
	 * @return  
	 * @throws ParseException   
	 */  
	public static String getPreDate(String date) throws ParseException{   
		Date d=new SimpleDateFormat().parse(date);   
		String preD=getPreDate(d,"d",1);   
		Date preDate=new SimpleDateFormat().parse(preD);   
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		return sdf.format(preDate);   
	}   

	/**
	 * String转换为Date
	 * @throws ParseException 
	 */
	public static Date parse(String date) throws ParseException
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.parse(date);
	}
}