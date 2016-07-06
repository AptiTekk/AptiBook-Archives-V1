/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import javax.ejb.Local;
import java.util.List;

@Local
public interface EntityService<T> {

    T get(int id);

    List<T> getAll();

    void insert(T o) throws Exception;

    void update(T newData, int id) throws Exception;

    void delete(int id) throws Exception;

    T merge(T entity) throws Exception;

}
