package com.honeycomb.helper;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Created by Ash on 20/02/2017.
 */

public class Time
{
    /**
     * Gets the time until a given deadline
     * @param deadline
     * @return
     */
    public static String untilDeadline(DateTime deadline)
    {
        Period p = new Period(new DateTime(), deadline);

        PeriodFormatter pFormatter = new PeriodFormatterBuilder()
                .appendYears()
                .appendSuffix(" year", " years")
                .appendSeparator(", ")
                .appendMonths()
                .appendSuffix(" month", " months")
                .appendSeparator(", ")
                .appendWeeks()
                .appendSuffix(" week", " weeks")
                .appendSeparator(", ")
                .appendDays()
                .appendSuffix(" day", " days")
                .appendSeparator(", ")
                .appendHours()
                .appendSuffix(" hour", " hours")
                .appendSeparator(" and ")
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .toFormatter();

        return pFormatter.print(p.normalizedStandard());
    }

    /**
     * Human-readable date
     * @param dateTime
     * @return
     */
    public static String toWordyReadable(DateTime dateTime)
    {
        DateTimeFormatter dtFormatter = new DateTimeFormatterBuilder()
                .appendDayOfWeekShortText()
                .appendLiteral(" ")
                .appendDayOfMonth(1)
                .appendLiteral(", ")
                .appendMonthOfYearText()
                .appendLiteral(" ")
                .appendYear(4, 4)
                .appendLiteral(" at ")
                .appendHourOfDay(2)
                .appendLiteral(":")
                .appendMinuteOfHour(2)
                .toFormatter();

        return dtFormatter.print(dateTime);
    }

    /**
     * Shorter human-readable date
     * @param dateTime
     * @return
     */
    public static String toShortWordyReadable(DateTime dateTime)
    {
        DateTimeFormatter dtFormatter = new DateTimeFormatterBuilder()
                .appendDayOfWeekShortText()
                .appendLiteral(" ")
                .appendDayOfMonth(1)
                .appendLiteral(", ")
                .appendMonthOfYearShortText()
                .appendLiteral(" ")
                .appendYear(4, 4)
                .appendLiteral(" at ")
                .appendHourOfDay(2)
                .appendLiteral(":")
                .appendMinuteOfHour(2)
                .toFormatter();

        return dtFormatter.print(dateTime);
    }
}
