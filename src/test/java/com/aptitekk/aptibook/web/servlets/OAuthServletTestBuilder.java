/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.servlets;

import com.aptitekk.aptibook.testUtils.TestBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;
import org.wildfly.swarm.undertow.WARArchive;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class OAuthServletTestBuilder {

    @Drone
    WebDriver browser;

    @CreateSwarm
    public static Swarm createSwarm() {
        return TestBuilder.createSwarm();
    }

    @Deployment(testable = false)
    public static WARArchive createDeployment() {
        return TestBuilder.createDeployment();
    }

    @Test
    public void testOAuthServletRedirection() {
        browser.navigate().to("http://localhost:8085/oauth?state=tenant=dev&code=1234");
        assertEquals("OAuthServlet did not redirect properly.", "http://localhost:8085/dev/index.xhtml", browser.getCurrentUrl());
    }

}
