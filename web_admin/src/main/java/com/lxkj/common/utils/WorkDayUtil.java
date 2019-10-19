package com.lxkj.common.utils;

import com.lxkj.common.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WorkDayUtil {

    // 获取两个日期的之间的
    public static List<DayWithWeek> getDayWithWeeks(String startString, String endString){
        List<DayWithWeek> result = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = simpleDateFormat.parse(startString);
            endDate = simpleDateFormat.parse(endString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        while(startCal.getTimeInMillis() <= endDate.getTime()){
            int week = startCal.get(Calendar.DAY_OF_WEEK)-1; // 周日期
            Date date = startCal.getTime();
            DayWithWeek dayWithWeek = new DayWithWeek();
            dayWithWeek.setWeek(week);
            dayWithWeek.setDate(date);
            result.add(dayWithWeek);
            startCal.add(Calendar.DATE, 1);
        }
        return result;
    }

    public static void main(String[] args) {
        List<DayWithWeek> dayWithWeeks = new WorkDayUtil().getDayWithWeeks("2018-12-01", "2018-12-31");
        dayWithWeeks.forEach(p->{
            System.out.println(p.getWeek()+":"+DateUtil.DateToString(p.getDate(),"yyyy-MM-dd"));
        });
    }
}
