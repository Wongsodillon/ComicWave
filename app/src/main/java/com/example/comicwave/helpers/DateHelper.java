package com.example.comicwave.helpers;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    public static String format(Timestamp timestamp) {
        if (timestamp == null) {
            return "Unknown date";
        }
        Date date = timestamp.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return formatter.format(date);
    }

    public static boolean isReleased(Timestamp timestamp) {
        long currentTimeMillis = System.currentTimeMillis();

        long timestampMillis = timestamp.getSeconds() * 1000;

        return timestampMillis <= currentTimeMillis;
    }

    public static String whatDayIsToday() {
        LocalDate currentDate = LocalDate.now();

        String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        return dayOfWeek;
    }
}
