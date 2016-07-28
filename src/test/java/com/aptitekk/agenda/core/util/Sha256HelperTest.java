/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.util;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

public class Sha256HelperTest {

  @Test
  public void testTwoStringsHashedAreEqual() throws NoSuchAlgorithmException {
    final String testRawData = "1234567890!@#$%^&*()-=_+,./';[]}{<>?abcdefghijklmnopqrstuvwxyz";

    assertArrayEquals("Sha256Helper did not produce identical results for identical inputs!",
        Sha256Helper.rawToSha(testRawData), Sha256Helper.rawToSha(testRawData));
    assertFalse("Sha256Helper produced same output as input!",
        Sha256Helper.rawToSha(testRawData).equals(testRawData));
  }

}
