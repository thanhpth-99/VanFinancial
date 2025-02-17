package van.vanfinancial.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static Date parseDate(String dateString) throws ParseException {
        LocalDate localDate = LocalDate
                .parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return Date.valueOf(localDate);
    }

    public static Timestamp convertDateToTimestamp(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return Timestamp.from(date.toInstant());
    }
}
