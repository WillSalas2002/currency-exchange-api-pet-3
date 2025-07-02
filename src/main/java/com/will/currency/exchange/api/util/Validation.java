package com.will.currency.exchange.api.util;

import java.util.Currency;

public class Validation {

    public static boolean isValidCode(String code) {
        if (code == null) return false;
        try {
            Currency.getInstance(code.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isValidFullName(String fullName) {
        return fullName != null && fullName.length() > 1 && fullName.length() <= 100;
    }

    public static boolean isValidSign(String sign) {
        return sign != null && sign.length() == 1;
    }

    public static boolean isValidRate(String rate) {
        return rate == null || !rate.matches("^\\d+(\\.\\d+)?$");
    }

    public static boolean isValidExchangeRatePath(String path) {
        return path == null || !(path.length() == 6);
    }
}
