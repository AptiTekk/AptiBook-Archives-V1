/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.servlets;

import com.aptitekk.aptibook.SwarmBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;
import org.wildfly.swarm.undertow.WARArchive;

@RunWith(Arquillian.class)
public class PingServletTest {

    @CreateSwarm
    public static Swarm createSwarm() {
        try {
            return SwarmBuilder.buildSwarm(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deployment(testable = false)
    public static WARArchive createDeployment() {
        try {
            return SwarmBuilder.buildDeployment();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}