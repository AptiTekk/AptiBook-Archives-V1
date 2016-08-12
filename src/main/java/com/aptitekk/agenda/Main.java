/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.infinispan.InfinispanFraction;
import org.wildfly.swarm.jpa.postgresql.PostgreSQLJPAFraction;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;

public class Main {

    private static final File RESOURCES = new File("src/main/resources");
    private static final File WEB = new File("web");
    private static final File WEB_INF = new File(WEB, "WEB-INF");

    public static void main(String[] args) throws Exception {
        Container container = new Container();

        //Configure PostgreSQLDS
        container.fraction(new DatasourcesFraction()
                .jdbcDriver("postgresql", (d) -> {
                    d.driverClassName("org.postgresql.Driver");
                    d.driverModuleName("org.postgresql");
                })
                .dataSource("PostgreSQLDS", (ds) -> {
                    ds.driverName("postgresql");
                    ds.connectionUrl(System.getenv("JDBC_DATABASE_URL"));
                    ds.userName(System.getenv("JDBC_DATABASE_USERNAME"));
                    ds.password(System.getenv("JDBC_DATABASE_PASSWORD"));
                })
        );

        //Enable Infinispan Caching
        container.fraction(InfinispanFraction.createDefaultFraction());

        //Disable "ExampleDS" default datasource; make default PostgreSQLDS.
        container.fraction(new PostgreSQLJPAFraction()
                .inhibitDefaultDatasource()
                .defaultDatasource("java:jboss/datasources/PostgreSQLDS")
        );

        container.start();

        // ---------------------------- ShrinkWrap WAR Generation ----------------------------
        WARArchive deployment = ShrinkWrap.create(WARArchive.class);

        //Add all Agenda classes
        deployment.addPackages(true, "com.aptitekk.agenda");

        //Add all resources
        recursiveAddAsClassesResource(deployment, RESOURCES);

        //Add all web files
        recursiveAddAsWebResource(deployment, WEB);

        //Add web.xml
        deployment.setWebXML(new File(WEB_INF, "web.xml"));

        //Add all Maven dependencies
        deployment.addAllDependencies();
        // ---------------------------- END ShrinkWrap WAR Generation ------------------------

        container.deploy(deployment);
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
                        String filePath = file.getPath().substring(WEB_INF.getPath().length() + 1).replaceAll("\\\\", "/");
                        deployment.addAsWebInfResource(file, filePath);
                    } else if (file.getPath().startsWith(WEB.getPath())) {
                        String filePath = file.getPath().substring(WEB.getPath().length() + 1).replaceAll("\\\\", "/");
                        deployment.addAsWebResource(file, filePath);
                    }
                }
            }
        }
    }

}
