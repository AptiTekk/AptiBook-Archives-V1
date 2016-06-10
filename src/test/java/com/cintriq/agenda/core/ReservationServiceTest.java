package com.cintriq.agenda.core;

import com.cintriq.agenda.core.entity.Asset;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.testingUtil.TestUtils;
import com.cintriq.agenda.core.utilities.time.CalendarRange;
import com.cintriq.agenda.core.utilities.time.SegmentedTime;
import com.cintriq.agenda.core.utilities.time.SegmentedTimeRange;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ReservationServiceTest {

    @Inject
    private ReservationService reservationService;

    @Inject
    private AssetService assetService;

    @Inject
    private AssetTypeService assetTypeService;

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtils.createDeployment();
    }

    @Test
    public void testCorrectAvailableAssetsAreReturned() throws Exception {
        AssetType assetType = new AssetType();
        assetType.setName("Test Asset Type");

        assetTypeService.insert(assetType);

        Random random = new Random();
        int numberAssetsToGenerate = 50;

        //Generate 20 Assets with random start and end times.
        for (int i = 0; i < numberAssetsToGenerate; i++) {
            SegmentedTime startSegmentedTime = new SegmentedTime(4, false);
            startSegmentedTime.increaseSegments(random.nextInt(12));

            SegmentedTime endSegmentedTime = (SegmentedTime) startSegmentedTime.clone();
            endSegmentedTime.increaseSegments(random.nextInt(22));

            Asset asset = new Asset();
            asset.setName(i + "");
            asset.setAssetType(assetType);
            asset.setAvailabilityStart(startSegmentedTime);
            asset.setAvailabilityEnd(endSegmentedTime);

            assetService.insert(asset);
        }

        assetType = assetTypeService.get(assetType.getId());

        assertEquals("There is an incorrect number of Assets assigned to the AssetType", numberAssetsToGenerate, assetType.getAssets().size());

        SegmentedTime startSearchTime = new SegmentedTime(8, false);
        SegmentedTime endSearchTime = new SegmentedTime(14, false);

        List<Asset> expectedAvailableAssets = new ArrayList<>();

        for (Asset asset : assetType.getAssets()) {
            if (startSearchTime.compareTo(asset.getAvailabilityStart()) < 0)
                continue;
            if (endSearchTime.compareTo(asset.getAvailabilityEnd()) > 0)
                continue;

            expectedAvailableAssets.add(asset);
        }

        List<Asset> actualAvailableAssets = reservationService.findAvailableAssets(assetType, new SegmentedTimeRange(null, startSearchTime, endSearchTime), 0);

        for (Asset asset : expectedAvailableAssets) {
            if (!actualAvailableAssets.contains(asset)) {
                fail("Expected Available Assets and Actual Available Assets differ in contents!");
            }
        }

        assertEquals("Expected Available Assets and Actual Available Assets differ in size!", expectedAvailableAssets.size(), actualAvailableAssets.size());
    }
}
