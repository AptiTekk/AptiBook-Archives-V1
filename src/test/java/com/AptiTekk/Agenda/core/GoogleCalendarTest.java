package com.AptiTekk.Agenda.core;

import static org.junit.Assert.fail;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import com.AptiTekk.Agenda.core.testingUtil.TestUtils;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GoogleCalendarTest {
  
  @Deployment
  public static Archive<?> createDeployment() {
    return TestUtils.createDeployment(GoogleCalendarService.class);
  }

  @Test
  public void test() {
    //fail("Not yet implemented");
  }

}
