/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entity.Permission;

import javax.ejb.Local;

@Local
public interface PermissionService extends EntityService<Permission> {

    Permission getPermissionByDescriptor(Permission.Descriptor descriptor);

}
