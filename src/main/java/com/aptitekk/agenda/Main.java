/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda;

import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.jpa.postgresql.PostgreSQLJPAFraction;

import java.io.File;

public class Main {

    private static final File RESOURCES = new File("src/main/resources");
    private static final File WEB = new File("web");
    private static final File WEB_INF = new File(WEB, "WEB-INF");

    public static void main(String[] args) throws Exception {
        Container container = new Container();

        container.fraction(new DatasourcesFraction()
                .jdbcDriver("postgresql", (d) -> {
                    d.driverClassName("org.postgresql.Driver");
                    d.driverModuleName("org.postgresql");
                })
                .dataSource("Agenda", (ds) -> {
                    ds.driverName("postgresql");
                    ds.connectionUrl(System.getenv("JDBC_DATABASE_URL"));
                    ds.userName(System.getenv("JDBC_DATABASE_USERNAME"));
                    ds.password(System.getenv("JDBC_DATABASE_PASSWORD"));
                })
        );

        // Prevent JPA Fraction from installing it's default datasource fraction
        container.fraction(new PostgreSQLJPAFraction()
                .inhibitDefaultDatasource()
                .defaultDatasource("jboss/datasources/Agenda")
        );

        container.start();

        //-------------------------------------------------------------- ShrinkWrap WAR Generation
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);

        //Add all Agenda classes
        deployment.addPackages(true, "com.aptitekk.agenda");

        //Add all resources
        deployment.merge(ShrinkWrap.create(GenericArchive.class)
                .as(ExplodedImporter.class)
                .importDirectory(RESOURCES)
                .as(GenericArchive.class), "/", Filters.includeAll());

        /*//Add persistence.xml
        deployment.addAsWebInfResource(new ClassLoaderAsset("META-INF/persistence.xml", Main.class.getClassLoader()), "classes/META-INF/persistence.xml");

        //Add Liquibase
        deployment.addAsWebInfResource(new ClassLoaderAsset("liquibase/", Main.class.getClassLoader()), "classes/liquibase/");*/

        //Add web.xml
        deployment.setWebXML(new File(WEB_INF, "web.xml"));

        //Add faces-config.xml
        deployment.addAsWebInfResource(new File(WEB_INF, "faces-config.xml"));

        //Add all web files
        deployment.merge(ShrinkWrap.create(GenericArchive.class)
                .as(ExplodedImporter.class)
                .importDirectory(WEB)
                .as(GenericArchive.class), "/", Filters.includeAll());

        //Add all Maven dependencies
        deployment.addAllDependencies();
        //-------------------------------------------------------------- END ShrinkWrap WAR Generation

        container.deploy(deployment);
    }

}
