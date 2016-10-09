/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.servlets;

import com.aptitekk.aptibook.SwarmBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;
import org.wildfly.swarm.undertow.WARArchive;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PingServletTest {

    @Drone
    WebDriver browser;

    @CreateSwarm
    public static Swarm createSwarm() {
        try {
            return SwarmBuilder.buildSwarm();
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

    @Test
    public void testPingServlet() {
        browser.navigate().to("http://localhost:8085/ping");
        assertEquals("PingServlet did not return pong", "pong", browser.getPageSource());
    }

}