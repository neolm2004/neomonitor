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
	public static final String DATE_FORMAT_CN_YYYYMMDDHHMMSS = "yyyy'��'MM'��'dd'��' HH'ʱ'mm'��'ss'��'";
	public static final String DATE_FORMAT_CN_YYYYMMDDHHMM = "yyyy'��'MM'��'dd'��' HH'ʱ'mm'��'";
	public static final String DATE_FORMAT_CN_YYYYMMDDHH = "yyyy'��'MM'��'dd'��' HH'ʱ'";
	public static final String DATE_FORMAT_CN_YYYYMMDD = "yyyy'��'MM'��'dd'��'";
	public static final String DATE_FORMAT_CN_YYYYMM = "yyyy'��'MM'��'";
	public static final String DATE_FORMAT_CN_YYYY = "yyyy'��'";

	/** ƥ���������ַ���, ��: "2011-01-01", "2011/01/10", "2011.03.21", "2011 11 01", "20111210" */
	public static String MATCH_YEAR_MONTH_DAY = "(19|20)\\d\\d([- /.]?)(0[1-9]|1[012])\\2(0[1-9]|[12][0-9]|3[01])";
	
	public static String MATCH_YEAR_MONTH_DAY_YYYYMMDD = "\\d\\d\\d\\d(0[1-9]|1[012])\\2(0[1-9]|[12][0-9]|3[01])";
	public static String MATCH_YEAR_MONTH_DAY_YYYYMM = "\\d\\d\\d\\d(0[1-9]|1[012])";
    /**
     * ��ȡ��ǰʱ��ķ�����ͳһʹ�øýӿڷ����Ժ�ת��ʵ�ַ�ʽ
     * @return
     */
    public static Date now(){
    	return new Date();
    }

	/**
	 * ��ȡ��ǰʱ���ַ���
	 * 
	 * @param pattern ��ʽ����ʽ
	 * @return ������ʽ���ĵ�ǰʱ���ַ���
	 */
	public static String now(String pattern) {
		if (pattern == null || "".equals(pattern.trim())) {
			pattern = DATE_FORMAT_YYYYMMDDHHMMSS;
		}
		return formatDate(now(), pattern);
	}

    /**
     * ��ʽ����־Ϊָ����ʽ���ַ���
     * 
     * @param date ���������ڶ���
     * @param pattern ��ʽ�����õ���ʽ
     * @return ��ʽ������ַ���
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

	/**
	 * ������������ַ������и�ʽ��
	 * @param strDate ��Ҫ���и�ʽ���������ַ���
	 * @param pattern ��ʽ�����õ���ʽ
	 * @return ��ʽ������ַ���
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
            logger.debug("ת�������ַ�����ʽʱ����;" + e.getMessage());
            return "";
        }
    }

	/**
	 * �������ַ���ת��Ϊ���ڶ���.
	 * 2011-3-7 zhengzp ����������ʽ��ƥ�����ڸ�ʽ,�Ӷ��ﵽ�ϸ�ƥ���Ŀ��,�����쳣����.
	 * 
	 * @param strDate ��Ҫ����ת���������ַ���
	 * @return ���ڶ���
	 */
	public static Date parseDate(String strDate) {
		if (strDate == null || strDate.trim().equals("")) {
			return null;
		}
		String formatStr = parseDateFormat(strDate);
		return parseDate(strDate, formatStr);
	}
	
	
	/**
	 * ���������ַ�����ʽ
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
		} else if (Pattern.matches("^[1-9]\\d{3}(��)(0[1-9]|1[012])(��)(0[1-9]|[12][0-9]|3[01])(��)$", strDate)) {
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
			logger.error("�޷�ʶ����ַ�����ʽ:" + strDate);
		}
		return formatStr;
	}

	/**
	 * ��ָ����ʽ�������ַ���ת��Ϊ���ڶ���.
	 * @param strDate ��Ҫ����ת���������ַ���
	 * @param pattern �����ַ����ĸ�ʽ
	 * @return ���ڶ���
	 */
	public static Date parseDate(String strDate, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		try {
			return formatter.parse(strDate);
		} catch (ParseException e) {
			logger.error("������ת�������ַ�������,����ַ����Ƿ������ڸ�ʽ.", e);
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
	 * ��ȡƫ��ʱ��
	 * 
	 * @param date
	 * @param trewingStr �����ʽ:-mm:ss or -ss ǰ�����Ҫ�з���
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
		// ȥ��ǰ��ķ���
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
	 * ��ȡ��ǰʱ���Timestamp
	 * @return
	 */
	public static Timestamp getNowTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	/**
	 * ��ȡָ��ʱ��(�ַ���)��Timestamp
	 * @param timestr
	 * @return
	 */
	public static Timestamp parseTimestamp(String timestr) {
		if (timestr == null || timestr.equals(""))
			return null;
		return new Timestamp(parseDate(timestr).getTime());
	}

	/**
	 * ��ȡָ��ʱ��(Date)��Timestamp
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
	 * 2008-09-14 zengxr ���ӻ�ȡ����ʱ��͵�ǰʱ�������һ���ĺ���
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
	 * �������ָ�����������������
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
	 * �������ָ�����������µ�����
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
     * ��ȡ��ǰʱ��ָ�������ֵ
     * @param field ʱ������(Calendar.YEAR or Calendar.MONTH or Calendar.DATE, etc.)
     * @return ʱ��ֵ
     */
    public static int getNowField(int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(field);
    }
    /**
	 * ָ����ĳһʱ���ȡ����Сʱ���ʱ�� 
	 * @param day  ����ʱ���
	 * @param x    ��ʾ��xСʱ
	 * @return
	 */
	public static String getOffsetHour(String day, int x) 
    { 
        Date date = getFormattedDate(day); 
        if (date == null) return ""; 
        Calendar cal = Calendar.getInstance(); 
        cal.setTime(date); 
        cal.add(Calendar.HOUR_OF_DAY, x);//24Сʱ�� 
        //cal.add(Calendar.HOUR, x);12Сʱ�� 
        date = cal.getTime(); 
        return formatDate(date, DATE_FORMAT_YYYYMMDDHHMMSS); 
    } 
	
	/**
     * ������������ַ������и�ʽ��.
     *
     * @param strDate     ��Ҫ���и�ʽ�������ڣ���ʽΪǰ�涨��� DATE_FORMAT_YYYYMMDDHHMMSS
     * @return ������ʽ������ַ���
     */
    public static Date getFormattedDate(String strDate) {
        String formatStr = "yyyyMMdd";
        if (strDate == null || strDate.trim().equals("")) {
            return null;
        }
        switch (strDate.trim().length()) {
            case 6:
            	//�޸���110501��ʱ���ʽת����������
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
                    formatStr = "yyyy��MM��dd��";
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
        	logger.getLogger(DateUtil.class).debug("ת�������ַ�����ʽʱ����;" + e.getMessage());
            return null;
        }
    }
    /**
     * ������������ַ������и�ʽ��.
     *
     * @param strDate     ��Ҫ���и�ʽ�������ڣ���ʽΪǰ�涨��� DATE_FORMAT_YYYYMMDDHHMMSS
     * @param strFormatTo ָ�����ú��ָ�ʽ���и�ʽ������
     * @return ������ʽ������ַ���
     */
    public static String getFormattedDate(String strDate, String strFormatTo) {
        String formatStr = "yyyyMMdd";
        if (strDate == null || strDate.trim().equals("")) {
            return "";
        }
        switch (strDate.trim().length()) {
            case 6:
            	//�޸���110501��ʱ���ʽת����������
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
                    formatStr = "yyyy��MM��dd��";
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
        	logger.getLogger(DateUtil.class).debug("ת�������ַ�����ʽʱ����;" + e.getMessage());
            return "";
        }
    }
    /**
     * �õ�ָ������ yyyyMMddHHmmss ǰ�� ָ���������
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
        	logger.getLogger(DateUtil.class).debug("��������:" + e.getMessage());
            return "";
        }
    }
    
}
