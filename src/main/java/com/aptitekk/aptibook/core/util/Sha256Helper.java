/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Helper {

    /**
     * The salt for the hasher.
     */
    private static final String SALT = "TJ0uQG9KA4v8vf9m0GgU";

    /**
     * Hashes raw text using SHA256 and a salt.
     *
     * @param raw The raw text to hash.
     * @return The hash, as a byte array.
     */
    public static byte[] rawToSha(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((raw + SALT).getBytes("UTF-8"));
            return md.digest();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
