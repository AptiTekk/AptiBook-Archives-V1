package com.cintriq.agenda.web.controllers;


import com.cintriq.agenda.core.AssetService;
import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.TagService;
import com.cintriq.agenda.core.entity.Asset;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.Tag;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "TagController")
@ViewScoped
public class TagController {

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
    private ArrayList<String> filterTags = new ArrayList<>();
    private Map<String, Boolean> checkMap = new HashMap<String, Boolean>();
    private List<String> result = new ArrayList<String>();

    public void availableFilterTags(AssetType assetType) {
        filterTags.clear();
        for (Tag availableTag : assetType.getTags()) {
            // System.out.println("availableTag = "+ availableTag.getName());
            //System.out.println("availableTag .toString() = "+ availableTag.getName().toString());
            if (!filterTags.contains(availableTag.getName().toString())) {
                filterTags.add(availableTag.getName().toString());
            }
        }
    }

    /* Called from AvailbleAssetsController.
       Returns List<String> of selected checkboxes from Results page.*/
    public List<String> filter() {
        List<String> result = new ArrayList<String>();
        for (Map.Entry<String, Boolean> entry : checkMap.entrySet()) {
            if (entry.getValue())
                if (!result.contains(entry.getKey()))
                    result.add(entry.getKey());
        }
        return result;
    }

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
            this.selectedAssetTypeTagNames = null;
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
        this.availableTags = selectedAsset.getAssetType().getTags();
    }

    public Map<String, Boolean> getCheckMap() {
        return checkMap;
    }

    public void setCheckMap(Map<String, Boolean> checkMap) {
        this.checkMap = checkMap;
    }


    public ArrayList<String> getFilterTags() {
        return filterTags;
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

  /*  public void setFilterTags(ArrayList<String> filterTags) {
        TagController.filterTags = filterTags;
    }*/


}
