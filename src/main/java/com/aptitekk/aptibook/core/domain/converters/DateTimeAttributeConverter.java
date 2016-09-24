/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.converters;

import org.joda.time.DateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Date;

/**
 * This class converts the joda DateTime object to and from database columns.
 */
@Converter(autoApply = true)
public class DateTimeAttributeConverter implements AttributeConverter<DateTime, Date> {

    @Override
    public Date convertToDatabaseColumn(DateTime dateTime) {
        return dateTime == null ? null : dateTime.toDate();
    }

    @Override
    public DateTime convertToEntityAttribute(Date date) {
        return date == null ? null : new DateTime(date);
    }
}
