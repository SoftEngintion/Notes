package com.ws.notes.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by KanModel on 2017/12/29.
 * 时间相关方法
 */

public abstract class TimeAid {
    public static long getNowTime() {
//        return new Date().getTime();
        return System.currentTimeMillis();
    }

    /**
     * @param time 字符串类型的时间戳
     * @return 时间字符串
     */
    public static String stampToDate(String time) {
        String res;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = Long.valueOf(time);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        Log.i(TAG, "stampToDate: "+res);
        return res;
    }

    /**
     * @param time long类型的时间戳
     * @return 时间字符串
     */
    public static String stampToDate(long time) {
        return stampToDate(String.valueOf(time));
    }

    /**
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s){
        String res;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        long ts = 0;
        try {
            date = simpleDateFormat.parse(s);
            ts = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ts;
    }

    public static long getTimeStamp(int year, int month, int day, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);
        return cal.getTimeInMillis();
    }

    public static long getDiffDay(long dstTime, long nowTime) {
        return getDiff(dstTime, nowTime) / (1000 * 60 * 60 * 24);
    }

    public static long getDiffDay(long dstTime) {
        return getDiff(dstTime, getNowTime()) / (1000 * 60 * 60 * 24);
    }

    public static long getDiffHour(long dstTime, long nowTime) {
//        (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        return (getDiff(dstTime, nowTime) - getDiffDay(dstTime, nowTime) * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
    }

    public static long getDiffHour(long dstTime) {
        long nowTime = getNowTime();
        return (getDiff(dstTime, nowTime) - getDiffDay(dstTime, nowTime) * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
    }

    public static long getDiffMinutes(long dstTime, long nowTime) {
//        (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
        return (getDiff(dstTime, nowTime) - getDiffDay(dstTime, nowTime) * (1000 * 60 * 60 * 24)
                - getDiffHour(dstTime, nowTime) * (1000 * 60 * 60)) / (1000 * 60);
    }

    public static long getDiffMinutes(long dstTime) {
        long nowTime = getNowTime();
        return (getDiff(dstTime, nowTime) - getDiffDay(dstTime, nowTime) * (1000 * 60 * 60 * 24)
                - getDiffHour(dstTime, nowTime) * (1000 * 60 * 60)) / (1000 * 60);
    }

    public static long getDiff(long dstTime, long nowTime) {
        return dstTime - nowTime;
    }
}
