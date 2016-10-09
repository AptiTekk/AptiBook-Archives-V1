/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.testUtils;

import com.aptitekk.aptibook.SwarmBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.WebDriver;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;
import org.wildfly.swarm.undertow.WARArchive;

public abstract class TestBuilder {

    public static Swarm createSwarm() {
        try {
            return SwarmBuilder.buildSwarm();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WARArchive createDeployment() {
        try {
            return SwarmBuilder.buildDeployment();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
