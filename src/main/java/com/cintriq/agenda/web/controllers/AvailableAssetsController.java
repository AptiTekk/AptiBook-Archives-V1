package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.ReservationService;
import com.cintriq.agenda.core.entity.*;
import com.cintriq.agenda.core.utilities.AgendaLogger;
import com.cintriq.agenda.core.utilities.TimeRange;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "AvailableAssetsController")
@ViewScoped
public class AvailableAssetsController {

    @ManagedProperty(value = "#{TagController}")
    private TagController tagController;

    public void setTagController(TagController tagController) {
        this.tagController = tagController;
    }

    @Inject
    private ReservationService reservationService;

    //private List<Asset> filteredAssets;
    public ArrayList<Asset> filteredAssets = new ArrayList<Asset>();
    private List<Asset> availableAssets;
    private Map<String, Boolean> checkMap = new HashMap<String, Boolean>();


    private List<String> result;

    public void filterAssets(AssetType assetType, TimeRange timeRange) {
        System.out.println("filteredAssets after clear: " + filteredAssets.size());
        this.tagController.availableFilterTags(assetType);
        this.result = tagController.filter();
        System.out.println("Result size: " + result.size());
        if ((!(result.size() == 0)) && !(result == null)) {
            boolean temp;
            this.availableAssets = reservationService.findAvailableAssets(assetType, timeRange, 0f);
            for (Asset asset : availableAssets) {
                //make sure something is checked in filter checkbox

                temp = false;
                //If Asset tag is found in checked filter
                for (Tag tag : asset.getTags()) {
                    if (result.contains(tag.getName().toString())) {
                        // System.out.println("MISSION SUCCESS");
                        temp = true;
                    }

                }
                //Add asset
                if (temp) {
                    if (asset != null) {
                        System.out.println("asset is not null");
                        if (!filteredAssets.contains(asset)) {
                            filteredAssets.add(asset);
                        } else {
                            System.out.println("Already contains: " + asset.getName().toString());
                        }
                    } else {
                        System.out.println("asset is null");
                    }
                }

            }
            System.out.println("Filtered asset size before merge: " + filteredAssets.size());

            availableAssets.clear();
            if(filteredAssets.size() == 0){
                filteredAssets.clear();
            }
            availableAssets = new ArrayList<>(filteredAssets);
        filteredAssets.clear();
        //System.out.println("filteredAsset size ..: " + filteredAssets.size());
        System.out.println("availableAssets size: " + availableAssets.size());

        }else{
            System.out.println("result is null");
            this.availableAssets = reservationService.findAvailableAssets(assetType, timeRange, 0f);

        }
    }



    public void searchForAssets(AssetType assetType, TimeRange timeRange) {
        this.availableAssets = reservationService.findAvailableAssets(assetType, timeRange, 0f);
        tagController.availableFilterTags(assetType);
    }


    public void onMakeReservationFired(User user, Asset asset, TimeRange timeRange) {
        AgendaLogger.logVerbose("Reserving " + asset.getName() + " for " + user.getUsername() + " on " + timeRange.getStartTimeFormatted(TimeRange.FORMAT_DATE_TIME) + " to " + timeRange.getEndTimeFormatted(TimeRange.FORMAT_DATE_TIME));
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setAsset(asset);
        reservation.setTimeStart(timeRange.getStartTime());
        reservation.setTimeEnd(timeRange.getEndTime());

        try {
            reservationService.insert(reservation);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: Tell user
        }
    }
    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public List<Asset> getAvailableAssets() {
        return availableAssets;
    }

    public Map<String, Boolean> getCheckMap() {
        return checkMap;
    }

    public void setCheckMap(Map<String, Boolean> checkMap) {
        this.checkMap = checkMap;
    }
}
