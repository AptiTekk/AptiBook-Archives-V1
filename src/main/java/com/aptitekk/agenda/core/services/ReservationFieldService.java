/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.ReservationField;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ReservationFieldService extends EntityService<ReservationField> {

    List<ReservationField> getByType(AssetType type);

}
