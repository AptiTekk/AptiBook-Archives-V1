/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.core.tenant.TenantSessionService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Named
@RequestScoped
public class TimeController implements Serializable {

    @Inject
    private TenantSessionService tenantSessionService;

    public static final DateTimeFormatter STANDARD_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");
    public static final DateTimeFormatter FRIENDLY_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy h:mm a");

    public ZonedDateTime applyTimeZone(ZonedDateTime dateTime) {
        if (dateTime == null)
            return null;

        return dateTime.withZoneSameLocal(tenantSessionService.getCurrentTenantZoneId());
    }

    public TimeZone getCurrentTimeZone() {
        return TimeZone.getTimeZone(tenantSessionService.getCurrentTenantZoneId());
    }

    public String formatStandard(ZonedDateTime dateTime) {
        if (dateTime == null)
            return null;

        return dateTime.format(STANDARD_DATE_FORMATTER);
    }

    public String formatFriendly(ZonedDateTime dateTime) {
        if (dateTime == null)
            return null;

        return dateTime.format(FRIENDLY_DATE_FORMATTER);
    }

}
