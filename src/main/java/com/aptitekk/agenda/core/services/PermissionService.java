/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entity.Permission;
import com.aptitekk.agenda.core.entity.Property;
import com.aptitekk.agenda.core.permissions.PermissionDescriptor;
import com.aptitekk.agenda.core.properties.PropertyKey;

import javax.ejb.Local;

@Local
public interface PermissionService extends EntityService<Permission> {

    Permission getPermissionByDescriptor(PermissionDescriptor descriptor);

}
