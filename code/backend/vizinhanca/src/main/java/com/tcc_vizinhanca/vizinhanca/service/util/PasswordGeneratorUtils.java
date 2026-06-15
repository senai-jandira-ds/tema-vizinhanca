package com.tcc_vizinhanca.vizinhanca.service.util;

import java.security.SecureRandom;

public class PasswordGeneratorUtils {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL = "@#$%&*!";

    private static final String ALL = UPPER + LOWER + NUMBERS + SPECIAL;

    private static final SecureRandom random = new SecureRandom();

    public static String generateSecure(int length) {
        StringBuilder password = new StringBuilder();

        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        for (int i = 4; i < length; i++) {
            password.append(ALL.charAt(random.nextInt(ALL.length())));
        }

        return shuffle(password.toString());
    }

    private static String shuffle(String input) {
        char[] array = input.toCharArray();
        for (int i = 0; i < array.length; i++) {
            int j = random.nextInt(array.length);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return new String(array);
    }

}
