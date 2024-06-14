package com.useo.securewebapplication;

public class MaskingUtils {

    public static String anonymizeEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            // Email does not contain a valid username part
            return email;
        }

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        int usernameLength = username.length();

        StringBuilder maskedEmail = new StringBuilder();
        maskedEmail.append(username.charAt(0)); // First character
        for (int i = 1; i < usernameLength - 1; i++) {
            maskedEmail.append('*'); // Mask middle characters
        }
        maskedEmail.append(username.charAt(usernameLength - 1)); // Last character
        maskedEmail.append(domain); // Append domain

        return maskedEmail.toString();
    }
}
