package com.aptitekk.agenda.web.controllers.assets;


import com.aptitekk.agenda.core.services.AssetService;
import com.aptitekk.agenda.core.services.AssetTypeService;
import com.aptitekk.agenda.core.services.TagService;
import com.aptitekk.agenda.core.entity.Asset;
import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.Tag;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class TagController implements Serializable {

    @Inject
    private TagService tagService;

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private AssetService assetService;

    private List<String> selectedAssetTypeTagNames = new ArrayList<>();
    private List<Tag> selectedAssetTags = new ArrayList<>();

    private Asset selectedAsset;
    private List<Tag> availableTags;

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
     * Updates the specified AssetType with the selected AssetType Tag names from this controller.
     * <br/><br/>
     * Note: If you have made any changes to the AssetType, you should merge the AssetType before calling this method.
     *
     * @param assetType The AssetType to update.
     */
    void updateAssetTags(AssetType assetType) {
        List<Tag> currentTags = assetType.getTags();

        if (selectedAssetTypeTagNames != null) {
            List<String> currentTagNames = new ArrayList<>();
            for (Tag tag : currentTags) {
                if (!selectedAssetTypeTagNames.contains(tag.getName()))
                    deleteTag(tag);
                else
                    currentTagNames.add(tag.getName());
            }

            for (String tagName : selectedAssetTypeTagNames) {
                if (!currentTagNames.contains(tagName))
                    createNewAssetTypeTag(assetType, tagName);
            }
        }
    }

    /**
     * Updates the specified Asset with the selected Asset Tag names from this controller.
     *
     * @param asset The Asset to update.
     */
    void updateAssetTags(Asset asset) {
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

    public List<String> getAssetTypeTagSuggestions(String input) {
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

    public List<String> getSelectedAssetTypeTagNames() {
        return selectedAssetTypeTagNames;
    }

    public void setSelectedAssetTypeTagNames(List<String> selectedAssetTypeTagNames) {
        if (selectedAssetTypeTagNames == null) {
            if (this.selectedAssetTypeTagNames != null)
                this.selectedAssetTypeTagNames.clear();
            else
                this.selectedAssetTypeTagNames = new ArrayList<>();
            return;
        }

        List<String> filteredTags = new ArrayList<>();

        //Remove commas and duplicates
        for (String tag : selectedAssetTypeTagNames) {
            tag = tag.trim().replaceAll("\\|", "");

            if (tag.contains(","))
                tag = tag.substring(0, tag.indexOf(","));

            if (filteredTags.contains(tag) || tag.isEmpty())
                continue;

            filteredTags.add(tag);
        }
        this.selectedAssetTypeTagNames = filteredTags;
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

    public Asset getSelectedAsset() {
        return selectedAsset;
    }

    void setSelectedAsset(Asset selectedAsset) {
        this.selectedAsset = selectedAsset;
        this.availableTags = selectedAsset != null ? selectedAsset.getAssetType().getTags() : null;
    }

}
