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

    public DateTime applyTimeZone(DateTime dateTime) {
        return dateTime.withZone(tenantSessionService.getCurrentTenantTimezone());
    }

    public TimeZone getCurrentTimeZone() {
        return tenantSessionService.getCurrentTenantTimezone().toTimeZone();
    }

}
