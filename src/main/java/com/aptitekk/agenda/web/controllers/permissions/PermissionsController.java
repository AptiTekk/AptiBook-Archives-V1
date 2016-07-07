/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.permissions;

import com.aptitekk.agenda.core.entity.Permission;
import com.aptitekk.agenda.core.services.PermissionService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class PermissionsController implements Serializable {

    @Inject
    private PermissionService permissionService;

    private List<PermissionDetails> permissionDetailsList;

    @PostConstruct
    public void init() {
        buildPermissionDetailsList();
    }

    private void buildPermissionDetailsList() {
        permissionDetailsList = new ArrayList<>();
        Map<Permission.Group, List<Permission>> permissionGroupMap = new HashMap<>();

        for (Permission.Group group : Permission.Group.values())
            permissionGroupMap.put(group, new ArrayList<>());

        for (Permission.Descriptor descriptor : Permission.Descriptor.values())
            permissionGroupMap.get(descriptor.getGroup()).add(permissionService.getPermissionByDescriptor(descriptor));

        for (Map.Entry<Permission.Group, List<Permission>> entry : permissionGroupMap.entrySet())
            permissionDetailsList.add(new PermissionDetails(entry.getKey(), entry.getValue()));
    }

    public List<PermissionDetails> getPermissionDetailsList() {
        return permissionDetailsList;
    }

}
