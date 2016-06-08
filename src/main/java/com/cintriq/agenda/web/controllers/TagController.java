package com.cintriq.agenda.web.controllers;


import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.TagService;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.Tag;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "TagController")
@ViewScoped
public class TagController {

    @Inject
    private TagService tagService;

    @Inject
    private AssetTypeService assetTypeService;

    private List<String> selectedTagNames = new ArrayList<>();

    private void createNewAssetTypeTag(AssetType assetType, String tagName) {
        if (assetType != null && tagName != null) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tag.setAssetType(assetType);
            try {
                tagService.insert(tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteTagfromAssetType(AssetType assetType, String tagName) {
        if (assetType != null && tagName != null) {
            Tag tag = tagService.findByName(assetType, tagName);
            try {
                if (tag != null) {
                    tagService.delete(tag.getId());
                }
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
     * Updates the specified AssetType with the selected Tag names from this controller.
     * <br/><br/>
     * Note: If you have made any changes to the AssetType, you should merge the AssetType before calling this method.
     * @param assetType The AssetType to update.
     */
    void updateAssetTypeTags(AssetType assetType)
    {
        List<Tag> currentTags = assetType.getTags();

        if (selectedTagNames != null) {
            List<String> currentTagNames = new ArrayList<>();
            for (Tag tag : currentTags) {
                if (!selectedTagNames.contains(tag.getName()))
                    deleteTag(tag);
                else
                    currentTagNames.add(tag.getName());
            }

            for (String tagName : selectedTagNames) {
                if (!currentTagNames.contains(tagName))
                    createNewAssetTypeTag(assetType, tagName);
            }
        }
    }

    public List<String> getAssetTypeSuggestions(String input) {
        return null; //Intentional: we don't want any suggestions, so return null;
    }

    public List<String> getSelectedTagNames() {
        return selectedTagNames;
    }

    public void setSelectedTagNames(List<String> selectedTagNames) {
        if (selectedTagNames == null) {
            this.selectedTagNames = null;
            return;
        }

        List<String> filteredTags = new ArrayList<>();

        //Remove commas and duplicates
        for (String tag : selectedTagNames) {
            tag = tag.trim();

            if (tag.contains(","))
                tag = tag.substring(0, tag.indexOf(","));

            if (filteredTags.contains(tag) || tag.isEmpty())
                continue;

            filteredTags.add(tag);
        }
        this.selectedTagNames = filteredTags;
    }
}
