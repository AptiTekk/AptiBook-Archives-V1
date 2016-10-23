/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.GlobalEntity;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public abstract class GlobalEntityServiceAbstract<T extends GlobalEntity> extends EntityServiceAbstract<T> implements EntityService<T>, Serializable {

    @Override
    public List<T> getAll() {
        TypedQuery<T> query = this.entityManager.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e", entityType);
        return query.getResultList();
    }

}
