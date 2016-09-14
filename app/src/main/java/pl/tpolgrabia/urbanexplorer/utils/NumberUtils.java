package pl.tpolgrabia.urbanexplorer.utils;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class NumberUtils {
    public static Long safeParseLong(String s) {
        if (s == null || "".equals(s.trim())) {
            return null;
        }

        String trimmed = s.trim();

        try {
            return Long.parseLong(trimmed);
        } catch (NumberFormatException e) {
            return null;
        }

    }

    public static Double safeParseDouble(String s) {
        if (s == null || "".equals(s.trim())) {
            return null;
        }

        String trimmed = s.trim();

        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Float safeParseFloat(String s) {
        if (s == null || "".equals(s.trim())) {
            return null;
        }

        String trimmed = s.trim();

        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
