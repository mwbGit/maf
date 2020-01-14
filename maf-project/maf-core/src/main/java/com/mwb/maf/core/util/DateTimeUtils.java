package com.mwb.maf.core.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;

/**
 * - 日期工具类
 */
public class DateTimeUtils {

    enum DateFormatType {

        /**
         * 时间格式
         */
        YYYYMMDDHHMMSS_NOSPACE("yyyyMMddHHmmss"),
        YYYYMMDDHHMMSS("yyyy-MM-dd HH:mm:ss"),
        YYYYMMDDHHMM("yyyy-MM-dd HH:mm"),
        YYYYMMDD("yyyy-MM-dd"),
        YYYYMMDD_SLASH("yyyy/MM/dd"),
        YYYYMMDD_NOSPACE("yyyyMMdd"),
        YYYYMM("yyyy-MM"),
        YYYYMM_CH("yyyy年MM月"),
        YYYYMMDDHHMMSS_CH("yyyy年MM月dd日 HH:mm:ss"),
        YYYYMMDDHHMM_CH("yyyy年MM月dd日 HH:mm"),
        MMDDHHMM_CH("MM月dd日 HH:mm"),
        YYYYMM_SEPARATOR("yyyy-MM"),
        DDHHMMSS("ddHHmmss"),
        MMDDHHMM("MM-dd HH:mm"),
        HHMMSS("HH:mm:ss"),
        HHMM("HH:mm");

        private String pattern;

        DateFormatType(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
    }

    private static Date parse(String date, DateFormatType dateFormatType) {
        return DateTime.parse(date, DateTimeFormat.forPattern(dateFormatType.pattern)).toDate();
    }

    /**
     * 字符串转日期
     *
     * @param date
     * @return
     */
    public static Date parseHHMMSS(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        return parse(date, DateFormatType.HHMMSS);
    }

    /**
     * 字符串转日期
     *
     * @param date
     * @return
     */
    public static Date parseYYYYMMDD(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        return parse(date, DateFormatType.YYYYMMDD);
    }

    /**
     * 字符串转日期
     *
     * @param date
     * @return
     */
    public static Date parseYYYYMMDD_SLASH(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        return parse(date, DateFormatType.YYYYMMDD_SLASH);
    }

    /**
     * 字符串转日期，格式为"yyyyMMdd"
     */
    public static Date parsrYYYYMMDDNoSpace(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        return parse(date, DateFormatType.YYYYMMDD_NOSPACE);
    }

    /**
     * 字符串转日期
     *
     * @param date
     * @return
     */
    public static Date parseYYYYMMDDHHMMSS(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        return parse(date, DateFormatType.YYYYMMDDHHMMSS);
    }

    private static String formatByDate(DateFormatType dateFormatType, Date date) {
        return new DateTime(date).toString(dateFormatType.pattern);
    }

    /**
     * 年月格式
     *
     * @param date
     * @return
     */
    public static String formatYYYYMM(Date date) {
        if (date == null) {
            return "";
        }
        return formatByDate(DateFormatType.YYYYMM, date);
    }

    /**
     * 年月格式
     *
     * @param date
     * @return
     */
    public static String formatYYYYMMCH(Date date) {
        if (date == null) {
            return "";
        }
        return formatByDate(DateFormatType.YYYYMM, date);
    }

    /**
     * 年月日格式
     *
     * @param date
     * @return
     */
    public static String formatYYYYMMDD(Date date) {
        if (date == null) {
            return "";
        }
        return formatByDate(DateFormatType.YYYYMMDD, date);
    }

    /**
     * 年/月/日格式
     *
     * @param date
     * @return
     */
    public static String formatYYYYMMDD_SLASH(Date date) {
        if (date == null) {
            return "";
        }
        return formatByDate(DateFormatType.YYYYMMDD_SLASH, date);
    }

