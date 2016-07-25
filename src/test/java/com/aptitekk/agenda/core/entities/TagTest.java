/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities;

import com.aptitekk.agenda.core.services.AssetCategoryService;
import com.aptitekk.agenda.core.services.TagService;
import com.aptitekk.agenda.core.testingUtil.TestUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class TagTest {

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtils.createDeployment();
    }

    @Inject
    AssetCategoryService assetCategoryService;

    @Inject
    TagService tagService;

    @Test
    public void testDeletingAssetCategoryDeletesTags() throws Exception {
        AssetCategory assetCategory = new AssetCategory();
        assetCategory.setName("Test AssetCategory");

        assetCategoryService.insert(assetCategory);

        Tag tag = new Tag();
        tag.setName("Test Tag");
        tag.setAssetCategory(assetCategory);

        tagService.insert(tag);

        assertEquals("Incorrect number of Tags in database!", 1, tagService.getAll().size());

        assetCategoryService.delete(assetCategory.getId());

        assertEquals("There is still a Tag in the database!", 0, tagService.getAll().size());
    }
}
