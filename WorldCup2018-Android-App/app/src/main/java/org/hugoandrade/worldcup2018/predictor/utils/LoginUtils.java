package org.hugoandrade.worldcup2018.predictor.utils;

/**
 * Class containing static helper methods to perform login related tasks.
 */
public final class LoginUtils {
    /**
     * Ensure this class is only used as a utility.
     */
    private LoginUtils() {
        throw new AssertionError();
    }

    /**
     * This method returns true if the password is at least 8 characters long.
     */
    public static boolean isAtLeast8CharactersLong(String password) {
        return password != null && password.trim().length() >= 8;
    }

    /**
     * This method returns true if the password is not all spaces.
     */
    public static boolean isNotAllSpaces(String password) {
        return password != null && password.trim().length() > 0;
    }

    public static boolean isValid(String username, String password) {
        return LoginUtils.isAtLeast8CharactersLong(password)
                && LoginUtils.isNotAllSpaces(password)
                && LoginUtils.isNotAllSpaces(username);
    }
}
