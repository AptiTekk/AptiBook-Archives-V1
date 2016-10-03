/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.assetCategories;


import com.aptitekk.aptibook.core.domain.entities.Asset;
import com.aptitekk.aptibook.core.domain.entities.AssetCategory;
import com.aptitekk.aptibook.core.domain.entities.Tag;
import com.aptitekk.aptibook.core.domain.services.AssetCategoryService;
import com.aptitekk.aptibook.core.domain.services.AssetService;
import com.aptitekk.aptibook.core.domain.services.TagService;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Named
@ViewScoped
public class TagController implements Serializable {

    @Inject
    private TagService tagService;

    @Inject
    private AssetCategoryService assetCategoryService;

    @Inject
    private AssetService assetService;

    private List<String> selectedAssetCategoryTagNames = new ArrayList<>();
    private List<Tag> selectedAssetTags = new ArrayList<>();

    private List<Tag> availableTags;



    private void createNewAssetCategoryTag(AssetCategory assetCategory, String tagName) {
        if (assetCategory != null && tagName != null) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tag.setAssetCategory(assetCategory);
            try {
                tagService.insert(tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteTag(Tag tag) {
        try {
            if (tag != null) {
                tagService.delete(tag.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the specified AssetCategory with the selected AssetCategory Tag names from this controller.
     * <br/><br/>
     * Note: If you have made any changes to the AssetCategory, you should merge the AssetCategory before calling this method.
     *
     * @param assetCategory The AssetCategory to update.
     */
    void updateAssetTags(AssetCategory assetCategory) {
        List<Tag> currentTags = assetCategory.getTags();

        if (selectedAssetCategoryTagNames != null) {
            List<String> currentTagNames = new ArrayList<>();
            for (Tag tag : currentTags) {
                if (!selectedAssetCategoryTagNames.contains(tag.getName()))
                    deleteTag(tag);
                else
                    currentTagNames.add(tag.getName());
            }

            for (String tagName : selectedAssetCategoryTagNames) {
                if (!currentTagNames.contains(tagName))
                    createNewAssetCategoryTag(assetCategory, tagName);
            }
        }
    }

    /**
     * Updates the specified Asset with the selected Asset Tag names from this controller.
     *
     * @param asset The Asset to update.
     */
    public void updateAssetTags(Asset asset) {
        List<Tag> currentTags = asset.getTags();
        if (currentTags == null)
            currentTags = new ArrayList<>();

        if (selectedAssetTags != null) {
            Iterator<Tag> iterator = currentTags.iterator();

            //Remove tags that are not selected
            while (iterator.hasNext()) {
                if (!selectedAssetTags.contains(iterator.next()))
                    iterator.remove();
            }

            //Add tags that are selected and not already added
            for (Tag tag : selectedAssetTags) {
                if (!currentTags.contains(tag))
                    currentTags.add(tag);
            }

            asset.setTags(currentTags);

            try {
                assetService.merge(asset);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getAssetCategoryTagSuggestions(String input) {
        return null; //Intentional: we don't want any suggestions, so return null;
    }

    public List<Tag> getAssetTagSuggestions(String input) {
        if (availableTags != null) {
            List<Tag> filteredTags = new ArrayList<>();

            for (Tag tag : availableTags) {
                if (selectedAssetTags != null && selectedAssetTags.contains(tag))
                    continue;

                if (tag.getName().toLowerCase().startsWith(input.toLowerCase()))
                    filteredTags.add(tag);
            }
            return filteredTags;
        }
        return null;
    }

    public List<String> getSelectedAssetCategoryTagNames() {
        return selectedAssetCategoryTagNames;
    }

    public void setSelectedAssetCategoryTagNames(List<String> selectedAssetCategoryTagNames) {
        if (selectedAssetCategoryTagNames == null) {
            if (this.selectedAssetCategoryTagNames != null)
                this.selectedAssetCategoryTagNames.clear();
            else
                this.selectedAssetCategoryTagNames = new ArrayList<>();
            return;
        }

        List<String> filteredTags = new ArrayList<>();

        //Remove commas and duplicates
        for (String tag : selectedAssetCategoryTagNames) {
            tag = tag.trim().replaceAll("\\|", "");

            if (tag.contains(","))
                tag = tag.substring(0, tag.indexOf(","));

            if (filteredTags.contains(tag) || tag.isEmpty())
                continue;

            filteredTags.add(tag);
        }
        this.selectedAssetCategoryTagNames = filteredTags;
    }

    public List<Tag> getSelectedAssetTags() {
        return selectedAssetTags;
    }

    public void setSelectedAssetTags(List<Tag> selectedAssetTags) {

        if (selectedAssetTags != null) {
            List<Tag> filteredTags = new ArrayList<>();
            for (Tag tag : selectedAssetTags) {
                if (!filteredTags.contains(tag))
                    filteredTags.add(tag);
            }
            this.selectedAssetTags = filteredTags;
        } else
            this.selectedAssetTags = null;
    }

    public void setAvailableTags(List<Tag> availableTags) {
        this.availableTags = availableTags;
    }

}
