/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.entities.Tenant;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

@Stateful
public class ResourceCategoryService extends MultiTenantEntityServiceAbstract<ResourceCategory> implements Serializable {

    /**
     * Finds ResourceCategory by its name, within the current Tenant.
     *
     * @param resourceCategoryName The name of the ResourceCategory
     * @return An ResourceCategory with the specified name, or null if one does not exist.
     */
    public ResourceCategory findByName(String resourceCategoryName) {
        return findByName(resourceCategoryName, getTenant());
    }

    /**
     * Finds ResourceCategory by its name, within the supplied Tenant.
     *
     * @param resourceCategoryName The name of the ResourceCategory
     * @param tenant            The Tenant of the ResourceCategory being searched for.
     * @return An ResourceCategory with the specified name, or null if one does not exist.
     */
    public ResourceCategory findByName(String resourceCategoryName, Tenant tenant) {
        if (resourceCategoryName == null || tenant == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT a FROM ResourceCategory a WHERE a.name = ?1 AND a.tenant = ?2", ResourceCategory.class)
                    .setParameter(1, resourceCategoryName)
                    .setParameter(2, tenant)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    @Override
    public List<ResourceCategory> getAll() {
        List<ResourceCategory> resourceCategories = super.getAll();
        resourceCategories.sort(new ResourceCategoryComparator());
        return resourceCategories;
    }

    @Override
    public List<ResourceCategory> getAll(Tenant tenant) {
        List<ResourceCategory> resourceCategories = super.getAll(tenant);
        resourceCategories.sort(new ResourceCategoryComparator());
        return resourceCategories;
    }

    /**
     * Sorts Resource Categories by name.
     */
    private class ResourceCategoryComparator implements Comparator<ResourceCategory> {

        @Override
        public int compare(ResourceCategory o1, ResourceCategory o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
