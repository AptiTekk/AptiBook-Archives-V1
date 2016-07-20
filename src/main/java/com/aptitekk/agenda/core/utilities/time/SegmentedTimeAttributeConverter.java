/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.utilities.time;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Time;
import java.util.Date;

/**
 * This class converts the SegmentedTime object to and from database columns.
 */
@Converter(autoApply = true)
public class SegmentedTimeAttributeConverter implements AttributeConverter<SegmentedTime, Date> {
    @Override
    public Date convertToDatabaseColumn(SegmentedTime segmentedTime) {
        return segmentedTime == null ? null : new Time(segmentedTime.getCalendar().getTime().getTime());
    }

    @Override
    public SegmentedTime convertToEntityAttribute(Date date) {
        return date == null ? null : new SegmentedTime(date);
    }
}
