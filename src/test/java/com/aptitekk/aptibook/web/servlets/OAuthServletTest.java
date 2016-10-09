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
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;
import org.wildfly.swarm.undertow.WARArchive;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class OAuthServletTest {

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
    public void testOAuthServletRedirection() {
        browser.navigate().to("http://localhost:8085/oauth?state=tenant=dev&code=1234");
        assertEquals("OAuthServlet did not redirect properly.", "http://localhost:8085/dev/index.xhtml", browser.getCurrentUrl());
    }

}
