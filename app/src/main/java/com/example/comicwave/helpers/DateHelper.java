package com.example.comicwave.helpers;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
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
}
