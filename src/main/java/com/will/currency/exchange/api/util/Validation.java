package com.will.currency.exchange.api.util;

public class Validation {

    // Method to validate currency code
    public static boolean isValidCode(String code) {
        return code != null && code.matches("[A-Z]{3}");
    }

    // Method to validate full name
    public static boolean isValidFullName(String fullName) {
        return fullName != null && fullName.length() > 1 && fullName.length() <= 100;
    }

    // Method to validate sign
    public static boolean isValidSign(String sign) {
        return sign != null && sign.length() == 1;
    }

    // Method to validate the whole currency object
    public static boolean isValidCurrency(String code, String fullName, String sign) {
        return isValidCode(code) && isValidFullName(fullName) && isValidSign(sign);
    }
}
