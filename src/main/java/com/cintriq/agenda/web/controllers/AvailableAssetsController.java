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

  /*  public void filter(Map<String, Boolean> checkMap){
      //  List<String> result = new ArrayList<String>();
        for (Map.Entry<String, Boolean> entry : checkMap.entrySet()) {
            if (entry.getValue()) {
                result.add(entry.getKey());
            }
        }
     *//*   for( Asset asset : availableAssets ){
            if(result != null) {
                for (int i = 0; i <= result.size(); i++) {
                    if (asset.getName() == result.get(i)) {
                        availableAssets.remove(asset);
                    }
                }
            }else {
                System.out.println("result is null");
            }
        }*//*
        //check if resul;t list is empty
      *//*  if(result != null){
            System.out.println("result is not empty");
            for(String resultItem : result){
                System.out.println("Printing result: " + result);
            }
        }else{
            System.out.println("result list is empty");
        }*//*
       // return result;
    }
*/


    public void searchForAssets(AssetType assetType, TimeRange timeRange) {
        this.tagController.availableFilterTags(assetType);
        this.tagController.filter();
        this.result = tagController.getResult();
        this.availableAssets = reservationService.findAvailableAssets(assetType, timeRange, 0f);
        boolean temp;
        for( Asset asset : availableAssets ){
            if(result != null && result.size() > 0 ) {
               temp = false;
                for(Tag tag : asset.getTags()){
                    if(result.contains(tag.getName().toString())){
                        System.out.println("MISSION SUCCESS");
                        temp = true;
                        //this.availableAssets = tag.getAssets();
                        //availableAssets.remove(asset);
                    }

                }
                if(temp) {
                    if(asset != null){
                        System.out.println("asset is not null");
                        Asset assetTemp;
                        assetTemp = asset;
                        if(!filteredAssets.contains(assetTemp)) {
                            filteredAssets.add(assetTemp);
                        }else{
                            System.out.println("Already contains: " + assetTemp.getName().toString());
                        }
                    }else{
                        System.out.println("asset is null");
                    }
                }
            }else {
                System.out.println("result is null");
            }
        }
        if(result != null && result.size() > 0 && filteredAssets.size() > 0) {
            System.out.println("filteredAsset size: " + filteredAssets.size());

           for(int i = 0; i < filteredAssets.size(); i++){
              if(filteredAssets.get(i) != null) {
                  if(!(availableAssets.size() == filteredAssets.size())){
                      availableAssets.clear();
                  }
                  System.out.println("filteredAssets print: " + filteredAssets.get(i).getName().toString());
                  availableAssets.add(filteredAssets.get(i));
              }
           }
            System.out.println("availableAssets size: " + availableAssets.size());
            //this.availableAssets = filteredAssets;
        }else {
            System.out.println("Something in result is null");
        }

        //TagController.availableFilterTags(assetType);
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
