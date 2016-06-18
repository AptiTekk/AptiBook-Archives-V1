package com.aptitekk.agenda.core.utilities;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;

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
