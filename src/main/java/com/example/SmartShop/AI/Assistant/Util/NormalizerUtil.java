package com.example.SmartShop.AI.Assistant.Util;


public class NormalizerUtil {
    public static String cleanText(String input) {
        return input.toLowerCase().replaceAll("[^a-z0-9 ]", "").trim();
    }
}
