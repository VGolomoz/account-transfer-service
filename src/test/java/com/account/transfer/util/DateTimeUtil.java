package com.account.transfer.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX");

    public static String formatToString(ZonedDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}
