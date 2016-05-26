package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.UserService;
import com.AptiTekk.Agenda.core.testingUtil.TestUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertNull;

@RunWith(Arquillian.class)
public class UserServiceImplTest {

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtils.createDeployment();
    }

    @Inject
    private UserService userService;

    @Test
    public void correctCredentialsReturnsNullIfNullInput() throws Exception {
        assertNull(userService.correctCredentials(null, null));
    }

}
