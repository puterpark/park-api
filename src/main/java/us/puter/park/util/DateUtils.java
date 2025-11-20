package us.puter.park.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    /**
     * 문자열 -> LocalDateTime
     * @param date
     * @param pattern
     * @return
     */
    public static LocalDateTime toLdt(String date, String pattern) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * LocalDateTime -> 문자열
     * @param date
     * @param pattern
     * @return
     */
    public static String toString(LocalDateTime date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 문자열 -> LocalDate
     * @param date
     * @param pattern
     * @return
     */
    public static LocalDate toLd(String date, String pattern) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * LocalDate -> 문자열
     * @param date
     * @param pattern
     * @return
     */
    public static String toString(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
}
