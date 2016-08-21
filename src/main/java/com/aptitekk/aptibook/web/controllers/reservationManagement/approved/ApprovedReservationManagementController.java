/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.reservationManagement.approved;

import com.aptitekk.aptibook.core.entities.AssetCategory;
import com.aptitekk.aptibook.core.entities.Reservation;
import com.aptitekk.aptibook.core.entities.services.UserGroupService;
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
public class ApprovedReservationManagementController implements Serializable {

    @Inject
    private ReservationDetailsController reservationDetailsController;

    @Inject
    private HelpController helpController;

    @Inject
    private UserGroupService userGroupService;

    private Map<AssetCategory, List<ReservationDetails>> reservationDetailsMap;

    @PostConstruct
    public void init() {
        buildReservationList();

        helpController.setCurrentTopic(HelpController.Topic.RESERVATION_MANAGEMENT_APPROVED);
    }

    private void buildReservationList() {
        this.reservationDetailsMap = reservationDetailsController.buildReservationList(Reservation.Status.APPROVED);
    }

    public List<AssetCategory> getAssetCategories() {
        return new ArrayList<>(reservationDetailsMap.keySet());
    }

    public List<ReservationDetails> getReservationDetailsForAssetCategory(AssetCategory assetCategory) {
        return reservationDetailsMap.get(assetCategory);
    }
}
