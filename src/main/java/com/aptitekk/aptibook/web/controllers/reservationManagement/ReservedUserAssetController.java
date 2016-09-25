/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.reservationManagement;


import com.aptitekk.aptibook.core.domain.entities.*;
import com.aptitekk.aptibook.core.domain.services.AssetCategoryService;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.web.components.primeFaces.schedule.ReservationScheduleEvent;
import com.aptitekk.aptibook.web.components.primeFaces.schedule.ReservationScheduleModel;
import com.aptitekk.aptibook.web.controllers.authentication.AuthenticationController;
import com.aptitekk.aptibook.web.controllers.help.HelpController;
import org.apache.http.auth.AUTH;
import org.joda.time.DateTime;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.ScheduleModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class ReservedUserAssetController implements Serializable {

    private ReservationScheduleModel scheduleModel;

    private ReservationScheduleEvent selectedEvent;

    private List<AssetCategory> assetCategories;

    private AssetCategory[] assetCategoriesDisplayed;

    @Inject
    UserService userService;

    @Inject
    AuthenticationController authenticationController;

    @Inject
    AssetCategoryService assetCategoryService;

    @Inject
    HelpController helpController;


    @PostConstruct
    private void init() {
        assetCategories = assetCategoryService.getAll();
        assetCategoriesDisplayed = new AssetCategory[assetCategories.size()];
        assetCategories.toArray(assetCategoriesDisplayed);
        scheduleModel = new ReservationScheduleModel() {
            @Override
            public List<Reservation> getReservationsBetweenDates(DateTime start, DateTime end) {
                List<Reservation> userGroupReservations = getAssetOwnerReservations();
                Iterator<Reservation> iterator = userGroupReservations.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getStatus() == Reservation.Status.REJECTED)
                        iterator.remove();
                }

                return userGroupReservations;
            }
        };

        helpController.setCurrentTopic(HelpController.Topic.FRONT_PAGE);
    }

    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }

    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        selectedEvent = (ReservationScheduleEvent) selectEvent.getObject();
    }

    public ReservationScheduleEvent getSelectedEvent() {
        return selectedEvent;
    }

    public List<AssetCategory> getAssetCategories() {
        return assetCategories;
    }

    public AssetCategory[] getAssetCategoriesDisplayed() {
        return assetCategoriesDisplayed;
    }

    public void setAssetCategoriesDisplayed(AssetCategory[] assetCategoriesDisplayed) {
        this.assetCategoriesDisplayed = assetCategoriesDisplayed;
    }

    public List<Reservation> getAssetOwnerReservations(){
        List<Reservation> assetReservations = new ArrayList<>();
        for(Asset asset : getUserGroupAssets()){
            for(Reservation reservation : asset.getReservations()){
                assetReservations.add(reservation);
            }
        }
        return assetReservations;
    }

    public List<Asset> getUserGroupAssets(){
        User user = authenticationController.getAuthenticatedUser();
        List<Asset> userGroupAssets = new ArrayList<>();
        for(UserGroup userGroup : user.getUserGroups()){
            for(UserGroup children : getUserGroupChildren(userGroup)){
                for(Asset asset : children.getAssets()) {
                    userGroupAssets.add(asset);
                }
            }
        }
        return userGroupAssets;
    }

    public List<UserGroup> getUserGroupChildren(UserGroup userGroup){
        Queue<UserGroup> queue = new LinkedList<>();
        queue.add(userGroup);
        UserGroup currEntry;
        List<UserGroup> groups = new ArrayList<>();
        while ((currEntry = queue.poll()) != null){
            groups.add(currEntry);
            for(UserGroup child : currEntry.getChildren()){
                queue.add(child);
            }
        }
        return groups;
    }

}
