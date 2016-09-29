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
import org.wildfly.swarm.jpa.JPAFraction;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;

public class SwarmBuilder {

    private static final File RESOURCES = new File("src/main/resources");
    private static final File WEB = new File("web");
    private static final File WEB_INF = new File(WEB, "WEB-INF");

    public static Swarm buildSwarm() throws Exception {
        Swarm swarm = new Swarm();

        //Configure PostgreSQLDS
        swarm.fraction(new DatasourcesFraction()
                .jdbcDriver("org.postgresql", (d) -> {
                    d.driverClassName("org.postgresql.Driver");
                    d.xaDatasourceClass("org.postgresql.xa.PGXADataSource");
                    d.driverModuleName("org.postgresql");
                })
                .dataSource("PostgreSQLDS", (ds) -> {
                    ds.driverName("org.postgresql");
                    ds.connectionUrl(System.getenv("JDBC_DATABASE_URL"));
                    ds.userName(System.getenv("JDBC_DATABASE_USERNAME"));
                    ds.password(System.getenv("JDBC_DATABASE_PASSWORD"));
                })
        );

        //Enable Infinispan Caching
        swarm.fraction(InfinispanFraction.createDefaultFraction());

        //Set Default Datasource
        swarm.fraction(new JPAFraction()
                .defaultDatasource("java:jboss/datasources/PostgreSQLDS"));

        return swarm;
    }

    public static WARArchive buildDeployment() throws Exception {
        // ---------------------------- ShrinkWrap WAR Generation ----------------------------
        WARArchive deployment = ShrinkWrap.create(WARArchive.class);

        deployment.addModule("org.postgresql");

        //Add all classes
        deployment.addPackages(true, "com.aptitekk.aptibook");

        //Add all resources
        recursiveAddAsClassesResource(deployment, RESOURCES);

        //Add all web files
        recursiveAddAsWebResource(deployment, WEB);

        //Add all Maven dependencies
        deployment.addAllDependencies();
        // ---------------------------- END ShrinkWrap WAR Generation ------------------------
        return deployment;
    }

    private static void recursiveAddAsClassesResource(WARArchive deployment, File root) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory())
                    recursiveAddAsClassesResource(deployment, file);
                else {
                    if (file.getPath().startsWith(RESOURCES.getPath())) {
                        String filePath = file.getPath().substring(RESOURCES.getPath().length() + 1).replaceAll("\\\\", "/");
                        deployment.addAsWebInfResource(new ClassLoaderAsset(filePath, Main.class.getClassLoader()), "classes/" + filePath);
                    }
                }
            }
        }
    }

    private static void recursiveAddAsWebResource(WARArchive deployment, File root) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory())
                    recursiveAddAsWebResource(deployment, file);
                else {
                    if (file.getPath().startsWith(WEB_INF.getPath())) {
                        if (file.getName().equals("web.xml")) {
                            deployment.setWebXML(file);
                        } else {
                            String filePath = file.getPath().substring(WEB_INF.getPath().length() + 1).replaceAll("\\\\", "/");
                            deployment.addAsWebInfResource(file, filePath);
                        }
                    } else if (file.getPath().startsWith(WEB.getPath())) {
                        String filePath = file.getPath().substring(WEB.getPath().length() + 1).replaceAll("\\\\", "/");
                        deployment.addAsWebResource(file, filePath);
                    }
                }
            }
        }
    }

}
