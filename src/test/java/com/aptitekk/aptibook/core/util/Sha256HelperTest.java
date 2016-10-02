/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.util;

import com.aptitekk.aptibook.SwarmBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.undertow.WARArchive;

import java.util.UUID;


public class Sha256HelperTest {

    public static Swarm newContainer() throws Exception {
        return SwarmBuilder.buildSwarm();
    }

    @Deployment
    public static WARArchive createDeployment() {
        try {
            return SwarmBuilder.buildDeployment();
        } catch (Exception e) {
            return null;
        }
    }

    public void testEncryption() {
    }

}
