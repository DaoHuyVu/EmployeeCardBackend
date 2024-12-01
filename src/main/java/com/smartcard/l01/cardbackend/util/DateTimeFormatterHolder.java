package com.smartcard.l01.cardbackend.util;


import java.time.format.DateTimeFormatter;

public class DateTimeFormatterHolder {
    private static final String pattern = "HH:mm:ss dd/MM/yyyy";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    public static DateTimeFormatter dateTimeFormatter(){
        return formatter;
    }
}
