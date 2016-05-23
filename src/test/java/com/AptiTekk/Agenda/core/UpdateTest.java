package com.AptiTekk.Agenda.core;

import com.AptiTekk.Agenda.core.testingUtil.TestUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

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
