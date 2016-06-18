package com.aptitekk.agenda.core.testingUtil;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

public class TestUtils {

    private static final File WEB = new File("web");
    private static final File WEB_INF = new File(WEB, "WEB-INF");

    public static WebArchive createDeployment(Class... clazz) {
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeDependencies().resolve().withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class)
                //Class Resources
                .addPackages(true, "com.aptitekk.agenda") //All Agenda classes
                .addClasses(clazz) //User specified classes

                //Test Resources
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("wildfly-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))

                //Web Resources
                .setWebXML(new File(WEB_INF, "web.xml"))
                .addAsWebInfResource(new File(WEB_INF, "faces-config.xml"))

                //Include all web files.
                .merge(ShrinkWrap.create(GenericArchive.class)
                        .as(ExplodedImporter.class)
                        .importDirectory(WEB)
                        .as(GenericArchive.class), "/", Filters.includeAll())

                //Libraries
                .addAsLibraries(libs);
    }

}
