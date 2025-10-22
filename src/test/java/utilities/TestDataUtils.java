package utilities;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/**
 * Small collection of test data generators used across the framework.
 * Keep this light-weight to avoid external dependencies.
 */
public final class TestDataUtils {

    private static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%&*()-_=+[]{}<>?";
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String[] SAMPLE_FIRST_NAMES = new String[]{
            "Alex", "Jamie", "Taylor", "Jordan", "Casey", "Morgan", "Riley", "Avery", "Quinn", "Cameron"
    };

    private static final String[] SAMPLE_LAST_NAMES = new String[]{
            "Smith", "Johnson", "Brown", "Williams", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson"
    };

    private TestDataUtils() {
        // utility
    }

    // ---------- Random basic values ----------

    public static int randomInt(int minInclusive, int maxInclusive) {
        if (minInclusive > maxInclusive) {
            throw new IllegalArgumentException("minInclusive must be <= maxInclusive");
        }
        return RANDOM.nextInt(maxInclusive - minInclusive + 1) + minInclusive;
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String uniqueId(String prefix) {
        String id = prefix == null ? "" : prefix;
        return id + System.currentTimeMillis() + "-" + Math.abs(RANDOM.nextInt());
    }

    // ---------- Random strings / names / passwords ----------

    public static String randomString(int length) {
        return randomString(length, true, true, false);
    }

    public static String randomString(int length, boolean useLetters, boolean useNumbers, boolean useSymbols) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be > 0");
        }
        StringBuilder pool = new StringBuilder();
        if (useLetters) pool.append(ALPHA);
        if (useNumbers) pool.append(DIGITS);
        if (useSymbols) pool.append(SYMBOLS);
        if (pool.length() == 0) throw new IllegalArgumentException("At least one character set must be enabled");

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = RANDOM.nextInt(pool.length());
            sb.append(pool.charAt(idx));
        }
        return sb.toString();
    }

    public static String randomFirstName() {
        return SAMPLE_FIRST_NAMES[RANDOM.nextInt(SAMPLE_FIRST_NAMES.length)];
    }

    public static String randomLastName() {
        return SAMPLE_LAST_NAMES[RANDOM.nextInt(SAMPLE_LAST_NAMES.length)];
    }

    public static String randomFullName() {
        return randomFirstName() + " " + randomLastName();
    }

    public static String randomEmail() {
        // simple, unique and safe email using uuid and domain
        String local = "user" + Math.abs(RANDOM.nextInt()) + "_" + System.currentTimeMillis();
        return local + "@example.com";
    }

    public static String randomPhoneNumber() {
        // returns a 10-digit number in the format 9XXXXXXXXX (first digit 6-9 to resemble real mobile numbers)
        StringBuilder sb = new StringBuilder(10);
        int first = 6 + RANDOM.nextInt(4); // 6-9
        sb.append(first);
        for (int i = 1; i < 10; i++) sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        return sb.toString();
    }

    public static String randomPassword(int length) {
        // ensure mixture of letters, digits and symbols where possible
        if (length < 4) {
            return randomString(4, true, true, true);
        }
        StringBuilder sb = new StringBuilder(length);
        // guarantee at least one of each class
        sb.append(ALPHA.charAt(RANDOM.nextInt(ALPHA.length())));
        sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        sb.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
        // fill rest
        for (int i = 3; i < length; i++) {
            String pool = ALPHA + DIGITS + SYMBOLS;
            sb.append(pool.charAt(RANDOM.nextInt(pool.length())));
        }
        // simple shuffle
        return shuffleString(sb.toString());
    }

    private static String shuffleString(String input) {
        char[] a = input.toCharArray();
        for (int i = a.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
        return new String(a);
    }

    // ---------- Random dates ----------

    /**
     * Returns a random date between start (inclusive) and end (inclusive).
     */
    public static String randomDateBetween(String startDate, String endDate, String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        LocalDate start = LocalDate.parse(startDate, fmt);
        LocalDate end = LocalDate.parse(endDate, fmt);
        if (start.isAfter(end)) throw new IllegalArgumentException("startDate > endDate");
        long days = end.toEpochDay() - start.toEpochDay();
        long randomDay = start.toEpochDay() + (long) (RANDOM.nextDouble() * (days + 1));
        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
        return randomDate.format(fmt);
    }
}
