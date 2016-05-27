package com.AptiTekk.Agenda.core;

import com.AptiTekk.Agenda.core.AssetService;
import com.AptiTekk.Agenda.core.entity.Asset;
import com.AptiTekk.Agenda.core.entity.AssetType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AssetServiceTest {

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtils.createDeployment();
    }

    @Inject
    private AssetService assetService;

    @Inject
    private AssetTypeService assetTypeService;

    @Test
    public void getByNameReturnsCorrectAsset() throws Exception {
        String assetName = UUID.randomUUID().toString();

        Asset asset = new Asset(assetName);
        assetService.insert(asset);

        Asset foundAsset = assetService.findByName(assetName);

        assertNotNull("The asset search returned null!", foundAsset);
        assertEquals("Unable to locate the correct asset by name!", asset, foundAsset);
    }

    @Test
    public void getAllByTypeReturnsCorrectAssets() throws Exception {
        List<Asset> assetList = new ArrayList<>();
        AssetType assetType = new AssetType("Test Type");
        assetTypeService.insert(assetType);

        for(int i = 0; i < 5; i++) {
            String assetName = UUID.randomUUID().toString();
            Asset asset = new Asset(assetName);
            asset.setType(assetType);
            assetService.insert(asset);
            assetList.add(asset);
        }

        List<Asset> returnedAssets = assetService.getAllByType(assetType);

        assertNotNull("The returned asset list was null!", returnedAssets);
        for(Asset asset : returnedAssets)
        {
            assertTrue("An asset in the returned assets list was not present in the original list!", assetList.contains(asset));
        }

        for(Asset asset : assetList)
        {
            assertTrue("An asset in the original assets list was not present in the returned list!", returnedAssets.contains(asset));
        }
    }
}
