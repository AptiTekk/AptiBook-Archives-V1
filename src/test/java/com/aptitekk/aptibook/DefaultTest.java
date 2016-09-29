/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;
import org.wildfly.swarm.undertow.WARArchive;

public abstract class DefaultTest {

    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        Swarm swarm = new Swarm();

        return swarm;
    }

    @Deployment
    public static WARArchive createDeployment() {
        return TestArchiver.buildArchive();
    }

}
