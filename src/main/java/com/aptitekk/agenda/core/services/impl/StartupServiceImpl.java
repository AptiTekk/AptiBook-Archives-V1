/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.*;
import com.aptitekk.agenda.core.properties.PropertyKey;
import com.aptitekk.agenda.core.services.*;
import com.aptitekk.agenda.core.utilities.LogManager;
import com.aptitekk.agenda.core.utilities.Sha256Helper;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;

@Startup
@Singleton
@ApplicationScoped
public class StartupServiceImpl implements StartupService, Serializable {

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private UserService userService;

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private PropertiesService propertiesService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private TenantService tenantService;

    @PostConstruct
    public void init() {
        checkForRootGroup();
        checkForAdminUser();
        checkForAssetTypes();
        writeDefaultProperties();
        writeDefaultPermissions();
        loadDefaultTenants();
    }

    @Override
    public void checkForRootGroup() {
        if (userGroupService.findByName(UserGroupService.ROOT_GROUP_NAME) == null) {
            UserGroup rootGroup = new UserGroup(UserGroupService.ROOT_GROUP_NAME);
            try {
                userGroupService.insert(rootGroup);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void checkForAdminUser() {
        User adminUser = userService.findByName(UserService.ADMIN_USERNAME);
        if (adminUser == null) {

            adminUser = new User();
            adminUser.setUsername(UserService.ADMIN_USERNAME);
            adminUser.setPassword(Sha256Helper.rawToSha(UserService.DEFAULT_ADMIN_PASSWORD));
            try {
                userService.insert(adminUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ensureAdminHasRootGroup();
    }

    private void ensureAdminHasRootGroup() {
        User adminUser = userService.findByName(UserService.ADMIN_USERNAME);
        if (adminUser != null) {
            UserGroup rootGroup = userGroupService.getRootGroup();
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

    @Override
    public void checkForAssetTypes() {

        //TODO: Do this once ever, not on every startup.
        if (assetTypeService.getAll().isEmpty()) {
            try {
                AssetType assetType = new AssetType("Rooms");
                assetTypeService.insert(assetType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeDefaultProperties() {
        for (PropertyKey key : PropertyKey.values()) {
            if (propertiesService.getPropertyByKey(key) == null) {
                Property property = new Property(key, key.getDefaultValue());
                try {
                    propertiesService.insert(property);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void writeDefaultPermissions() {
        for (Permission.Descriptor descriptor : Permission.Descriptor.values()) {
            if (permissionService.getPermissionByDescriptor(descriptor) == null) {
                Permission permission = new Permission();
                permission.setDescriptor(descriptor);
                try {
                    permissionService.insert(permission);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void loadDefaultTenants() {
        for (int i = 0; i < 3; i++) {
            if (tenantService.getTenantBySubscriptionId(i) == null) {
                Tenant tenant = new Tenant();
                tenant.setSubscriptionId(i);
                try {
                    tenantService.insert(tenant);
                } catch (Exception e) {
                    LogManager.logError("Couldn't persist Tenant with subscription id " + i);
                    e.printStackTrace();
                }
            }
        }
    }
}
