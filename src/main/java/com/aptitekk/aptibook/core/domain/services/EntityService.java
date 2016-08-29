/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import javax.ejb.Local;
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
