/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entity.converter;

import com.aptitekk.agenda.core.entity.enums.ReservationStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kevint on 6/26/2016.
 */
@Converter(autoApply = true)
public class ReservationStatusConverter implements AttributeConverter<ReservationStatus, String> {

    @Override
    public String convertToDatabaseColumn(ReservationStatus reservationStatus) {
        if (reservationStatus == null)
            return null;
        return reservationStatus.name();
    }

    @Override
    public ReservationStatus convertToEntityAttribute(String string) {
        return ReservationStatus.valueOf(string);
    }
}
