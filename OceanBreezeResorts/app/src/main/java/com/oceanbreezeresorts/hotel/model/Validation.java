package com.oceanbreezeresorts.hotel.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@#$%^&+=]).{8,}$";
    private static final String MOBILE_REGEX = "^(?:\\+94|0)(7[01245678])\\d{7}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z]+[0-9]*@[a-zA-Z]+\\.[a-zA-Z]{2,}$";

    public static boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public static boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isValidMobile(String mobile) {
        Pattern pattern = Pattern.compile(MOBILE_REGEX);
        Matcher matcher = pattern.matcher(mobile);
        return matcher.matches();
    }

}
