/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entity.Property;
import com.aptitekk.agenda.core.entity.QProperty;
import com.aptitekk.agenda.core.properties.PropertyKey;
import com.aptitekk.agenda.core.services.PropertiesService;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;

@Stateless
public class PropertiesServiceImpl extends EntityServiceAbstract<Property> implements PropertiesService {

    private QProperty table = QProperty.property;

    @Override
    public Property getPropertyByKey(PropertyKey key) {
        return new JPAQuery<Property>(entityManager).from(table).where(table.propertyKey.eq(key))
                .fetchOne();
    }

}
