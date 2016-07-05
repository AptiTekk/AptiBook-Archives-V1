package com.aptitekk.agenda.core;

import static org.junit.Assert.fail;

import com.aptitekk.agenda.core.services.GoogleCalendarService;
import com.aptitekk.agenda.core.testingUtil.TestUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

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
