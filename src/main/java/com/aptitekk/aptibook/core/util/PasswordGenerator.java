/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordGenerator {
    private static final Random RANDOM = new SecureRandom();
    /**
     * Generate a random String suitable for use as a temporary password.
     *
     * @return String suitable for use as a temporary password
     * @since 2.4
     */
    public static String generateRandomPassword(int length)
    {
        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";
        String pw = "";
        for (int i=0; i<length; i++)
        {
            int index = (int)(RANDOM.nextDouble()*letters.length());
            pw += letters.substring(index, index+1);
        }
        return pw;
    }

}
