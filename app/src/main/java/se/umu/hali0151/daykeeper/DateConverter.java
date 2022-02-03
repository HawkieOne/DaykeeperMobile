package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Converts the type Date so it can be used in a Room database
 */

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {

    /**
     * Get the Date from the database
     * @param value
     * @return the date in Date format
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    /**
     * Get the Date in the database
     * @param date
     * @return the date in long format
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
