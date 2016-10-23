/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.Resource;
import com.aptitekk.aptibook.core.domain.entities.ResourceCategory;
import com.aptitekk.aptibook.core.domain.entities.Tag;

import javax.ejb.Stateful;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@Stateful
public class TagService extends MultiTenantEntityServiceAbstract<Tag> implements Serializable {

    @Override
    public void delete(int id) {
        Tag tag = get(id);
        if(tag != null) {
            for(Resource resource : tag.getResources()){
                resource.getTags().remove(tag);
            }
        }

        try {
            super.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Tag findByName(ResourceCategory resourceCategory, String name) {
        if (resourceCategory == null || name == null)
            return null;

        try {
            return entityManager
                    .createQuery("SELECT t FROM Tag t WHERE t.resourceCategory = ?1 AND t.name = ?2", Tag.class)
                    .setParameter(1, resourceCategory)
                    .setParameter(2, name)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

}
