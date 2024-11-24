package com.example.comicwave.helpers;

public class NumberHelper {
    public static String format(int number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 1000000) {
            return (number / 1000) + "K";
        } else if (number < 1000000000) {
            return (number / 1000000) + "M";
        } else {
            return (number / 1000000000) + "B";
        }
    }
}
