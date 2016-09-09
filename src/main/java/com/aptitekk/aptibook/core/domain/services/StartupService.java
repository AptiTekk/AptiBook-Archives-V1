/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.domain.entities.*;
import com.aptitekk.aptibook.core.domain.entities.property.Property;
import com.aptitekk.aptibook.core.tenant.TenantManagementService;
import com.aptitekk.aptibook.core.util.Sha256Helper;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@Startup
@Singleton
@ApplicationScoped
public class StartupService implements Serializable {

    private final UserGroupService userGroupService;

    private final UserService userService;

    private final AssetCategoryService assetCategoryService;

    private final PropertiesService propertiesService;

    private final PermissionService permissionService;

    private final TenantService tenantService;

    @Inject
    public StartupService(UserGroupService userGroupService, UserService userService, AssetCategoryService assetCategoryService, PropertiesService propertiesService, PermissionService permissionService, TenantService tenantService, TenantManagementService tenantManagementService) {
        this.userGroupService = userGroupService;
        this.userService = userService;
        this.assetCategoryService = assetCategoryService;
        this.propertiesService = propertiesService;
        this.permissionService = permissionService;
        this.tenantService = tenantService;
    }

    @PostConstruct
    public void init() {
        for (Tenant tenant : tenantService.getAll()) {
            checkForRootGroup(tenant);
            checkForAdminUser(tenant);
            checkForAssetCategories(tenant);
            writeDefaultProperties(tenant);
            writeDefaultPermissions(tenant);
        }
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

    private void checkForAssetCategories(Tenant tenant) {
        //TODO: Do this when tenant is created; not on every startup.
        if (assetCategoryService.getAll(tenant).isEmpty()) {
            try {
                AssetCategory assetCategory = new AssetCategory("Rooms");
                assetCategoryService.insert(assetCategory, tenant);
            } catch (Exception e) {
                e.printStackTrace();
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
                Property property = new Property(key, key.getDefaultValue());
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
}
