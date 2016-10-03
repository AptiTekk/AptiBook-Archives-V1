/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.crypto;

import com.aptitekk.aptibook.SwarmBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;

import java.io.File;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PasswordStorageTest {

    @CreateSwarm
    public static Swarm createSwarm() {
        try {
            return SwarmBuilder.buildSwarm(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.createFromZipFile(JavaArchive.class, new File("target/aptibook-swarm.jar"));
    }

    @Test
    public void testHashesAreUnique() throws PasswordStorage.CannotPerformOperationException {
        String password = "secret";
        String hash1 = PasswordStorage.createHash(password);
        String hash2 = PasswordStorage.createHash(password);

        assertNotEquals("Hashes were not unique.", hash1, hash2);
    }

}
