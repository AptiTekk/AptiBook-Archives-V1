/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;

import com.aptitekk.aptibook.core.crypto.PasswordStorage;
import com.aptitekk.aptibook.core.domain.entities.*;
import com.aptitekk.aptibook.core.util.AptiBookInfoProvider;
import com.aptitekk.aptibook.core.util.PasswordGenerator;

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
    private ResourceCategoryService resourceCategoryService;

    @Inject
    private PropertiesService propertiesService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private EmailService emailService;

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
    public void insert(Tenant entity) throws Exception {
        super.insert(entity);

        initializeNewTenant(entity);
    }

    private void initializeNewTenant(Tenant tenant) {
        ensureTenantIntegrity(tenant);
        addDefaultResourceCategories(tenant);
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
            rootGroup.setTenant(tenant);
            try {
                userGroupService.insert(rootGroup);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkForAdminUser(Tenant tenant) {
        User adminUser = userService.findByEmailAddress(UserService.ADMIN_EMAIL_ADDRESS, tenant);
        if (adminUser == null) {

            try {
                adminUser = new User();
                adminUser.setEmailAddress(UserService.ADMIN_EMAIL_ADDRESS);

                if (AptiBookInfoProvider.isUsingHeroku()) {
                    String password = PasswordGenerator.generateRandomPassword(10);
                    adminUser.setHashedPassword(PasswordStorage.createHash(password));
                    emailService.sendEmailNotification(tenant.getAdminEmail(), "AptiBook Registration", "<p>Thank you for registering with AptiBook! We are very excited to hear about how you and your team uses AptiBook.</p>"
                            + "<p>You can sign in to AptiBook using the URL and credentials below. Once you sign in, you can change your password by clicking <b>admin</b> on the navigation bar and visiting <b>My Account</b>.<br>"
                            + "https://aptibook.aptitekk.com/" + tenant.getSlug() + "</p>"
                            + "<center>"
                            + "Username: <b>admin</b> <br>"
                            + "Password: <b>" + password + "</b>"
                            + "</center>"
                            + "<p>Please let us know of any way we can be of assistance, and be sure to check out our knowledge base at https://support.aptitekk.com/. Enjoy!</p>");
                } else {
                    adminUser.setHashedPassword(PasswordStorage.createHash("admin"));
                }
                adminUser.setVerified(true);
                adminUser.setUserState(User.State.APPROVED);
                adminUser.setTenant(tenant);

                try {
                    userService.insert(adminUser);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (PasswordStorage.CannotPerformOperationException e) {
                e.printStackTrace();
            }
        }
        ensureAdminHasRootGroup(tenant);
    }

    private void ensureAdminHasRootGroup(Tenant tenant) {
        User adminUser = userService.findByEmailAddress(UserService.ADMIN_EMAIL_ADDRESS, tenant);
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
                property.setTenant(tenant);
                try {
                    propertiesService.insert(property);
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
                permission.setTenant(tenant);
                try {
                    permissionService.insert(permission);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addDefaultResourceCategories(Tenant tenant) {
        if (resourceCategoryService.getAll(tenant).isEmpty()) {
            try {
                ResourceCategory resourceCategory = new ResourceCategory("Rooms");
                resourceCategory.setTenant(tenant);
                resourceCategoryService.insert(resourceCategory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
