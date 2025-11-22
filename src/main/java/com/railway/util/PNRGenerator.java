package com.railway.util;

import java.util.Random;

public class PNRGenerator {
    private static final String PREFIX = "PNR";
    private static final Random random = new Random();

    public static String generatePNR() {
        long timestamp = System.currentTimeMillis();
        int randomNum = random.nextInt(1000);
        return PREFIX + timestamp + randomNum;
    }

    public static String generateSimplePNR() {
        int randomNum = 100000 + random.nextInt(900000);
        return PREFIX + randomNum;
    }
}
