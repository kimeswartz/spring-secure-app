package com.useo.securewebapplication;

public class MaskingUtils {

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;  // Returnerar original-e-posten om den är null eller inte giltig
        }

        int atIndex = email.indexOf("@");
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() < 2) {
            return email;  // Om användarnamnet är för kort, returnera den originala e-posten
        }


        StringBuilder maskedEmail = new StringBuilder();
        maskedEmail.append(username.charAt(0)); // Lägger till första tecknet
        for (int i = 1; i < username.length() - 1; i++) {
            maskedEmail.append('*'); // Lägger till stjärnor
        }
        maskedEmail.append(username.charAt(username.length() - 1)); // Lägger till sista tecknet
        maskedEmail.append(domain); // Lägger till domändelen

        return maskedEmail.toString();
    }
}