/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.resourceCategories;


import com.aptitekk.aptibook.core.domain.entities.Resource;
import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.entities.Tag;
import com.aptitekk.aptibook.core.domain.services.ResourceCategoryService;
import com.aptitekk.aptibook.core.domain.services.ResourceService;
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
    private ResourceCategoryService resourceCategoryService;

    @Inject
    private ResourceService resourceService;

    private List<String> selectedResourceCategoryTagNames = new ArrayList<>();
    private List<Tag> selectedResourceTags = new ArrayList<>();

    private List<Tag> availableTags;

    private void createNewResourceCategoryTag(ResourceCategory resourceCategory, String tagName) {
        if (resourceCategory != null && tagName != null) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tag.setResourceCategory(resourceCategory);
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
     * Updates the specified ResourceCategory with the selected ResourceCategory Tag names from this controller.
     * <br/><br/>
     * Note: If you have made any changes to the ResourceCategory, you should merge the ResourceCategory before calling this method.
     *
     * @param resourceCategory The ResourceCategory to update.
     */
    void updateResourceTags(ResourceCategory resourceCategory) {
        List<Tag> currentTags = resourceCategory.getTags();

        if (selectedResourceCategoryTagNames != null) {
            List<String> currentTagNames = new ArrayList<>();
            for (Tag tag : currentTags) {
                if (!selectedResourceCategoryTagNames.contains(tag.getName()))
                    deleteTag(tag);
                else
                    currentTagNames.add(tag.getName());
            }

            for (String tagName : selectedResourceCategoryTagNames) {
                if (!currentTagNames.contains(tagName))
                    createNewResourceCategoryTag(resourceCategory, tagName);
            }
        }
    }

    /**
     * Updates the specified Resource with the selected Resource Tag names from this controller.
     *
     * @param resource The Resource to update.
     */
    public void updateResourceTags(Resource resource) {
        List<Tag> currentTags = resource.getTags();
        if (currentTags == null)
            currentTags = new ArrayList<>();

        if (selectedResourceTags != null) {
            Iterator<Tag> iterator = currentTags.iterator();

            //Remove tags that are not selected
            while (iterator.hasNext()) {
                if (!selectedResourceTags.contains(iterator.next()))
                    iterator.remove();
            }

            //Add tags that are selected and not already added
            for (Tag tag : selectedResourceTags) {
                if (!currentTags.contains(tag))
                    currentTags.add(tag);
            }

            resource.setTags(currentTags);

            try {
                resourceService.merge(resource);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getResourceCategoryTagSuggestions(String input) {
        return null; //Intentional: we don't want any suggestions, so return null;
    }

    public List<Tag> getResourceTagSuggestions(String input) {
        if (availableTags != null) {
            List<Tag> filteredTags = new ArrayList<>();

            for (Tag tag : availableTags) {
                if (selectedResourceTags != null && selectedResourceTags.contains(tag))
                    continue;

                if (tag.getName().toLowerCase().startsWith(input.toLowerCase()))
                    filteredTags.add(tag);
            }
            return filteredTags;
        }
        return null;
    }

    public List<String> getSelectedResourceCategoryTagNames() {
        return selectedResourceCategoryTagNames;
    }

    public void setSelectedResourceCategoryTagNames(List<String> selectedResourceCategoryTagNames) {
        if (selectedResourceCategoryTagNames == null) {
            if (this.selectedResourceCategoryTagNames != null)
                this.selectedResourceCategoryTagNames.clear();
            else
                this.selectedResourceCategoryTagNames = new ArrayList<>();
            return;
        }

        List<String> filteredTags = new ArrayList<>();

        //Remove commas and duplicates
        for (String tag : selectedResourceCategoryTagNames) {
            tag = tag.trim().replaceAll("\\|", "");

            if (tag.contains(","))
                tag = tag.substring(0, tag.indexOf(","));

            if (filteredTags.contains(tag) || tag.isEmpty())
                continue;

            filteredTags.add(tag);
        }
        this.selectedResourceCategoryTagNames = filteredTags;
    }

    public List<Tag> getSelectedResourceTags() {
        return selectedResourceTags;
    }

    public void setSelectedResourceTags(List<Tag> selectedResourceTags) {

        if (selectedResourceTags != null) {
            List<Tag> filteredTags = new ArrayList<>();
            for (Tag tag : selectedResourceTags) {
                if (!filteredTags.contains(tag))
                    filteredTags.add(tag);
            }
            this.selectedResourceTags = filteredTags;
        } else
            this.selectedResourceTags = null;
    }

    public void setAvailableTags(List<Tag> availableTags) {
        this.availableTags = availableTags;
    }

}
