package com.cintriq.agenda.core;

import com.cintriq.agenda.core.entity.*;
import com.cintriq.agenda.core.testingUtil.TestUtils;
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

    @Inject
    Properties properties;

    @Test
    public void test() throws Exception {
        User testOwner = new User();
        testOwner.setFirstName("Test");
        testOwner.setLastName("Owner");
        testOwner.setUsername("testOwner");
        testOwner.setEnabled(true);

        UserGroup testOwnerGroup = new UserGroup();
        testOwnerGroup.setName("Test Owner Group");
        testOwnerGroup.addUser(testOwner);

        User testRenter = new User();
        testRenter.setFirstName("Test");
        testRenter.setLastName("Renter");
        testRenter.setUsername("testRenter");
        testRenter.setEnabled(true);

        AssetType testAssetType = new AssetType();
        testAssetType.setName("TestType");

        Asset testAsset = new Asset();
        testAsset.setName("TestReservable");
        testAsset.setAssetType(testAssetType);
        testAsset.setOwner(testOwnerGroup);

        userService.insert(testOwner);
        userService.insert(testRenter);

        userGroupService.insert(testOwnerGroup);

        typeService.insert(testAssetType);
        assetService.insert(testAsset);

        Reservation reservation = new Reservation();
        reservation.setTitle("Test Reservation");
        reservation.setDescription("Test Reservation Description");
        reservation.setDateCreated(Calendar.getInstance());
        reservation.setTimeStart(Calendar.getInstance());

        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.HOUR, 1);
        reservation.setTimeEnd(endTime);
        reservation.setAsset(testAsset);
        reservation.setUser(testRenter);

        System.out.println(reservationService.getClass().getName());
        reservationService.insert(reservation);
        
        assertEquals("Notifications count should be 1", 1, notificationService.getAll().size());
        
        assertNotNull("testOwner is null", testOwner);
        assertNotNull("Notifications list null", testOwner.getNotifications());
        
        assertNotNull("Reservation doesn't exist", reservationService.get(reservation.getId()));
        assertEquals("Notification didn't appear", 1, notificationService.getUnread(testOwner).size());

    }

}
