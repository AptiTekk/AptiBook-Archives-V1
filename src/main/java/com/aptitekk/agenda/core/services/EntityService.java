/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.entities.util.GlobalEntity;

import javax.ejb.Local;
import javax.persistence.Entity;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@Local
public interface EntityService<T> {

    T get(int id);

    List<T> getAll();

    void insert(T o) throws Exception;

    void delete(int id) throws Exception;

    T merge(T entity) throws Exception;

}
