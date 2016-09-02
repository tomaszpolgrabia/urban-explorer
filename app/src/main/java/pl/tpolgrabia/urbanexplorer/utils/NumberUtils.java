package pl.tpolgrabia.urbanexplorer.utils;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class NumberUtils {
    public static Long safeParseLong(String s) {
        if (s == null || "".equals(s)) {
            return null;
        }

        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }

    }
}
