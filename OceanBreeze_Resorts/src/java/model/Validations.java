package model;

public class Validations {

    public static boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
    }

    public static boolean isMobileNumberValid(String mobile) {
        return mobile.matches("^07[012345678]{1}[0-9]{7}$");
    }

    public static boolean isDouble(String price) {

        return price.matches("^\\d+(\\.\\d{2})?$");

    }

    public static boolean isInteger(String value) {

        return value.matches("^\\d+$");

    }
}
