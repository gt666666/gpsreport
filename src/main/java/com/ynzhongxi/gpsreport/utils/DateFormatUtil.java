package com.ynzhongxi.gpsreport.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 定义日期格式化字符串
 *
 * @author lixingwu
 */
public class DateFormatUtil {
    public final String MONTH_FORMAT = "yyyy-MM";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String HOUR_FORMAT = "HH:mm:ss";
    public static final String HOUR_DATETIME_FORMAT = "yyyy-MM-dd HH";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String MILLISECOND_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    public static final String MILLI3SECOND_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT_DIT = "yyyy.MM.dd";
    public static final String DATE_FORMAT_MD = "MMdd";
    public static final String MONTH_FORMAT_SHORT = "yyyyMM";
    public static final String DATE_FORMAT_SHORT = "yyyyMMdd";
    public static final String DATE_FORMAT_LONG = "yyyyMMddHHmm";
    public static final String YYYYM_MDDH_HMMSS_FORMAT = "yyyyMMddHHmmss";
    public static final String YYYYM_MDDH_HMMSSSSS_FORMAT = "yyyyMMddHHmmssSSS";
    public static final String DATE_FORMAT_BANK = "yyyy MM dd";
    public static final String STR_YYYY_MM = "yyyy年MM月";
    public static final String STR_MM_DD = "MM月dd日";
    public static final String STR_YYYY_MM_DD = "yyyy年MM月dd日";
    public static final String STR_HH_MM_SS = "HH时mm分ss秒";
    public static final String STR_YYYY_MM_DD_HH_MM_SS = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String STR_YY_MM_DD = "yy年MM月dd日";
    public static final String DEFAULT = DATE_FORMAT;

    /**
     * 方法描述：当前日期位移offset分钟，并格式化为 yyyy-MM-dd HH:mm:ss 字符串.
     * 创建时间：2019-06-18 09:43:16
     * 创建作者：李兴武
     *
     * @param offset 位移的分钟数（正加负减）
     * @author "lixingwu"
     */
    public static String nowDateOffsetMinuteToStr(int offset) {
        DateTime erexpireDatetime = DateUtil.offsetMinute(DateUtil.date(), offset);
        return DateUtil.format(erexpireDatetime, DATETIME_FORMAT);
    }

    /**
     * <p>方法名称：获取当前时间，包含毫秒的时间字符串.</p>
     * <p>详细描述：.</p>
     * <p>创建时间：2019-07-18 11:16:52</p>
     * <p>创建作者：李兴武</p>
     * <p>修改记录：</p>
     *
     * @return the string
     * @author "lixingwu"
     */
    public static String nowMilliSecond() {
        return DateUtil.format(new Date(), YYYYM_MDDH_HMMSSSSS_FORMAT);
    }

    public static String simpleDate(Long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }

    public static Date getCalendar(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, -1);
        String format = sdf.format(calendar.getTime());
        Date d = null;
        try {
            d = sdf.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static String getYearMonth(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        Date mdate = calendar.getTime();
        return format.format(mdate);
    }

    public static String getDate(Date data) {
        String sdf = new SimpleDateFormat("yyyy-MM-dd").format(data);
        return sdf;
    }
}
