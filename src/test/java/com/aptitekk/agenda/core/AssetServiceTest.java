/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core;

import com.aptitekk.agenda.core.entities.Asset;
import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.services.AssetCategoryService;
import com.aptitekk.agenda.core.entities.services.AssetService;
import com.aptitekk.agenda.core.testingUtil.TestUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
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
    private AssetCategoryService assetCategoryService;

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
    @Transactional
    public void getAssetsFromTypeReturnsCorrectAssets() throws Exception {
        List<Asset> assetList = new ArrayList<>();
        AssetCategory assetCategory = new AssetCategory("Test Type");
        assetCategoryService.insert(assetCategory);

        for(int i = 0; i < 5; i++) {
            String assetName = UUID.randomUUID().toString();
            Asset asset = new Asset(assetName);
            asset.setAssetCategory(assetCategory);
            assetService.insert(asset);
            assetList.add(asset);
        }

        //Refresh AssetCategory
        assetCategory = assetCategoryService.get(assetCategory.getId());

        //Get assets from AssetCategory
        List<Asset> returnedAssets = assetCategory.getAssets();
        assertTrue("List is empty!", returnedAssets.size() > 0);

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
