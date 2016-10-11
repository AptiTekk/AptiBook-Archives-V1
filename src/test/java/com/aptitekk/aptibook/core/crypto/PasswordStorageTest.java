/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.crypto;

import com.aptitekk.aptibook.testUtils.TestBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;
import org.wildfly.swarm.undertow.WARArchive;

import java.util.UUID;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class PasswordStorageTest {

    @CreateSwarm
    public static Swarm createSwarm() {
        return TestBuilder.createSwarm();
    }

    @Deployment(testable = false)
    public static WARArchive createDeployment() {
        return TestBuilder.createDeployment();
    }

    @Test
    public void testHashesAreUnique() throws PasswordStorage.CannotPerformOperationException {
        String password = UUID.randomUUID().toString();
        String hash1 = PasswordStorage.createHash(password);
        String hash2 = PasswordStorage.createHash(password);

        assertNotEquals("Hashes were not unique.", hash1, hash2);
    }

    @Test
    public void testVerifyPassword() throws PasswordStorage.CannotPerformOperationException, PasswordStorage.InvalidHashException {
        String password = UUID.randomUUID().toString();
        String hash = PasswordStorage.createHash(password);

        assertTrue("Password verification failed.", PasswordStorage.verifyPassword(password, hash));
    }

}
