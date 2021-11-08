package com.ziroom.qa.quality.defende.provider.util;

import com.ziroom.qa.quality.defende.provider.result.CustomException;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: yinm5
 * @Description: 日期工具类
 * @Date: 13:10 2019/8/2
 */
@Slf4j
public class DateUtil {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 当前系统时区偏移
     */
    public final static ZoneOffset offset = ZoneOffset.ofTotalSeconds(TimeZone.getDefault().getRawOffset() / 1000);

    /**
     * 获取当周第一天开始10位时间戳
     *
     * @param weekOffset 0 代表当周，1 代表下周 -1代表上周
     * @return long 十位时间戳
     */
    public static long getFirstDayOfCurrentWeek(int weekOffset) {
        return LocalDate.now().plusWeeks(weekOffset).with(DayOfWeek.MONDAY).atStartOfDay().toEpochSecond(offset);
    }

    /**
     * 获取星期六10位时间戳
     *
     * @param weekOffset 0 代表当周，1 代表下周 -1代表上周
     * @return
     */
    public static long getSaturdayOfCurrentWeek(int weekOffset) {
        return LocalDate.now().plusWeeks(weekOffset).with(DayOfWeek.SATURDAY).atStartOfDay().toEpochSecond(offset);
    }

    /**
     * 获取当季度第一天开始10位时间戳
     *
     * @param quarterOffset 0 代表当季度，1 代表下季度 -1代表上季度
     * @return long 十位时间戳
     */
    public static long getFirstDayOfCurrentQuarter(int quarterOffset) {
        int currentMonth = DateUtil.getCurrentMonth();
        int targetQuarter = (currentMonth - 1) / 3 + 1 + quarterOffset;
        int targetMonth = 3 * (targetQuarter - 1) + 1;
        return LocalDate.now().plusMonths(targetMonth - currentMonth).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay().toEpochSecond(offset);
    }

    /**
     * 获取当月第一天开始10位时间戳
     *
     * @param monthOffset 0 代表当月，1 代表下月 -1代表上月
     * @return long 十位时间戳
     */
    public static long getFirstDayOfCurrentMonth(int monthOffset) {
        return LocalDate.now().plusMonths(monthOffset).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay().toEpochSecond(offset);
    }

    /**
     * 获取当年第一天开始10位时间戳
     *
     * @param yearOffset 0 代表当年，1 代表下年 -1代表上年
     * @return long 十位时间戳
     */
    public static long getFirstDayOfCurrentYear(int yearOffset) {
        return LocalDate.now().plusYears(yearOffset).with(TemporalAdjusters.firstDayOfYear()).atStartOfDay().toEpochSecond(offset);
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    /**
     * 获取当前季度
     *
     * @return
     */
    public static int getCurrentQuarter() {
        return (DateUtil.getCurrentMonth() - 1) / 3 + 1;
    }

    /**
     * 获取当前年份
     *
     * @return
     */
    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    /**
     * 获取当前周数
     *
     * @return
     */
    public static int getCurrentWeek() {
        LocalDate localDate = LocalDate.now();
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 4);
        return localDate.get(weekFields.weekOfWeekBasedYear());
    }


    /**
     * 获取星期五10位时间戳
     *
     * @param weekOffset 0 代表当周，1 代表下周 -1代表上周
     * @return
     */
    public static long getMondayOfCurrentWeek(int weekOffset) {
        return LocalDate.now().plusWeeks(weekOffset).with(DayOfWeek.MONDAY).atStartOfDay().toEpochSecond(offset);
    }


    /**
     * 将UTC时间转换为GST(北京时间)
     *
     * @param date
     */
    public static String convertUtC2GST(String date) throws ParseException {
        String dateTime = date.replace("Z", " UTC");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        Date time = simpleDateFormat.parse(dateTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String resultTime = dateFormat.format(time);
        return resultTime;
    }

    /**
     * 将末尾为+0800的时间转换为日期时间
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String convertUtC2GST2(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+0800");
        Date time = simpleDateFormat.parse(date);
        String resultTime = sdf.format(time);
        return resultTime;
    }

    /**
     * 将传入日期增加 N 天
     *
     * @return
     */
    public static String addSomeDay(String dateTime, int n) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = null;
        try {
            dt = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.DAY_OF_YEAR, n);
        Date resultDate = rightNow.getTime();
        return sdf.format(resultDate);
    }

    /**
     * 获取当前时间日期指定格式
     *
     * @param format 例如："yyyyMMdd HH:mm:ss"
     * @return
     */

    public static String dateStr(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(System.currentTimeMillis());
    }

    /**
     * 已知月份获取当前月每周的开始和结束时间
     */
    public static List<Map<String,String>> weeksByMouth(int year, int month) {
        List<Map<String,String>> list = new ArrayList<>();
        LocalDate start = LocalDate.now().withYear(year).withMonth(month).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().withYear(year).withMonth(month).with(TemporalAdjusters.lastDayOfMonth());
        Map<Integer, LocalDateWeekData> map = Stream.iterate(start, localDate -> localDate.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .collect(Collectors.groupingBy(localDate -> localDate.get(WeekFields.of(DayOfWeek.MONDAY, 1).weekOfMonth()),
                        Collectors.collectingAndThen(Collectors.toList(), LocalDateWeekData::new)));


        for(Integer weekNum :map.keySet()) {
            Map<String,String> weekBetween =  new HashMap<>();
            LocalDateWeekData e = map.get(weekNum);
            LocalDate weekStart = e.getStart();
            LocalDate weekEnd = e.getEnd();
            long startTimeStep = weekStart.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
            long endTimeStep = weekEnd.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
            weekBetween.put("startTime",String.valueOf(startTimeStep));
            weekBetween.put("endTime",String.valueOf(endTimeStep));
            list.add(weekBetween);
        }
        return  list;
    }

    /**
     * 时间戳转固定日期格式
     *
     */
    public static String  timeStepToDate (String timeStemp,String dateFormat) {
        String res = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        long lt = new Long(timeStemp);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;

    }

    /**
     * 指定开始时间与结束时间之间的所有日期
     * @param startTime 日期格式为yyyy-MM-dd
     * @param endTime
     * @return
     */
    public static List<String> getBetweenTime(String startTime,String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        List<String> betweenTimeList = new ArrayList<>();
        try {

            cal.setTime(sdf.parse(startTime));
            for (long d = cal.getTimeInMillis(); d <= sdf.parse(endTime).getTime(); d = getPlaus(cal)) {
                betweenTimeList.add(sdf.format(d));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return betweenTimeList;

    }

    public static long getPlaus(Calendar c) {
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
        return c.getTimeInMillis();
    }


    /**
     * 指定时间类型的字符串转换为时间戳
     *
     * @param tsStr
     * @return
     */
    public static String stringToStamp(String tsStr,String dateFormat) {

        DateFormat format = new SimpleDateFormat(dateFormat);
        String ts;

        try {
            Date date = format.parse(tsStr);
            ts = String.valueOf(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("日期格式转换失败！");

        }
        return ts;
    }






}
