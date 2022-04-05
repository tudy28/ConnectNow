package socialnetwork.utils;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


public class Constants {
    public static final DateTimeFormatter DATE_TIME_FORMATTER= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_HM= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_NICE=DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
}
