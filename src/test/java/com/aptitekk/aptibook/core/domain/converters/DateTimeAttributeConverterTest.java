/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.converters;

import com.aptitekk.aptibook.DefaultTest;
import com.aptitekk.aptibook.TestArchiver;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.undertow.WARArchive;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class DateTimeAttributeConverterTest extends DefaultTest {

    private DateTimeAttributeConverter dateTimeAttributeConverter;

    @Before
    public void setUp() throws Exception {
        dateTimeAttributeConverter = new DateTimeAttributeConverter();
    }

    @Test
    public void testConversion() throws Exception {
        DateTime original = new DateTime(DateTimeZone.UTC);
        Date convertToDate = dateTimeAttributeConverter.convertToDatabaseColumn(original);
        DateTime convertFromDate = dateTimeAttributeConverter.convertToEntityAttribute(convertToDate);

        assertEquals("Converted DateTime and original DateTime are not identical.", original, convertFromDate);
    }

    @Test
    public void convertToDatabaseColumn() throws Exception {

    }

    @Test
    public void convertToEntityAttribute() throws Exception {

    }

}
