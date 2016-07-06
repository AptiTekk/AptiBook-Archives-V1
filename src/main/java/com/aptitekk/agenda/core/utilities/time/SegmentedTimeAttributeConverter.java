/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.utilities.time;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Time;

/**
 * This class converts the SegmentedTime object to and from database columns.
 */
@Converter(autoApply = true)
public class SegmentedTimeAttributeConverter implements AttributeConverter<SegmentedTime, Time> {
    @Override
    public Time convertToDatabaseColumn(SegmentedTime segmentedTime) {
        return segmentedTime == null ? null : new Time(segmentedTime.getCalendar().getTime().getTime());
    }

    @Override
    public SegmentedTime convertToEntityAttribute(Time time) {
        return time == null ? null : new SegmentedTime(time);
    }
}
