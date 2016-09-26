/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers;

import com.aptitekk.aptibook.core.tenant.TenantSessionService;
import org.joda.time.DateTime;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.TimeZone;

@Named
@RequestScoped
public class TimeController implements Serializable {

    @Inject
    private TenantSessionService tenantSessionService;

    public static final String STANDARD_DATE_FORMAT = "MM/dd/yyyy h:mm a";
    public static final String FRIENDLY_DATE_FORMAT = "EEEE, dd MMMM, yyyy h:mm aa";

    public DateTime applyTimeZone(DateTime dateTime) {
        if(dateTime == null)
            return null;

        return dateTime.withZone(tenantSessionService.getCurrentTenantTimezone());
    }

    public TimeZone getCurrentTimeZone() {
        return tenantSessionService.getCurrentTenantTimezone().toTimeZone();
    }

    public String formatStandard(DateTime dateTime) {
        if(dateTime == null)
            return null;

        return dateTime.toString(STANDARD_DATE_FORMAT);
    }

    public String formatFriendly(DateTime dateTime) {
        if(dateTime == null)
            return null;

        return dateTime.toString(FRIENDLY_DATE_FORMAT);
    }

}
