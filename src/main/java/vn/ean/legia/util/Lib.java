package vn.ean.legia.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.security.MessageDigest;

@Component
public class Lib {

    public static Object getSession(HttpServletRequest request, String key) {
        return request.getSession().getAttribute(key);
    }

    public static void setSession(HttpServletRequest request, String key, Object value) {
        request.getSession(true).setAttribute(key, value);
    }

    public static void setCookie(HttpServletResponse response, String key, String value, int expired) {
        Cookie cookie = new Cookie(key, value);

        cookie.setPath("/");
        cookie.setMaxAge(expired);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);

        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public static String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String randStr(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }

    public static Long dateFormConvert(String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toEpochSecond();
    }

    public static String timestampConvert(Long timestamp, String format) {
        if (timestamp == 9999999999L) {
            return "Vĩnh viễn";
        }
        
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.of("Asia/Ho_Chi_Minh")).format(DateTimeFormatter.ofPattern(format));
    }

    public static Long currentTimestamp() {
        return LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toEpochSecond();
    }

    public static String timeSince(Long seconds, int levels) {
        long months = seconds / (30L * 24 * 3600);
        seconds %= 30L * 24 * 3600;

        long days = seconds / (24 * 3600);
        seconds %= 24 * 3600;

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        seconds %= 60;

        String fullArgs = "";

        if (months > 0)
            fullArgs += (months + "th ");
        if (days > 0)
            fullArgs += (days + "ng ");
        if (hours > 0)
            fullArgs += (hours + "g ");
        if (minutes > 0)
            fullArgs += (minutes + "p ");

        fullArgs += (seconds + "s ");
        String[] timeArgs = fullArgs.split(" ");

        if (timeArgs.length > levels) {
            return String.join(" ", Arrays.copyOfRange(timeArgs, 0, levels)).trim();
        }

        return fullArgs.trim();
    }

    public static Long getTimestampAt(String input) {
        ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");

        LocalDate today = LocalDate.now(vnZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(input, formatter);
        LocalDateTime dateTime = LocalDateTime.of(today, time);
        ZonedDateTime zdt = dateTime.atZone(vnZone);

        return zdt.toEpochSecond();
    }

    public static Map<String, Map<String, Long>> getMonthsMapFromPast(int previous) {
        ZoneId zoneVN = ZoneId.of("Asia/Ho_Chi_Minh");

        LocalDate today = LocalDate.now(zoneVN);
        LocalDate firstDayPrevMonth = today.minusMonths(1).withDayOfMonth(previous);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

        Map<String, Map<String, Long>> result = new LinkedHashMap<>();
        LocalDate date = today;

        while (!date.isBefore(firstDayPrevMonth)) {
            ZonedDateTime startOfDay = date.atStartOfDay(zoneVN);
            ZonedDateTime endOfDay = date.atTime(23, 59, 59).atZone(zoneVN);

            long startSec = startOfDay.toEpochSecond();
            long endSec = endOfDay.toEpochSecond();

            Map<String, Long> times = new HashMap<>();
            times.put("start", startSec);
            times.put("end", endSec);

            result.put(date.format(fmt), times);

            date = date.minusDays(1);
        }

        return result;
    }

    public static Map<String, Long> getMonthTimestamps(String month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/yyyy");
        YearMonth ym = YearMonth.parse(month, formatter);
        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");

        ZonedDateTime startOfMonth = ym.atDay(1).atStartOfDay(zone);
        ZonedDateTime endOfMonth = ym.atEndOfMonth().atTime(23, 59, 59).atZone(zone);

        Map<String, Long> result = new HashMap<>();
        result.put("start", startOfMonth.toEpochSecond());
        result.put("end", endOfMonth.toEpochSecond());

        return result;
    }

    public static Long getTodayTimestampByHHMM(String input) {
        LocalTime time = LocalTime.parse(input);
        LocalDate today = LocalDate.now();
        LocalDateTime dateTime = LocalDateTime.of(today, time);

        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }
}
