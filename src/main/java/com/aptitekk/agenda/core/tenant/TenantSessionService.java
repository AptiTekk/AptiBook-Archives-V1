/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.tenant;

import com.aptitekk.agenda.core.entities.Tenant;

import javax.ejb.Local;

@Local
public interface TenantSessionService {

    Tenant getCurrentTenant();

}