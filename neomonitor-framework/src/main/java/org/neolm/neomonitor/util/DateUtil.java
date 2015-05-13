package org.neolm.neomonitor.util;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;



public class DateUtil {

	private static Logger logger = Logger.getLogger(DateUtil.class);

	public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final String DATE_FORMAT_YYYYMMDDHHMMSS_ORACLE = "yyyymmddHH24miss";
	public static final String DATE_FORMAT_YYYYMMDDHHMM = "yyyyMMddHHmm";
	public static final String DATE_FORMAT_YYYYMMDDHH = "yyyyMMddHH";
	public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
	public static final String DATE_FORMAT_YYYYMM = "yyyyMM";
	public static final String DATE_FORMAT_YYYY = "yyyy";
	public static final String DATE_FORMAT_MM = "MM";
	public static final String DATE_FORMAT_EN_A_YYYYMMDDHHMMSS = "yyyy/MM/dd HH:mm:ss";
	public static final String DATE_FORMAT_EN_A_YYYYMMDDHHMM = "yyyy/MM/dd HH:mm";
	public static final String DATE_FORMAT_EN_A_YYYYMMDDHH = "yyyy/MM/dd HH";
	public static final String DATE_FORMAT_EN_A_YYYYMMDD = "yyyy/MM/dd";
	public static final String DATE_FORMAT_EN_A_YYYYMM = "yyyy/MM";
	public static final String DATE_FORMAT_EN_A_YYYY = "yyyy";
	public static final String DATE_FORMAT_EN_B_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_EN_B_YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT_EN_B_YYYYMMDDHH = "yyyy-MM-dd HH";
	public static final String DATE_FORMAT_EN_B_YYYYMMDD = "yyyy-MM-dd";
	public static final String DATE_FORMAT_EN_B_YYYYMM = "yyyy-MM";
	public static final String DATE_FORMAT_EN_B_YYYY = "yyyy";
	public static final String DATE_FORMAT_CN_YYYYMMDDHHMMSS = "yyyy'年'MM'月'dd'日' HH'时'mm'分'ss'秒'";
	public static final String DATE_FORMAT_CN_YYYYMMDDHHMM = "yyyy'年'MM'月'dd'日' HH'时'mm'分'";
	public static final String DATE_FORMAT_CN_YYYYMMDDHH = "yyyy'年'MM'月'dd'日' HH'时'";
	public static final String DATE_FORMAT_CN_YYYYMMDD = "yyyy'年'MM'月'dd'日'";
	public static final String DATE_FORMAT_CN_YYYYMM = "yyyy'年'MM'月'";
	public static final String DATE_FORMAT_CN_YYYY = "yyyy'年'";

	/** 匹配年月日字符串, 如: "2011-01-01", "2011/01/10", "2011.03.21", "2011 11 01", "20111210" */
	public static String MATCH_YEAR_MONTH_DAY = "(19|20)\\d\\d([- /.]?)(0[1-9]|1[012])\\2(0[1-9]|[12][0-9]|3[01])";
	
	public static String MATCH_YEAR_MONTH_DAY_YYYYMMDD = "\\d\\d\\d\\d(0[1-9]|1[012])\\2(0[1-9]|[12][0-9]|3[01])";
	public static String MATCH_YEAR_MONTH_DAY_YYYYMM = "\\d\\d\\d\\d(0[1-9]|1[012])";
    /**
     * 获取当前时间的方法，统一使用该接口方便以后转换实现方式
     * @return
     */
    public static Date now(){
    	return new Date();
    }

	/**
	 * 获取当前时间字符串
	 * 
	 * @param pattern 格式化样式
	 * @return 经过格式化的当前时间字符串
	 */
	public static String now(String pattern) {
		if (pattern == null || "".equals(pattern.trim())) {
			pattern = DATE_FORMAT_YYYYMMDDHHMMSS;
		}
		return formatDate(now(), pattern);
	}

