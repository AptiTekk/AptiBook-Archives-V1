package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.ReservationService;
import com.cintriq.agenda.core.entity.*;
import com.cintriq.agenda.core.utilities.AgendaLogger;
import com.cintriq.agenda.core.utilities.time.CalendarRange;
import com.cintriq.agenda.core.utilities.time.SegmentedTime;
import com.cintriq.agenda.core.utilities.time.SegmentedTimeRange;

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
        this.tagController.availableFilterTags(assetType);
        this.result = tagController.filter(); //Get selected checkbox list.
        if ((!(result.size() == 0)) && !(result == null)) {
            boolean temp;
            this.availableAssets = reservationService.findAvailableAssets(assetType, timeRange, 0f);
            for (Asset asset : availableAssets) {
                temp = false;
                //If Asset tag is found in checked filter
                for (Tag tag : asset.getTags()) {
                    if (result.contains(tag.getName().toString())) {
                        temp = true;
                    }
                }
                //If asset tag was found in results list, add asset to the filtered list.
                if (temp) {
                    if (asset != null) {
                        if (!filteredAssets.contains(asset)) {
                            filteredAssets.add(asset);
                        } else {
                            System.out.println("Already contains: " + asset.getName().toString());
                        }
                    } else {
                        System.out.println("Asset is null");
                    }
                }
            }
            availableAssets.clear();
            if (filteredAssets.size() == 0) {
                filteredAssets.clear();
            }
            availableAssets = new ArrayList<>(filteredAssets);
            filteredAssets.clear();
        } else {
            System.out.println("Result(checkbox) is null");
            this.availableAssets = reservationService.findAvailableAssets(assetType, timeRange, 0f);
        }
    }


    public void searchForAssets(AssetType assetType, SegmentedTimeRange segmentedTimeRange) {
        this.availableAssets = reservationService.findAvailableAssets(assetType, segmentedTimeRange, 0f);
        tagController.availableFilterTags(assetType);
    }

    public void onMakeReservationFired(User user, Asset asset, SegmentedTimeRange segmentedTimeRange) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setAsset(asset);
        reservation.setDate(segmentedTimeRange.getDate());
        reservation.setTimeStart(segmentedTimeRange.getStartTime());
        reservation.setTimeEnd(segmentedTimeRange.getEndTime());

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
