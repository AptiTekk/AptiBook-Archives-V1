/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entities.superClasses.MultiTenantEntity;
import com.aptitekk.agenda.core.entities.Tenant;

import javax.ejb.Local;
import java.util.List;

@Local
public interface MultiTenantEntityService<T extends MultiTenantEntity> extends EntityService<T> {

    List<T> getAll(Tenant tenant);

    void insert(T o, Tenant tenant) throws Exception;

}
