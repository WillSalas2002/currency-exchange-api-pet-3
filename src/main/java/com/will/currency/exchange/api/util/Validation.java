package com.will.currency.exchange.api.util;

import java.util.regex.Pattern;

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

    public static boolean isValidRate(String rate) {
        return rate != null && rate.matches("^\\d+(\\.\\d+)?$");
    }

    // Method to validate the whole currency object
    public static boolean isValidCurrency(String code, String fullName, String sign) {
        return isValidCode(code) && isValidFullName(fullName) && isValidSign(sign);
    }
}
