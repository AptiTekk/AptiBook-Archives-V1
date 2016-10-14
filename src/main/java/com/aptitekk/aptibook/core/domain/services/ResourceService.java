/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.Resource;
import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.entities.Tenant;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

@Stateful
public class ResourceService extends MultiTenantEntityServiceAbstract<Resource> implements Serializable {

    @Inject
    private ResourceCategoryService resourceCategoryService;

    /**
     * Finds Resource by its name within an Resource Category.
     *
     * @param name          The name of the Resource.
     * @param resourceCategory The Resource Category to search within.
     * @return An Resource with the specified name, or null if one does not exist.
     */
    public Resource findByName(String name, ResourceCategory resourceCategory) {
        if (name == null || resourceCategory == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT r FROM Resource r WHERE r.name = ?1 AND r.resourceCategory = ?2 AND r.tenant = ?3", Resource.class)
                    .setParameter(1, name)
                    .setParameter(2, resourceCategory)
                    .setParameter(3, getTenant())
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    @Override
    public List<Resource> getAll() {
        List<Resource> resources = super.getAll();
        resources.sort(new ResourceComparator());
        return resources;
    }

    @Override
    public List<Resource> getAll(Tenant tenant) {
        List<Resource> resources = super.getAll(tenant);
        resources.sort(new ResourceComparator());
        return resources;
    }

    /**
     * Sorts Resource Categories by name.
     */
    private class ResourceComparator implements Comparator<Resource> {

        @Override
        public int compare(Resource o1, Resource o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
