package com.transactionservice.transactionservicedeelaa.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberValidator {

    private static final Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]*");

    public static boolean isValidNum(String num) {

        Matcher matcher = pattern.matcher(num);
        return matcher.matches();
    }
}
