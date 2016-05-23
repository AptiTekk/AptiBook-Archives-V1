package com.AptiTekk.Agenda.core;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.AptiTekk.Agenda.core.testingUtil.TestUtils;

@RunWith(Arquillian.class)
public class UpdateTest {
  
  @Deployment
  public static WebArchive createDeployment() {
    return TestUtils.createDeployment();
  }
  
  @Inject
  UpdateService updater;

  @Test
  public void testThreadState() {
    //assertEquals(updater.getUpdaterState(), UpdateService.State.RUNNING);
  }
  
  

}
