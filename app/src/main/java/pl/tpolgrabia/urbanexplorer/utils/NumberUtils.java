package pl.tpolgrabia.urbanexplorer.utils;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class NumberUtils {
    public static Long safeParseLong(CharSequence charSequence) {
        if (charSequence == null) {
            return 1L;
        }

        try {
            return Long.parseLong(charSequence.toString());
        } catch (NumberFormatException e) {
            return 1L;
        }

    }
}