    /**
     * 格式化日志为指定样式的字符串
     * 
     * @param date 给定的日期对象
     * @param pattern 格式化采用的样式
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

	/**
	 * 对输入的日期字符串进行格式化
	 * @param strDate 需要进行格式化的日期字符串
	 * @param pattern 格式化采用的样式
	 * @return 格式化后的字符串
	 */
	public static String formatDate(String strDate, String pattern) {
        if (strDate == null || strDate.trim().equals("")) {
            return "";
        }
        String formatStr = parseDateFormat(strDate);
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formatter.parse(strDate));
            formatter = new SimpleDateFormat(pattern);
            return formatter.format(calendar.getTime());
        } catch (Exception e) {
            logger.debug("转换日期字符串格式时出错;" + e.getMessage());
            return "";
        }
    }

	/**
	 * 将日期字符串转换为日期对象.
	 * 2011-3-7 zhengzp 采用正则表达式来匹配日期格式,从而达到严格匹配的目的,降低异常几率.
	 * 
	 * @param strDate 需要进行转换的日期字符串
	 * @return 日期对象
	 */
	public static Date parseDate(String strDate) {
		if (strDate == null || strDate.trim().equals("")) {
			return null;
		}
		String formatStr = parseDateFormat(strDate);
		return parseDate(strDate, formatStr);
	}
	
	
	/**
	 * 解析日期字符串格式
	 * @param strDate
	 * @return
	 */
	public static String parseDateFormat(String strDate) {
		String formatStr = "";
		if (Pattern.matches("^[1-9]\\d{3}(0[1-9]|1[012])$", strDate)) {
			formatStr = DATE_FORMAT_YYYYMM;
		}else if (Pattern.matches("^[1-9]\\d{3}$", strDate)) {
			formatStr = "yyyy";
		}else if (Pattern.matches("^\\d\\d(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$", strDate)) {
			formatStr = "yyMMdd";
		} else if (Pattern.matches("^[1-9]\\d{3}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$", strDate)) {
			formatStr = DATE_FORMAT_YYYYMMDD;
		} else if (Pattern.matches("^[1-9]\\d{3}(/)(0[1-9]|1[012])\\1(0[1-9]|[12][0-9]|3[01])$", strDate)) {
			formatStr = DATE_FORMAT_EN_A_YYYYMMDD;
		} else if (Pattern.matches("^[1-9]\\d{3}(-)(0[1-9]|1[012])\\1(0[1-9]|[12][0-9]|3[01])$", strDate)) {
			formatStr = DATE_FORMAT_EN_B_YYYYMMDD;
		} else if (Pattern.matches("^[1-9]\\d{3}(.)(0[1-9]|1[012])\\1(0[1-9]|[12][0-9]|3[01])$", strDate)) {
			formatStr = "yyyy.MM.dd";
		} else if (Pattern.matches("^[1-9]\\d{3}( )(0[1-9]|1[012])\\1(0[1-9]|[12][0-9]|3[01])$", strDate)) {
			formatStr = "yyyy MM dd";
		} else if (Pattern.matches("^[1-9]\\d{3}(年)(0[1-9]|1[012])(月)(0[1-9]|[12][0-9]|3[01])(日)$", strDate)) {
			formatStr = DATE_FORMAT_CN_YYYYMMDD;
		} else if (Pattern.matches("^[1-9]\\d{3}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])([01][0-9]|2[0-3])([0-5][0-9]){2}$", strDate)) {
			formatStr = DATE_FORMAT_YYYYMMDDHHMMSS;
		} else if (Pattern.matches(
				"^[1-9]\\d{3}(/)(0[1-9]|1[012])\\1(0[1-9]|[12][0-9]|3[01]) ([01][0-9]|2[0-3])(:)([0-5][0-9])\\5([0-5][0-9])$", strDate)) {
			formatStr = DATE_FORMAT_EN_A_YYYYMMDDHHMMSS;
		} else if (Pattern.matches(
				"^[1-9]\\d{3}(-)(0[1-9]|1[012])\\1(0[1-9]|[12][0-9]|3[01]) ([01][0-9]|2[0-3])(:)([0-5][0-9])\\5([0-5][0-9])$", strDate)) {
			formatStr = DATE_FORMAT_EN_B_YYYYMMDDHHMMSS;
		} else {
			logger.error("无法识别的字符串格式:" + strDate);
		}
		return formatStr;
	}

	/**
	 * 将指定格式的日期字符串转换为日期对象.
	 * @param strDate 需要进行转换的日期字符串
	 * @param pattern 日期字符串的格式
	 * @return 日期对象
	 */
	public static Date parseDate(String strDate, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		try {
			return formatter.parse(strDate);
		} catch (ParseException e) {
			logger.error("解析和转换日期字符串出错,检查字符串是否是日期格式.", e);
			return null;
		}
	}
	/**
	 * @param cutOffType
	 * @param cutOffNum
	 * @return
	 */
	public static Date getCutoffNow(int cutOffType, int cutOffNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cutOffType, cutOffNum);
        return cal.getTime();
    }

    /**
     * @param cutOffType
     * @param cutOffNum
     * @param pos
     * @return
     */
    public static Date getCutoffDate(int cutOffType, int cutOffNum, Date pos) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(pos);
        cal.add(cutOffType, cutOffNum);
        return cal.getTime();
    }

    /**
     * @param cutOffType
     * @param cutOffNum
     * @param format
     * @return
     */
    public static String getCutoffNow(int cutOffType, int cutOffNum, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cutOffType, cutOffNum);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(cal.getTime());
    }

    /**
     * @param cutOffType
     * @param cutOffNum
     * @param pos
     * @param format
     * @return
     */
    public static String getCutoffDate(int cutOffType, int cutOffNum, Date pos, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(pos);
        cal.add(cutOffType, cutOffNum);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(cal.getTime());
    }

	/**
	 * 获取偏移时间
	 * 
	 * @param date
	 * @param trewingStr 传入格式:-mm:ss or -ss 前面必须要有符号
	 * @return
	 */
	public static Date getSkewingTime(Date date, String trewingStr) {
		if (trewingStr == null)
			return null;
		trewingStr = trewingStr.trim();
		String tmp = "";
		boolean isDesc = false;
		int minute = 0, second = 0;
		if (trewingStr.indexOf('-') >= 0)
			isDesc = true;
		// 去掉前面的符号
		trewingStr = trewingStr.substring(1, trewingStr.length());
		if (trewingStr.indexOf(':') >= 0) {
			tmp = trewingStr.substring(0, trewingStr.indexOf(':'));
			minute = Integer.parseInt(tmp);
			tmp = trewingStr.substring(trewingStr.indexOf(':') + 1);
			second = Integer.parseInt(tmp);
		} else {
			second = Integer.parseInt(trewingStr);
		}
		long trewing = (minute * 60 + second) * 1000;
		if (isDesc) {
			trewing = -trewing;
		}
		Date datetmp = new Date(date.getTime() + trewing);
		return datetmp;
	}

	public static Date getFirstDateAtMonth(Date date) {
		Calendar time = Calendar.getInstance();
		time.setTime(date);
		time.set(Calendar.DATE, time.getActualMinimum(Calendar.DAY_OF_MONTH));
		return time.getTime();
	}
	
    public static Date getLastDateAtMonth(Date date) {
        Calendar time = Calendar.getInstance();
        time.setTime(date);
        time.set(Calendar.DATE, time.getActualMaximum(Calendar.DAY_OF_MONTH));
        return time.getTime();
    }

	/**
	 * 获取当前时间的Timestamp
	 * @return
	 */
	public static Timestamp getNowTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	/**
	 * 获取指定时间(字符串)的Timestamp
	 * @param timestr
	 * @return
	 */
	public static Timestamp parseTimestamp(String timestr) {
		if (timestr == null || timestr.equals(""))
			return null;
		return new Timestamp(parseDate(timestr).getTime());
	}

	/**
	 * 获取指定时间(Date)的Timestamp
	 * @param time
	 * @return
	 */
	public static Timestamp parseTimestamp(Date time) {
		if (time == null)
			return null;
		return new Timestamp(time.getTime());
	}

	/**
	 * @param sInitY
	 * @param sInitM
	 * @param sEndY
	 * @param sEndM
	 * @return
	 */
	public static String[] getYMs(String sInitY, String sInitM, String sEndY, String sEndM) {
		int initM = Integer.parseInt(sInitM);
		int initY = Integer.parseInt(sInitY);
		int endM = Integer.parseInt(sEndM);
		int endY = Integer.parseInt(sEndY);
		String[] YMs = null;
		int totalM = (endY - initY) * 12 + (endM - initM) + 1;
		YMs = new String[totalM];
		Date initDate = parseDate(sInitY + sInitM);
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < totalM; i++) {
			cal.setTime(initDate);
			cal.add(Calendar.MONTH, i);
			SimpleDateFormat formatter = new SimpleDateFormat(DateUtil.DATE_FORMAT_YYYYMM);
			YMs[i] = "_" + formatter.format(cal.getTime());
		}
		return YMs;
	}

	/**
	 * 2008-09-14 zengxr 增加获取输入时间和当前时间中最大一个的函数
	 * 
	 * @param efftTime
	 * @param format
	 * @return
	 */
	public static String getLastDateWithNow(String efftTime, String format) {
		if (efftTime == null || format == null)
			return null;
		if (efftTime.length() > format.length()) {
			efftTime = efftTime.substring(0, format.length());
		}
		String now = DateUtil.now(format);
		if (efftTime.compareTo(now) < 0) {
			if (efftTime.length() > format.length()) {
				now = now + efftTime.substring(format.length());
			}
			return now;
		} else
			return efftTime;
	}

	/**
	 * 计算距离指定日期若干天的日期
	 * 
	 * @param src
	 * @param dayOffset
	 * @return
	 */
	public static Date getOffsetDay(Date src, int dayOffset) {
		Calendar now = Calendar.getInstance();
		now.setTime(src);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + dayOffset);
		return now.getTime();
	}

	/**
	 * 计算距离指定日期若干月的日期
	 * 
	 * @param src
	 * @param monthOffset
	 * @return
	 */
	public static Date getOffsetMonth(Date src, int monthOffset) {
		Calendar now = Calendar.getInstance();
		now.setTime(src);
		now.set(Calendar.MONTH, now.get(Calendar.MONTH) + monthOffset);
		return now.getTime();
	}

    /**
     * 获取当前时间指定区域的值
     * @param field 时间区域(Calendar.YEAR or Calendar.MONTH or Calendar.DATE, etc.)
     * @return 时间值
     */
    public static int getNowField(int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(field);
    }
    /**
	 * 指定在某一时间点取几个小时后的时间 
	 * @param day  具体时间点
	 * @param x    表示加x小时
	 * @return
	 */
	public static String getOffsetHour(String day, int x) 
    { 
        Date date = getFormattedDate(day); 
        if (date == null) return ""; 
        Calendar cal = Calendar.getInstance(); 
        cal.setTime(date); 
        cal.add(Calendar.HOUR_OF_DAY, x);//24小时制 
        //cal.add(Calendar.HOUR, x);12小时制 
        date = cal.getTime(); 
        return formatDate(date, DATE_FORMAT_YYYYMMDDHHMMSS); 
    } 
	
	/**
     * 对输入的日期字符串进行格式化.
     *
     * @param strDate     需要进行格式化的日期，格式为前面定义的 DATE_FORMAT_YYYYMMDDHHMMSS
     * @return 经过格式化后的字符串
     */
    public static Date getFormattedDate(String strDate) {
        String formatStr = "yyyyMMdd";
        if (strDate == null || strDate.trim().equals("")) {
            return null;
        }
        switch (strDate.trim().length()) {
            case 6:
            	//修复“110501”时间格式转换错误问题
                if (strDate.substring(0, 1).equals("0")
                		||strDate.substring(0, 1).equals("1")) {
                    formatStr = "yyMMdd";
                } else {
                    formatStr = "yyyyMM";
                }
                break;
            case 8:
                formatStr = "yyyyMMdd";
                break;
            case 10:
                if (strDate.indexOf("-") == -1) {
                    formatStr = "yyyy/MM/dd";
                } else {
                    formatStr = "yyyy-MM-dd";
                }
                break;
            case 11:
                if (strDate.getBytes().length == 14) {
                    formatStr = "yyyy年MM月dd日";
                } else {
                    return null;
                }
            case 14:
                formatStr = "yyyyMMddHHmmss";
                break;
            case 19:
                if (strDate.indexOf("-") == -1) {
                    formatStr = "yyyy/MM/dd HH:mm:ss";
                } else {
                    formatStr = "yyyy-MM-dd HH:mm:ss";
                }
                break;
            default:
                return null;
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
            return formatter.parse(strDate);
        } catch (Exception e) {
        	logger.getLogger(DateUtil.class).error(e,e);
        	logger.getLogger(DateUtil.class).debug("转换日期字符串格式时出错;" + e.getMessage());
            return null;
        }
    }
    /**
     * 对输入的日期字符串进行格式化.
     *
     * @param strDate     需要进行格式化的日期，格式为前面定义的 DATE_FORMAT_YYYYMMDDHHMMSS
     * @param strFormatTo 指定采用何种格式进行格式化操作
     * @return 经过格式化后的字符串
     */
    public static String getFormattedDate(String strDate, String strFormatTo) {
        String formatStr = "yyyyMMdd";
        if (strDate == null || strDate.trim().equals("")) {
            return "";
        }
        switch (strDate.trim().length()) {
            case 6:
            	//修复“110501”时间格式转换错误问题
                if (strDate.substring(0, 1).equals("0")
                		||strDate.substring(0, 1).equals("1")) {
                    formatStr = "yyMMdd";
                } else {
                    formatStr = "yyyyMM";
                }
                break;
            case 8:
                formatStr = "yyyyMMdd";
                break;
            case 10:
                if (strDate.indexOf("-") == -1) {
                    formatStr = "yyyy/MM/dd";
                } else {
                    formatStr = "yyyy-MM-dd";
                }
                break;
            case 11:
                if (strDate.getBytes().length == 14) {
                    formatStr = "yyyy年MM月dd日";
                } else {
                    return "";
                }
            case 14:
                formatStr = "yyyyMMddHHmmss";
                break;
            case 19:
                if (strDate.indexOf("-") == -1) {
                    formatStr = "yyyy/MM/dd HH:mm:ss";
                } else {
                    formatStr = "yyyy-MM-dd HH:mm:ss";
                }
                break;
            default:
                return strDate.trim();
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formatter.parse(strDate));
            formatter = new SimpleDateFormat(strFormatTo);
            return formatter.format(calendar.getTime());
        } catch (Exception e) {
        	logger.getLogger(DateUtil.class).debug("转换日期字符串格式时出错;" + e.getMessage());
            return "";
        }
    }
    /**
     * 得到指定日期 yyyyMMddHHmmss 前后 指定天的数据
     *
     * @param strNowDay
     * @param nDelaySeconds
     * @return
     */
    public static String getSpecDayStr(String strNowDay, int nDelayDays) {
        if (strNowDay.length() != 14) {
        	strNowDay = getFormattedDate(strNowDay,DATE_FORMAT_YYYYMMDDHHMMSS);
        }
        
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_YYYYMMDDHHMMSS);
            formatter.setLenient(false);
            Date date = formatter.parse(strNowDay);
            Date datetmp = new Date(date.getTime() + nDelayDays*24*60*60*1000);
            return formatter.format(datetmp);
        } catch (Exception e) {
        	logger.getLogger(DateUtil.class).debug("操作出错:" + e.getMessage());
            return "";
        }
    }
    
}
