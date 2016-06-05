package com.cintriq.agenda.core.impl;

import com.cintriq.agenda.core.*;
import com.cintriq.agenda.core.entity.AppProperty;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.User;
import com.cintriq.agenda.core.entity.UserGroup;
import com.cintriq.agenda.core.utilities.AgendaLogger;
import com.cintriq.agenda.core.utilities.Sha256Helper;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
@ApplicationScoped
public class StartupServiceImpl implements StartupService {

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private UserService userService;

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private Properties properties;

    @PostConstruct
    public void init() {
        checkForRootGroup();
        checkForAdminUser();
        checkForReservableTypes();
        persistServiceDefaultProperties();
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
            User adminUser = userService.findByName(UserService.ADMIN_USERNAME);
            if (adminUser != null) {
                try {
                    rootGroup.addUser(adminUser);
                    adminUser.addGroup(rootGroup);
                    userGroupService.merge(rootGroup);
                    userService.merge(adminUser);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void checkForAdminUser() {
        if (userService.findByName(UserService.ADMIN_USERNAME) == null) {

            User adminUser = new User();
            adminUser.setUsername(UserService.ADMIN_USERNAME);
            adminUser.setPassword(Sha256Helper.rawToSha(UserService.DEFAULT_ADMIN_PASSWORD));
            adminUser.setEnabled(true);
            try {
                userService.insert(adminUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
            UserGroup rootGroup = userGroupService.findByName(UserGroupService.ROOT_GROUP_NAME);
            if (rootGroup != null) {
                try {
                    rootGroup.addUser(adminUser);
                    adminUser.addGroup(rootGroup);
                    userGroupService.merge(rootGroup);
                    userService.merge(adminUser);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void checkForReservableTypes() {
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
    public void persistServiceDefaultProperties() {
//        Package pack = EntityService.class.getPackage();
//        Reflections reflections = new Reflections(pack.getName());
//        Set<Class<?>> allClasses
//                = reflections.getSubTypesOf(Object.class);
//        System.out.println("package: " + pack.getName());
        List<Class> allClasses = new ArrayList<Class>() {
            {
                add(ReservationService.class);
                add(NotificationService.class);
                add(GoogleService.class);
                add(GoogleCalendarService.class);
            }
        };
        for (Class cl : allClasses) {
//            System.out.println("class: " + cl.getSimpleName());
            Field[] declaredFields = cl.getDeclaredFields();
            for (Field field : declaredFields) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
                        && field.getType() == AppProperty.class) {
                    try {
                        AppProperty property = (AppProperty) field.get(null);
                        if (properties.getProperty(property.getKey()) == null) {
                            AgendaLogger.logMessage("Persisting Default Value for " + property.getKey());
                            properties.insert(property);
                        }
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        Logger.getLogger(StartupServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }
    }

}
