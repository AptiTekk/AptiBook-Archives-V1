/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.infinispan.InfinispanFraction;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        Swarm swarm = SwarmBuilder.buildSwarm();
        swarm.start();

        WARArchive deployment = SwarmBuilder.buildDeployment();
        swarm.deploy(deployment);
    }

}