    /**
     * 年月日时分秒格式
     *
     * @param date
     * @return
     */
    public static String formatYYYYMMDDHHMMSS(Date date) {
        if (date == null) {
            return "";
        }

        return formatByDate(DateFormatType.YYYYMMDDHHMMSS, date);
    }

    /**
     * 年月日时分格式
     *
     * @param date
     * @return
     */
    public static String formatYYYYMMDDHHMM(Date date) {
        if (date == null) {
            return "";
        }

        return formatByDate(DateFormatType.YYYYMMDDHHMM, date);
    }

    /**
     * 年月日时分秒格式
     * 中文格式
     *
     * @param date
     * @return
     */
    public static String formatYYYYMMDDHHMMSS_CN(Date date) {
        if (date == null) {
            return "";
        }

        return formatByDate(DateFormatType.YYYYMMDDHHMMSS_CH, date);
    }

    /**
     * 年月日时分格式
     *
     * @param date
     * @return
     */
    public static String formatHHMMSS(Date date) {
        if (date == null) {
            return "";
        }

        return formatByDate(DateFormatType.HHMMSS, date);
    }

    /**
     * 当天日期字符串
     *
     * @return
     */
    public static String getDailyVersion() {
        return formatByDate(DateFormatType.YYYYMMDD_NOSPACE, new Date());
    }

    /**
     * 当天日期字符串
     *
     * @return
     */
    public static String getDailyVersion(Date date) {
        return formatByDate(DateFormatType.YYYYMMDD_NOSPACE, date);
    }

    /**
     * 加上指定小时
     *
     * @param date
     * @param hours
     * @return
     */
    public static Date addHours(Date date, int hours) {
        return new DateTime(date).plusHours(hours).toDate();
    }

    /**
     * 加上指定天数
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(Date date, int days) {
        return new DateTime(date).plusDays(days).toDate();
    }

    /**
     * 减去指定天数
     *
     * @param date
     * @param days
     * @return
     */
    public static Date minusDays(Date date, int days) {
        return new DateTime(date).minusDays(days).toDate();
    }

    /**
     * 两个日期相差天数
     *
     * @param smallDate
     * @param bigDate
     * @return
     */
    public static int daysBetween(Date smallDate, Date bigDate) {
        smallDate = parseYYYYMMDD(formatYYYYMMDD(smallDate));
        bigDate = parseYYYYMMDD(formatYYYYMMDD(bigDate));
        return daysBetween(new DateTime(smallDate), new DateTime(bigDate));
    }

    /**
     * 两个日期相差天数
     *
     * @param smallDate
     * @param bigDate
     * @return
     */
    public static int daysBetween(DateTime smallDate, DateTime bigDate) {
        return Days.daysBetween(smallDate, bigDate).getDays();
    }

    /**
     * 年月日格式
     *
     * @param date
     * @return
     */
    public static String formatYYYYMMDDNOSPACE(Date date) {
        if (date == null) {
            return "";
        }
        return formatByDate(DateFormatType.YYYYMMDD_NOSPACE, date);
    }

    /**
     * app展示日期
     * 今天11：22、昨天11：22、10月11：22、2019年10月11：22
     * @param date
     * @return
     */
    public static String getAppDateDesc(Date date) {
        if (date == null) {
            return "";
        }
        DateTime dateTime = new DateTime(date);
        DateTime now = new DateTime();

        if (now.getDayOfYear() == dateTime.getDayOfYear()
            && now.getYear() == dateTime.getYear()) {
            return "今天" + formatByDate(DateFormatType.HHMM, date);
        }

        if (now.getDayOfYear() - 1 == dateTime.getDayOfYear()
                && now.getYear() == dateTime.getYear()) {
            return "昨天" + formatByDate(DateFormatType.HHMM, date);
        }

        if (now.getYear() == dateTime.getYear()) {
            return formatByDate(DateFormatType.MMDDHHMM_CH, date);
        }

        return formatByDate(DateFormatType.YYYYMMDDHHMM_CH, date);
    }

}
