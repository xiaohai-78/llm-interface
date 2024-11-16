package com.xiaohai.newsassistant.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {

    public static void main(String[] args) {
        System.out.println(getWxTitleTime());
    }

    public static SimpleDateFormat SDFYMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat SDFSHORT = new SimpleDateFormat("yyyyMMdd");

    /**
     * 获取当天0点整时间戳
     * @param epochSecond
     * @return
     */
    public static long getTodayInt(long epochSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(epochSecond * 1000L);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis() / 1000L;
    }

    public static long getTimestamp() {
        return getLong() / 1000L;
    }

    public static long getLong() {
        Date dt = new Date();
        return dt.getTime();
    }

    /**
     * 获取当前格式化的时间
     *
     * @param format 格式化方式
     * @return 格式化的时间
     */
    public static String getString(SimpleDateFormat format) {
        return format.format(new Date());
    }

    /**
     * 获取当前格式化的时间 格式：yyyy-MM-dd HH:mm
     *
     * @return 格式化的时间
     */
    public static String getString() {
        return getString(SDFYMDHM);
    }

    /**
     * 获取当前时间，格式yyyyMMdd
     */
    public static String getStringShort() {
        return getString(SDFSHORT);
    }

    /**
     * 获取微信公众号标题日期
     * 如果是21点之前，则返回当天日期
     * 如果是21点之后，则返回第二天日期
     * @return
     */
    public static String getWxTitleTime(){
        // 获取当前日期
        String dateStr = DateTimeUtil.getStringShort();
        // 获取当前时间戳
        long timestamp = DateTimeUtil.getTimestamp();
        // 当天0点
        long todayInt = DateTimeUtil.getTodayInt(timestamp);
        // 如果时间没到21点，则返回第二天日期
        if(timestamp > todayInt + 21 * 60 *60){
            dateStr = DateTimeUtil.getStringEpochSecond(todayInt + 25 * 60 *60, DateTimeUtil.SDFSHORT);
        }
        return dateStr;
    }

    /**
     * 获取格式化的时间
     *
     * @param epochSecond 需要格式化的时间 精确到秒的时间戳
     * @param format      格式化方式
     * @return 格式化的时间
     */
    public static String getStringEpochSecond(Long epochSecond, SimpleDateFormat format) {
        return epochSecond == null || epochSecond == 0 ? null : getString(1000L * epochSecond, format);
    }

    /**
     * 获取格式化的时间
     *
     * @param epochMilli 需要格式化的时间 精确到毫秒的时间戳
     * @param format     格式化方式
     * @return 格式化的时间
     */
    public static String getString(Long epochMilli, SimpleDateFormat format) {
        if (epochMilli == null) {
            return null;
        }
        if (epochMilli == 0) {
            return "";
        }
        return format.format(new Date(epochMilli));
    }

    /**
     *  判断需要获取新闻的日期，如果时间没到21点，则返回昨天日期
     * @return
     */
    public static String getNewsTime(){
        // 获取当前日期
        String dateStr = DateTimeUtil.getStringShort();
        // 获取当前时间戳
        long timestamp = DateTimeUtil.getTimestamp();
        // 当天0点
        long todayInt = DateTimeUtil.getTodayInt(timestamp);
        // 如果时间没到22点，则返回昨天日期
        if(timestamp < todayInt + 21 * 60 *60){
            dateStr = DateTimeUtil.getStringEpochSecond(todayInt - 1, DateTimeUtil.SDFSHORT);
        }
        return dateStr;
    }
}
