/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core;

import com.aptitekk.agenda.core.entities.Notification;
import com.aptitekk.agenda.core.entities.User;
import com.aptitekk.agenda.core.entities.services.NotificationService;
import com.aptitekk.agenda.core.entities.services.UserService;
import com.aptitekk.agenda.core.testingUtil.TestUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RunWith(Arquillian.class)
public class NotificationSorterTest {

    @Inject
    private UserService userService;

    @Inject
    private NotificationService notificationService;

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtils.createDeployment();
    }

    @Test
    public void testSorted() throws Exception {
        // ARRANGE
        User testUser = new User();
        testUser.setUsername("testUser");

        userService.insert(testUser);

        Notification unreadNotification2 = new Notification();
        unreadNotification2.setCreation(new Date());
        unreadNotification2.setSubject("unreadNotification2");
        unreadNotification2.setRead(false);
        unreadNotification2.setUser(testUser);

        Notification readNotification2 = new Notification();
        readNotification2.setCreation(new Date());
        readNotification2.setRead(true);
        readNotification2.setSubject("readNotification2");
        readNotification2.setUser(testUser);

        Notification unreadNotification1 = new Notification();
        unreadNotification1.setCreation(new Date());
        unreadNotification1.setRead(false);
        unreadNotification1.setSubject("unreadNotification1");
        unreadNotification1.setUser(testUser);

        Notification readNotification1 = new Notification();
        readNotification1.setCreation(new Date());
        readNotification1.setRead(true);
        readNotification1.setSubject("readNotification1");
        readNotification1.setUser(testUser);

        notificationService.insert(unreadNotification1);
        notificationService.insert(readNotification1);
        notificationService.insert(unreadNotification2);
        notificationService.insert(readNotification2);

        //act
        List<Notification> notifs = notificationService.getAllForUser(testUser);

        //assert
        List<Notification> expected = new LinkedList<Notification>() {{
            add(unreadNotification1);
            add(unreadNotification2);
            add(readNotification1);
            add(readNotification2);
        }};

        for(int i=0; i<notifs.size(); i++) {
            System.out.println("[" + i + "]: " + notifs.get(i).getSubject() + " " + expected.get(i).getSubject());
        }

        Assert.assertEquals("Lists differed", expected, notifs);

    }
}
