package com.AptiTekk.Agenda.core.testingUtil;

import com.AptiTekk.Agenda.core.EntityService;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.impl.EntityServiceAbstract;
import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import com.AptiTekk.Agenda.core.utilities.NotificationFactory;

public class TestUtils {
  
  public static WebArchive createDeployment() {
    File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
        .importRuntimeDependencies().resolve().withTransitivity().asFile();
    
    return ShrinkWrap.create(WebArchive.class)
        .addPackages(true, EntityService.class.getPackage(), 
                NotificationFactory.class.getPackage(), 
                EntityServiceAbstract.class.getPackage(), User.class.getPackage())
        .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
        .addAsWebInfResource("wildfly-ds.xml")
        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
        .addAsLibraries(libs);      
  }

}
