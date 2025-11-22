package vn.ean.legia.util;

public class Validate {
    
    public static boolean isAlnum(String text) {
        return (text != null && text.matches("^[a-zA-Z0-9]+$"));
    }

    public static boolean isMarkSafe(String text) {
        return (!text.contains("'") && !text.contains("\""));
    }

    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        } else if (!isAlnum(username) || username.length() < 6 || username.length() > 30) {
            return false;
        }

        return true;
    }

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        } else if (password.length() < 6 || password.length() > 100) {
            return false;
        }

        return true;
    }
}
