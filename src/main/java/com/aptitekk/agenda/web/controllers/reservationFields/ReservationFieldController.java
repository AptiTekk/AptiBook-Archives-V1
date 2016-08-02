/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.reservationFields;

import com.aptitekk.agenda.core.entities.AssetCategory;
import com.aptitekk.agenda.core.entities.ReservationField;
import com.aptitekk.agenda.core.entities.services.ReservationFieldService;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class ReservationFieldController implements Serializable {

    @Inject
    private ReservationFieldService reservationFieldService;

    private Map<AssetCategory, List<ReservationField>> reservationFieldCache = new HashMap<>();

    public List<ReservationField> getReservationFields(AssetCategory assetCategory) {
        //noinspection Java8CollectionsApi (Otherwise we are calling getAllForAssetCategory every time this method is called.)
        if (reservationFieldCache.get(assetCategory) == null)
            reservationFieldCache.put(assetCategory, reservationFieldService.getAllForAssetCategory(assetCategory));

        return reservationFieldCache.get(assetCategory);
    }

}
