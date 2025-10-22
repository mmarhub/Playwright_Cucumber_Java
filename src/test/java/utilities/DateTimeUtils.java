package utilities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Date and Time utility helpers for tests.
 * Uses java.time (Java 8+) APIs.
 */
public final class DateTimeUtils {

    private DateTimeUtils() {
        // utility
    }

    // ---------- Current date/time ----------

    public static String now(String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return LocalDateTime.now().format(fmt);
    }

    public static String today(String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return LocalDate.now().format(fmt);
    }

    public static long nowEpochMillis() {
        return Instant.now().toEpochMilli();
    }

    // ---------- Future / Past helpers ----------

    public static String plusDaysFromNow(int days, String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return LocalDate.now().plusDays(days).format(fmt);
    }

    public static String minusDaysFromNow(int days, String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return LocalDate.now().minusDays(days).format(fmt);
    }

    public static String plusMonthsFromNow(int months, String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return LocalDate.now().plusMonths(months).format(fmt);
    }

    public static String minusMonthsFromNow(int months, String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return LocalDate.now().minusMonths(months).format(fmt);
    }

    public static String plusYearsFromNow(int years, String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return LocalDate.now().plusYears(years).format(fmt);
    }

    public static String minusYearsFromNow(int years, String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return LocalDate.now().minusYears(years).format(fmt);
    }

    // ---------- Convert between formats ----------

    /**
     * Converts a date/time string from one pattern to another.
     * Tries to parse as LocalDate first (date-only), then falls back to LocalDateTime.
     */
    public static String convert(String input, String fromPattern, String toPattern) {
        if (input == null) return null;
        DateTimeFormatter from = DateTimeFormatter.ofPattern(fromPattern, Locale.ENGLISH);
        DateTimeFormatter to = DateTimeFormatter.ofPattern(toPattern, Locale.ENGLISH);

        try {
            LocalDate d = LocalDate.parse(input, from);
            return d.format(to);
        } catch (DateTimeParseException ignored) {
            // try LocalDateTime
        }

        LocalDateTime dt = LocalDateTime.parse(input, from);
        return dt.format(to);
    }

    // ---------- Time zone helpers ----------

    public static String zonedNow(String pattern, String zoneId) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        ZonedDateTime z = ZonedDateTime.now(ZoneId.of(zoneId));
        return z.format(fmt);
    }

    public static String fromEpochMillis(long epochMillis, String pattern, String zoneId) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH).withZone(ZoneId.of(zoneId));
        return fmt.format(Instant.ofEpochMilli(epochMillis));
    }
}
