package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.*;
import com.AptiTekk.Agenda.core.entity.AppProperty;
import com.AptiTekk.Agenda.core.entity.AssetType;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.entity.UserGroup;
import com.AptiTekk.Agenda.core.utilities.AgendaLogger;
import com.AptiTekk.Agenda.core.utilities.Sha256Helper;

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
            userGroupService.insert(rootGroup);
            User adminUser = userService.findByName(UserService.ADMIN_USERNAME);
            if (adminUser != null) {
                rootGroup.addUser(adminUser);
                adminUser.addGroup(rootGroup);
                userGroupService.merge(rootGroup);
                userService.merge(adminUser);
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
            userService.insert(adminUser);
            UserGroup rootGroup = userGroupService.findByName(UserGroupService.ROOT_GROUP_NAME);
            if (rootGroup != null) {
                rootGroup.addUser(adminUser);
                adminUser.addGroup(rootGroup);
                userGroupService.merge(rootGroup);
                userService.merge(adminUser);
            }
        }
    }

    @Override
    public void checkForReservableTypes() {
        if (assetTypeService.getAll().isEmpty()) {
            AssetType assetType = new AssetType("Rooms");
            assetTypeService.insert(assetType);
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
