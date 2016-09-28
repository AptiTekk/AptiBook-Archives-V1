/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.*;
import com.aptitekk.aptibook.core.util.Sha256Helper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.List;

@Stateless
public class TenantService extends GlobalEntityServiceAbstract<Tenant> implements Serializable {

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private UserService userService;

    @Inject
    private AssetCategoryService assetCategoryService;

    @Inject
    private PropertiesService propertiesService;

    @Inject
    private PermissionService permissionService;

    public Tenant getTenantBySubscriptionId(int subscriptionId) {
        try {
            return entityManager
                    .createQuery("SELECT t FROM Tenant t WHERE t.subscriptionId = ?1", Tenant.class)
                    .setParameter(1, subscriptionId)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    public Tenant getTenantBySlug(String slug) {
        try {
            return entityManager
                    .createQuery("SELECT t FROM Tenant t WHERE t.slug = ?1", Tenant.class)
                    .setParameter(1, slug)
                    .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    @Override
    public void insert(Tenant o) throws Exception {
        super.insert(o);

        initializeNewTenant(o);
    }

    private void initializeNewTenant(Tenant tenant) {
        ensureTenantIntegrity(tenant);
        addDefaultAssetCategories(tenant);
    }

    public void ensureTenantIntegrity(Tenant tenant) {
        checkForRootGroup(tenant);
        checkForAdminUser(tenant);
        writeDefaultProperties(tenant);
        writeDefaultPermissions(tenant);
    }

    private void checkForRootGroup(Tenant tenant) {
        if (userGroupService.getRootGroup(tenant) == null) {
            UserGroup rootGroup = new UserGroup(UserGroupService.ROOT_GROUP_NAME);
            try {
                userGroupService.insert(rootGroup, tenant);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkForAdminUser(Tenant tenant) {
        User adminUser = userService.findByName(UserService.ADMIN_USERNAME, tenant);
        if (adminUser == null) {

            adminUser = new User();
            adminUser.setUsername(UserService.ADMIN_USERNAME);
            adminUser.setPassword(Sha256Helper.rawToSha(UserService.DEFAULT_ADMIN_PASSWORD));
            adminUser.setVerified(true);
            adminUser.setUserState(User.State.APPROVED);
            try {
                userService.insert(adminUser, tenant);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ensureAdminHasRootGroup(tenant);
    }

    private void ensureAdminHasRootGroup(Tenant tenant) {
        User adminUser = userService.findByName(UserService.ADMIN_USERNAME, tenant);
        if (adminUser != null) {
            UserGroup rootGroup = userGroupService.getRootGroup(tenant);
            if (rootGroup != null) {
                if (!adminUser.getUserGroups().contains(rootGroup)) {
                    try {
                        adminUser.getUserGroups().add(rootGroup);
                        userService.merge(adminUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void writeDefaultProperties(Tenant tenant) {
        List<Property> currentProperties = propertiesService.getAll(tenant);

        for (Property.Key key : Property.Key.values()) {
            boolean foundProperty = false;

            for (Property property : currentProperties) {
                if (property.getPropertyKey().equals(key)) {
                    foundProperty = true;
                    break;
                }
            }

            if (!foundProperty) {
                Property property = new Property();
                property.setPropertyKey(key);
                property.setPropertyValue(key.getDefaultValue());
                try {
                    propertiesService.insert(property, tenant);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeDefaultPermissions(Tenant tenant) {
        List<Permission> currentPermissions = permissionService.getAll(tenant);

        for (Permission.Descriptor descriptor : Permission.Descriptor.values()) {
            boolean foundPermission = false;

            for (Permission permission : currentPermissions) {
                if (permission.getDescriptor().equals(descriptor)) {
                    foundPermission = true;
                    break;
                }
            }

            if (!foundPermission) {
                Permission permission = new Permission();
                permission.setDescriptor(descriptor);
                try {
                    permissionService.insert(permission, tenant);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addDefaultAssetCategories(Tenant tenant) {
        if (assetCategoryService.getAll(tenant).isEmpty()) {
            try {
                AssetCategory assetCategory = new AssetCategory("Rooms");
                assetCategoryService.insert(assetCategory, tenant);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}