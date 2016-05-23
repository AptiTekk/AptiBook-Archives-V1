package com.AptiTekk.Agenda.core;

import com.AptiTekk.Agenda.core.entity.*;
import com.AptiTekk.Agenda.core.entity.AssetType;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.AptiTekk.Agenda.core.testingUtil.TestUtils;

import java.util.Date;

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

    @Inject
    Properties properties;

    @Test
    public void test() {
        User testOwner = new User();
        testOwner.setFirstName("Test");
        testOwner.setLastName("Owner");
        testOwner.setUsername("testOwner");
        testOwner.setEnabled(true);

        UserGroup testOwners = new UserGroup();
        testOwners.addUser(testOwner);

        User testRenter = new User();
        testRenter.setFirstName("Test");
        testRenter.setLastName("Renter");
        testRenter.setUsername("testRenter");
        testRenter.setEnabled(true);

        AssetType testAssetType = new AssetType();
        testAssetType.setName("TestType");

        Asset testAsset = new Asset();
        testAsset.setName("TestReservable");
        testAsset.setType(testAssetType);
        testAsset.setOwner(testOwners);

        userService.insert(testOwner);
        userService.insert(testRenter);

        userGroupService.insert(testOwners);

        typeService.insert(testAssetType);
        assetService.insert(testAsset);

        Reservation reservation = new Reservation();
        reservation.setTitle("Test Reservation");
        reservation.setDescription("Test Reservation Description");
        reservation.setDateCreated(new Date());
        reservation.setTimeStart(new Date());

        //TODO constrain to availability
        Date timeEnd = new Date();
        timeEnd.setTime(timeEnd.getTime() + 100000);
        reservation.setTimeEnd(timeEnd);
        reservation.setAsset(testAsset);
        reservation.setUser(testRenter);

        reservationService.insert(reservation);
        
        assertEquals("Notifications count should be 1", 1, notificationService.getAll().size());
        
        assertNotNull("testOwner is null", testOwner);
        assertNotNull("Notifications list null", testOwner.getNotifications());
        
        assertNotNull("Reservation doesn't exist", reservationService.get(reservation.getId()));
        assertEquals("Notification didn't appear", 1, notificationService.getUnread(testOwner).size());

    }

}
