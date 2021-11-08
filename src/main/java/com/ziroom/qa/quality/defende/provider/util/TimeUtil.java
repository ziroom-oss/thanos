package com.ziroom.qa.quality.defende.provider.util;

import com.ziroom.qa.quality.defende.provider.vo.TimeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TimeUtil {

    public static DateTimeFormatter sf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter newSf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


    public static TimeVo parseTimeStr(String timeVal) {
        TimeVo timeVo = new TimeVo();
        try {
            String[] timeArray = timeVal.split(",");
            timeVo.setStart(LocalDateTime.parse(timeArray[0] + ":00", sf));
            timeVo.setEnd(LocalDateTime.parse(timeArray[1] + ":00", sf).plusDays(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeVo;
    }

    public static TimeVo parseDateStr(String timeVal) {
        TimeVo timeVo = new TimeVo();
        try {
            String[] timeArray = timeVal.split(",");
            timeVo.setStart(LocalDateTime.parse(timeArray[0] + " 00:00:00", sf));
            timeVo.setEnd(LocalDateTime.parse(timeArray[1] + " 00:00:00", sf).plusDays(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeVo;
    }

    /**
     * 解析日期，如果为空，则取一天前的日期
     *
     * @param dateTimeStr 日期格式为（yyyy-mm-dd,yyyy-mm-dd）
     * @param lastDay     前x天到现在
     * @return
     */
    public static TimeVo getDateTimeByStr(String dateTimeStr, int lastDay) {
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (StringUtils.isBlank(dateTimeStr)) {
            startTime = LocalDateTime.of(LocalDateTime.now().plusDays(-lastDay).toLocalDate(), LocalTime.MIN);
            endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
        } else {
            TimeVo timeVo = TimeUtil.parseDateStr(dateTimeStr);
            startTime = timeVo.getStart().plusDays(-lastDay);
            endTime = timeVo.getEnd().plusDays(-lastDay);
        }
        return TimeVo.builder().start(startTime).end(endTime).build();
    }

    /**
     * 获取当前日期的 day 天后的日期，格式 yyyy-MM-dd
     *
     * @param dateTimeStr
     * @param day
     * @return
     */
    public static String getDateTimeStrByDay(String dateTimeStr, int day) {
        LocalDateTime dateTime;
        if (StringUtils.isNotBlank(dateTimeStr)) {
            dateTime = LocalDateTime.of(LocalDateTime.parse(dateTimeStr, sf).plusDays(day).toLocalDate(), LocalTime.MIN);
        } else {
            dateTime = LocalDateTime.of(LocalDateTime.now().plusDays(day).toLocalDate(), LocalTime.MIN);
        }
        return dateFormat.format(dateTime);
    }

    public static List<LocalDate> getDateList(String start, String end) {
        List<LocalDate> list = new ArrayList<>();

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        long distance = ChronoUnit.DAYS.between(startDate, endDate);
        log.info("两个日期相差天数为：{}", distance);
        if (distance < 0) {
            return list;
        }
        list = Stream.iterate(startDate, d -> d.plusDays(1)).limit(distance + 1).collect(Collectors.toList());
        return list;
    }

    public static void main(String[] args) {
        String start = "2021-09-11";
        String end = "2021-09-21";
        System.out.println(getDateList(start, end));
    }


}
