/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core;

import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.testingUtil.TestUtils;
import com.aptitekk.agenda.core.util.Sha256Helper;
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
        assertNull(userService.getUserWithCredentials(null, null));
    }

    @Test
    public void correctCredentialsReturnsCorrectUserIfCorrect() throws Exception {
        User user = new User();

        String username = UUID.randomUUID().toString();
        String rawPassword = UUID.randomUUID().toString();

        user.setUsername(username);
        user.setPassword(Sha256Helper.rawToSha(rawPassword));

        userService.insert(user);

        User check = userService.getUserWithCredentials(username, rawPassword);

        assertNotNull("The credentials should have been correct, but getUserWithCredentials returned null!", check);

        assertTrue("The user returned by getUserWithCredentials does not match the original! Original: "+user.getId()+" - Returned: "+check.getId(), user.equals(check));
    }
}
