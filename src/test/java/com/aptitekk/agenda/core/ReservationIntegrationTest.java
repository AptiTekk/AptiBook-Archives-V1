/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core;

import com.aptitekk.agenda.core.entities.*;
import com.aptitekk.agenda.core.services.*;
import com.aptitekk.agenda.core.testingUtil.TestUtils;
import com.aptitekk.agenda.core.utilities.time.SegmentedTime;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class ReservationIntegrationTest {

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtils.createDeployment();
    }

    @Inject
    UserService userService;
    @Inject
    UserGroupService userGroupService;
    @Inject
    AssetTypeService typeService;
    @Inject
    AssetService assetService;
    @Inject
    ReservationService reservationService;

    @Inject
    NotificationService notificationService;

    @Test
    public void test() throws Exception {
        User testOwner = new User();
        testOwner.setFirstName("Test");
        testOwner.setLastName("Owner");
        testOwner.setUsername("testOwner");

        UserGroup testOwnerGroup = new UserGroup();
        testOwnerGroup.setName("Test Owner Group");
        testOwnerGroup.getUsers().add(testOwner);

        User testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("Renter");
        testUser.setUsername("testUser");

        AssetType testAssetType = new AssetType();
        testAssetType.setName("TestType");

        Asset testAsset = new Asset();
        testAsset.setName("TestReservable");
        testAsset.setAssetType(testAssetType);
        testAsset.setOwner(testOwnerGroup);

        userService.insert(testOwner);
        userService.insert(testUser);

        userGroupService.insert(testOwnerGroup);

        typeService.insert(testAssetType);
        assetService.insert(testAsset);

        Reservation reservation = new Reservation();
        reservation.setTitle("Test Reservation");
        reservation.setTimeStart(new SegmentedTime(6, false));

        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.HOUR, 1);
        reservation.setTimeEnd(new SegmentedTime(15, false));
        reservation.setAsset(testAsset);
        reservation.setUser(testUser);

        System.out.println(reservationService.getClass().getName());
        reservationService.insert(reservation);
        
        assertEquals("Notifications count should be 1", 1, notificationService.getAll().size());
        
        assertNotNull("testOwner is null", testOwner);
        assertNotNull("Notifications list null", testOwner.getNotifications());
        
        assertNotNull("Reservation doesn't exist", reservationService.get(reservation.getId()));
        assertEquals("Notification didn't appear", 1, notificationService.getUnread(testOwner).size());

    }

}
