package com.example.achuan.teamsystem.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by achuan on 17-5-20.
 * 参考链接：http://blog.csdn.net/zhq217217/article/details/51781647
 * 功能：时间相关的处理方法
 */

public class DateUtil {

    /**
     * 判断是否为今天(效率比较高)
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsToday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());//当天日期
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();
    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

    /**
     * 获取当天日期的时间范围
     * 参数介绍：isStart 为true时,得到今天凌晨0点时间;为false时,得到今天晚上24点时间
     * */
    public static Date getTodayRange(boolean isStart){
        //先获取当前时间
        long time=System.currentTimeMillis();
        //配置时间格式(年-月-日)
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //变成日期实例
        Date predate=new Date(time);
        //转化成字符串形式
        String today=sdf1.format(predate);//先拿到：年-月-日
        String start=today+" 00:00:00";
        String end=today+" 23:59:59";
        try {
            if(isStart){
                return sdf2.parse(start);
            }else {
                return sdf2.parse(end);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
