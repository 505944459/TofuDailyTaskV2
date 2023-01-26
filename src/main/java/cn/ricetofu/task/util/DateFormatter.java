package cn.ricetofu.task.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: RiceTofu123
 * @Date: 2023-01-22
 * @Discription: 统一日期解析工具类
 * */
public class DateFormatter {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 将一个日期对象转为格式化日期字符串
     * @return 格式化后的日期字符串
     * */
    public static String format(Date date){
        return sdf.format(date);
    }

    /**
     * 将一个时间字符串转换为日期对象
     * @return 日期对象
     * */
    public static Date parse(String str){
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
