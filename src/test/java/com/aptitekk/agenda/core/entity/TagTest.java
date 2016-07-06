/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entity;

import com.aptitekk.agenda.core.services.AssetTypeService;
import com.aptitekk.agenda.core.services.TagService;
import com.aptitekk.agenda.core.testingUtil.TestUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class TagTest {

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtils.createDeployment();
    }

    @Inject
    AssetTypeService assetTypeService;

    @Inject
    TagService tagService;

    @Test
    public void testDeletingAssetTypeDeletesTags() throws Exception {
        AssetType assetType = new AssetType();
        assetType.setName("Test AssetType");

        assetTypeService.insert(assetType);

        Tag tag = new Tag();
        tag.setName("Test Tag");
        tag.setAssetType(assetType);

        tagService.insert(tag);

        assertEquals("Incorrect number of Tags in database!", 1, tagService.getAll().size());

        assetTypeService.delete(assetType.getId());

        assertEquals("There is still a Tag in the database!", 0, tagService.getAll().size());
    }
}
