/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entity.Permission;
import com.aptitekk.agenda.core.entity.QPermission;
import com.aptitekk.agenda.core.permissions.PermissionDescriptor;
import com.aptitekk.agenda.core.services.PermissionService;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import java.io.Serializable;

@Stateless
public class PermissionServiceImpl extends EntityServiceAbstract<Permission> implements PermissionService, Serializable {

    private QPermission table = QPermission.permission;

    @Override
    public Permission getPermissionByDescriptor(PermissionDescriptor descriptor) {
        return new JPAQuery<Permission>(entityManager).from(table).where(table.descriptor.eq(descriptor))
                .fetchOne();
    }

}
