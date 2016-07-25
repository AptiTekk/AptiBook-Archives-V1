/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core;

import com.aptitekk.agenda.core.entities.*;
import com.aptitekk.agenda.core.services.*;
import com.aptitekk.agenda.core.testingUtil.TestUtils;
import com.aptitekk.agenda.core.util.time.SegmentedTime;
import com.aptitekk.agenda.core.util.time.SegmentedTimeRange;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
public class ReservationServiceTest {

    @Inject
    private UserService userService;

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private AssetService assetService;

    @Inject
    private AssetCategoryService assetCategoryService;

    private Random random;

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtils.createDeployment();
    }

    @Before
    public void setUp() throws Exception {
        this.random = new Random(100000);
    }

    @Test
    public void testCorrectAvailableAssetsAreReturned() throws Exception {
        User testOwner = new User();
        testOwner.setFirstName("Test");
        testOwner.setLastName("Owner");
        testOwner.setUsername("testOwner");
        userService.insert(testOwner);

        UserGroup testOwnerGroup = new UserGroup();
        testOwnerGroup.setName("Test Owner Group");
        testOwnerGroup.getUsers().add(testOwner);
        userGroupService.insert(testOwnerGroup);

        User testRenter = new User();
        testRenter.setFirstName("Test");
        testRenter.setLastName("Renter");
        testRenter.setUsername("testRenter");
        userService.insert(testRenter);

        AssetCategory assetCategory = new AssetCategory();
        assetCategory.setName("Test AssetCategory");

        assetCategoryService.insert(assetCategory);

        SegmentedTimeRange searchTimeRange = new SegmentedTimeRange(Calendar.getInstance(), new SegmentedTime(12, false), new SegmentedTime(15, false)); //Search from 12 to 3 on today's date.
        List<Asset> expectedAvailableAssets = new ArrayList<>();

        Asset expectedAsset1 = new Asset("expectedAsset1");
        expectedAsset1.setAssetCategory(assetCategory);
        expectedAsset1.setAvailabilityStart(new SegmentedTime(12, false));
        expectedAsset1.setAvailabilityEnd(new SegmentedTime(15, false));
        expectedAsset1.setOwner(testOwnerGroup);
        assetService.insert(expectedAsset1);
        expectedAvailableAssets.add(expectedAsset1);

        Asset expectedAsset2 = new Asset("expectedAsset2");
        expectedAsset2.setAssetCategory(assetCategory);
        expectedAsset2.setAvailabilityStart(new SegmentedTime(11, true));
        expectedAsset2.setAvailabilityEnd(new SegmentedTime(15, true));
        expectedAsset2.setOwner(testOwnerGroup);
        assetService.insert(expectedAsset2);
        expectedAvailableAssets.add(expectedAsset2);

        Asset expectedAsset3 = new Asset("expectedAsset3");
        expectedAsset3.setAssetCategory(assetCategory);
        expectedAsset3.setAvailabilityStart(new SegmentedTime(8, false));
        expectedAsset3.setAvailabilityEnd(new SegmentedTime(15, false));
        expectedAsset3.setOwner(testOwnerGroup);
        assetService.insert(expectedAsset3);
        expectedAvailableAssets.add(expectedAsset3);

        Reservation reservation = new Reservation();
        reservation.setDate(searchTimeRange.getDate());
        reservation.setTimeStart(new SegmentedTime(8, false));
        reservation.setTimeEnd(new SegmentedTime(12, false));
        reservation.setUser(testRenter);
        reservation.setAsset(expectedAsset3);
        reservationService.insert(reservation);

        Asset expectedAsset4 = new Asset("expectedAsset4");
        expectedAsset4.setAssetCategory(assetCategory);
        expectedAsset4.setAvailabilityStart(new SegmentedTime(8, false));
        expectedAsset4.setAvailabilityEnd(new SegmentedTime(15, false));
        expectedAsset4.setOwner(testOwnerGroup);
        assetService.insert(expectedAsset4);
        expectedAvailableAssets.add(expectedAsset4);

        reservation = new Reservation();
        reservation.setDate((Calendar) searchTimeRange.getDate().clone());
        reservation.getDate().add(Calendar.DAY_OF_MONTH, 1);
        reservation.setTimeStart(new SegmentedTime(10, false));
        reservation.setTimeEnd(new SegmentedTime(13, false));
        reservation.setUser(testRenter);
        reservation.setAsset(expectedAsset4);
        reservationService.insert(reservation);

        Asset unexpectedAsset1 = new Asset("unexpectedAsset1");
        unexpectedAsset1.setAssetCategory(assetCategory);
        unexpectedAsset1.setAvailabilityStart(new SegmentedTime(12, true));
        unexpectedAsset1.setAvailabilityEnd(new SegmentedTime(15, false));
        unexpectedAsset1.setOwner(testOwnerGroup);
        assetService.insert(unexpectedAsset1);

        Asset unexpectedAsset2 = new Asset("unexpectedAsset2");
        unexpectedAsset2.setAssetCategory(assetCategory);
        unexpectedAsset2.setAvailabilityStart(new SegmentedTime(12, false));
        unexpectedAsset2.setAvailabilityEnd(new SegmentedTime(14, true));
        unexpectedAsset2.setOwner(testOwnerGroup);
        assetService.insert(unexpectedAsset2);

        Asset unexpectedAsset3 = new Asset("unexpectedAsset3");
        unexpectedAsset3.setAssetCategory(assetCategory);
        unexpectedAsset3.setAvailabilityStart(new SegmentedTime(8, false));
        unexpectedAsset3.setAvailabilityEnd(new SegmentedTime(15, false));
        unexpectedAsset3.setOwner(testOwnerGroup);
        assetService.insert(unexpectedAsset3);

        reservation = new Reservation();
        reservation.setDate(searchTimeRange.getDate());
        reservation.setTimeStart(new SegmentedTime(10, false));
        reservation.setTimeEnd(new SegmentedTime(13, false));
        reservation.setUser(testRenter);
        reservation.setAsset(unexpectedAsset3);
        reservationService.insert(reservation);

        assetCategory = assetCategoryService.get(assetCategory.getId()); //Refresh AssetCategory

        List<Asset> actualAvailableAssets = reservationService.findAvailableAssets(assetCategory, searchTimeRange, 0);

        for (Asset asset : actualAvailableAssets) {
            if (!expectedAvailableAssets.contains(asset)) {
                fail("Expected Available Assets and Actual Available Assets differ in contents!\n" +
                        "Asset " + asset.getName() + " should not have been available!");
            }
        }

        for (Asset asset : expectedAvailableAssets) {
            if (!actualAvailableAssets.contains(asset)) {
                fail("Expected Available Assets and Actual Available Assets differ in contents!\n" +
                        "Asset " + asset.getName() + " should have been available!");
            }
        }

        assertEquals("Expected Available Assets and Actual Available Assets differ in size!", expectedAvailableAssets.size(), actualAvailableAssets.size());
    }
}
