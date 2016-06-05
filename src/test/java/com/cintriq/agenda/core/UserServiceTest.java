package com.cintriq.agenda.core;

import com.cintriq.agenda.core.entity.User;
import com.cintriq.agenda.core.testingUtil.TestUtils;
import com.cintriq.agenda.core.utilities.Sha256Helper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class UserServiceTest {

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

    @Test
    public void correctCredentialsReturnsCorrectUserIfCorrect() throws Exception {
        User user = new User();

        String username = UUID.randomUUID().toString();
        String rawPassword = UUID.randomUUID().toString();

        user.setUsername(username);
        user.setPassword(Sha256Helper.rawToSha(rawPassword));

        userService.insert(user);

        User check = userService.correctCredentials(username, rawPassword);

        assertNotNull("The credentials should have been correct, but correctCredentials returned null!", check);

        assertTrue("The user returned by correctCredentials does not match the original! Original: "+user.getId()+" - Returned: "+check.getId(), user.equals(check));
    }
}
