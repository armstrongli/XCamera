package com.xxboy.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonFunction {
    public static final String DATE_FORMAT = "yyyyMMdd_hhmmss_SSS";

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public static String getCurrentDateString() {
        return simpleDateFormat.format(new Date());
    }
}
