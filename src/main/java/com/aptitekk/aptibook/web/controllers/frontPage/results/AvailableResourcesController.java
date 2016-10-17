/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.frontPage.results;

import com.aptitekk.aptibook.core.domain.entities.Resource;
import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.entities.Tag;
import com.aptitekk.aptibook.core.domain.services.ResourceService;
import com.aptitekk.aptibook.core.domain.services.NotificationService;
import com.aptitekk.aptibook.core.domain.services.ReservationService;
import com.aptitekk.aptibook.core.domain.services.UserGroupService;
import com.aptitekk.aptibook.web.controllers.frontPage.reserve.RequestReservationViewController;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class AvailableResourcesController implements Serializable {

    @Inject
    private RequestReservationViewController requestReservationViewController;

    @Inject
    private ReservationService reservationService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private ResourceService resourceService;

    private List<Resource> availableResources;
    private List<Resource> filteredResources;

    private List<Tag> filterTags;
    private boolean[] selectedFilterTags;


    public void searchForResources(ResourceCategory resourceCategory, ZonedDateTime startTime, ZonedDateTime endTime) {
        this.availableResources = reservationService.findAvailableResources(resourceCategory, startTime, endTime);

        filterTags = resourceCategory.getTags();
        selectedFilterTags = new boolean[filterTags.size()];
        for (int i = 0; i < selectedFilterTags.length; i++) {
            selectedFilterTags[i] = false;
        }
        filterResources();
    }

    /**
     * Updates the filteredResources list with only those resources which contain all the selectedFilterTags
     */
    public void filterResources() {
        filteredResources = new ArrayList<>();
        List<Tag> selectedTags = new ArrayList<>();
        for (int i = 0; i < selectedFilterTags.length; i++) {
            if (selectedFilterTags[i]) {
                selectedTags.add(filterTags.get(i));
            }
        }
        for (Resource resource : availableResources) {
            boolean skipResource = false;

            //Make sure the Resource has all the selected filter Tags
            for (Tag tag : selectedTags) {
                if (!resource.getTags().contains(tag))
                    skipResource = true;
            }

            if (skipResource)
                continue;

            filteredResources.add(resource);
        }
    }

    /**
     * Loads the RequestReservationViewController with the proper details.
     * The page should be reloaded with ajax after this method is called.
     *
     * @param resource     The Resource that the user wants to reserve.
     * @param startTime The start time of the reservation.
     * @param endTime   The end time of the reservation.
     */
    public void onMakeReservationFired(Resource resource, ZonedDateTime startTime, ZonedDateTime endTime) {
        requestReservationViewController.setResource(resource);
        requestReservationViewController.setStartTime(startTime);
        requestReservationViewController.setEndTime(endTime);
    }

    public List<Resource> getAvailableResources() {
        return availableResources;
    }

    public List<Resource> getFilteredResources() {
        return filteredResources;
    }

    public List<Tag> getFilterTags() {
        return filterTags;
    }

    public boolean[] getSelectedFilterTags() {
        return selectedFilterTags;
    }




}
