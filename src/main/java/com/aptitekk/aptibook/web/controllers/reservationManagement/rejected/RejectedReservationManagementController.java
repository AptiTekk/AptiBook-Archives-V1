/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.reservationManagement.rejected;

import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.entities.Reservation;
import com.aptitekk.aptibook.core.domain.services.UserGroupService;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import com.aptitekk.aptibook.web.controllers.reservationManagement.ReservationDetails;
import com.aptitekk.aptibook.web.controllers.reservationManagement.ReservationDetailsController;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class RejectedReservationManagementController implements Serializable {

    @Inject
    private ReservationDetailsController reservationDetailsController;

    @Inject
    private HelpController helpController;

    @Inject
    private UserGroupService userGroupService;

    private Map<ResourceCategory, List<ReservationDetails>> reservationDetailsMap;

    @PostConstruct
    public void init() {
        buildReservationList();

        helpController.setCurrentTopic(HelpController.Topic.RESERVATION_MANAGEMENT_REJECTED);
    }

    private void buildReservationList() {
        this.reservationDetailsMap = reservationDetailsController.buildReservationList(Reservation.Status.REJECTED);
    }

    public List<ResourceCategory> getResourceCategories() {
        return new ArrayList<>(reservationDetailsMap.keySet());
    }

    public List<ReservationDetails> getReservationDetailsForResourceCategory(ResourceCategory resourceCategory) {
        return reservationDetailsMap.get(resourceCategory);
    }
}
