package org.hugoandrade.worldcup2018.predictor.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Helper class for handling a most common subset of ISO 8601 strings
 * (in the following format: "2008-03-01T13:00:00+01:00"). It supports
 * parsing the "Z" timezone, but many other less-used features are
 * missing.
 */
public final class ISO8601 {
    /** Transform Calendar to ISO 8601 string. */
    public static String fromCalendar(final Calendar calendar) {
        return fromDate(calendar.getTime());
    }

    public static String fromDate(Date date) {
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.UK)
                .format(date);
        return formatted.substring(0, 26) + ":" + formatted.substring(26);
    }

    /** Transform ISO 8601 string to Date. */
    /*public static Date toDate(final String iso8601string) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.UK);
        try {
            return sdf.parse(iso8601string);
        } catch (ParseException e) {
            Log.e(ISO8601.class.getSimpleName(), e.toString());
            return null;
        }
    }/**/
    public static Date toDate(final String iso8601string) {
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.UK);
        try {
            //return sdf.parse(iso8601string);
            return sdf.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }/**/

    public static Calendar toCalendar(String iso8601string) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(toDate(iso8601string));
        } catch (NullPointerException e ) {
            Log.e(ISO8601.class.getSimpleName(), e.toString());
            return null;
        }
        return c;
    }
}
