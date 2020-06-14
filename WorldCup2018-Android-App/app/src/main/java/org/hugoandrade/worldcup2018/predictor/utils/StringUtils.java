package org.hugoandrade.worldcup2018.predictor.utils;

/**
 * Provides some general utility Match helper methods.
 */
public final class StringUtils {
    /**
     * Logging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = StringUtils.class.getSimpleName();

    /**
     * Ensure this class is only used as a utility.
     */
    private StringUtils() {
        throw new AssertionError();
    }

    public static String capitalize(CharSequence line) {
        return line == null ? null : capitalize(line.toString());
    }

    public static String capitalize(String line) {
        if (line == null || line.length() == 0)
            return line;

        line = Character.toUpperCase(line.charAt(0)) + line.substring(1);

        int i = line.indexOf(" ");
        if (i == -1)
            return line;
        else {
            return line.substring(0, i + 1) + capitalize(line.substring(i + 1));
        }
    }
}

