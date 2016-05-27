package com.AptiTekk.Agenda.web.controllers;

import com.AptiTekk.Agenda.core.AssetTypeService;
import com.AptiTekk.Agenda.core.ReservationService;
import com.AptiTekk.Agenda.core.entity.*;
import com.AptiTekk.Agenda.core.utilities.AgendaLogger;
import com.AptiTekk.Agenda.core.utilities.TimeRange;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean(name = "TimeSelectionController")
@ViewScoped
public class TimeSelectionController {

    private Date date = Calendar.getInstance().getTime();

    private List<String> startTimes;
    private String startTime;


   public Date startDateTime;
    public Date endDateTime;


    private List<String> endTimes;
    private String endTime;
    private String lastStartTimeCalculated;

    private TimeRange allowedTimes;
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a");
    public static DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/YYYY");

    private List<AssetType> assetTypes;
    private AssetType selectedAssetType;

    private List<Asset> results;

    private Map<Asset, Boolean> checkMap = new HashMap<Asset, Boolean>();

    @Inject
    private ReservationService reservationService;

    @Inject
    private AssetTypeService assetTypeService;


    @PostConstruct
    public void init() {
        // ---- Temporary code to generate an allowed time TimeRange. Should
        // ideally come from a settings page somewhere. ----//
        Calendar startTime = Calendar.getInstance();
        startTime.set(0, 0, 0, 6, 30);

        Calendar endTime = Calendar.getInstance();
        endTime.set(0, 0, 0, 20, 30);

        allowedTimes = new TimeRange(startTime, endTime);

        // ---- End Temporary Code ----//
        calculateStartTimes();

        assetTypes = assetTypeService.getAll();



    }

    /**
     * Generates a list of times that can be selected for the "Start Time". This
     * list will range from the minimum time allowed to 30 minutes before the
     * maximum time allowed (To allow for the end time to be at least 30 minutes
     * away from the selected time)
     */
    private void calculateStartTimes() {
        startTimes = new ArrayList<>();

        Calendar counterCalendar = (Calendar) allowedTimes.getStartTime().clone();

        while ((double) (counterCalendar.get(Calendar.HOUR_OF_DAY)
                + (counterCalendar.get(Calendar.MINUTE) / 60d)) != (double) (allowedTimes.getEndTime()
                .get(Calendar.HOUR_OF_DAY) + (allowedTimes.getEndTime().get(Calendar.MINUTE) / 60d))) {
            String value = TIME_FORMAT.format(counterCalendar.getTime());
            startTimes.add(value);

            counterCalendar.add(Calendar.MINUTE, 30);
        }

    }

    /**
     * Generates a list of times that can be selected for the "End Time" based
     * on the currently selected time.
     */
    private void calculateEndTimes(String startTime) {
        endTimes = new ArrayList<>();

        try {
            Date parsedDate = TIME_FORMAT.parse(startTime);
            Calendar minCalendar = Calendar.getInstance();
            minCalendar.setTime(parsedDate);

            Calendar counterCalendar = (Calendar) minCalendar.clone();
            counterCalendar.add(Calendar.MINUTE, 30);

            while ((counterCalendar.get(Calendar.HOUR_OF_DAY)
                    + (counterCalendar.get(Calendar.MINUTE) / 60d)) != (allowedTimes.getEndTime()
                    .get(Calendar.HOUR_OF_DAY) + (allowedTimes.getEndTime().get(Calendar.MINUTE) / 60d))
                    + 0.5) {
                String value = TIME_FORMAT.format(counterCalendar.getTime());
                endTimes.add(value);

                counterCalendar.add(Calendar.MINUTE, 30);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            endTimes.add("--- Internal Server Error ---");
        }
    }


    public void onPicked( User user){
  ;
        DateFormat datetimeFormat = new SimpleDateFormat("dd/MM/YYYY h:mm a");
        try {
            startDateTime  = datetimeFormat.parse(DATE_FORMAT.format(date) + " " + startTime);
            endDateTime = datetimeFormat.parse(DATE_FORMAT.format(date) + " " + endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(user == null){
            System.out.println("User is null in onPicked() in TimeSelectionController");
        }
        for(Asset asset : getSelectedAssets()){
            Reservation reservation = new Reservation();
            reservation.setAsset(asset);
            reservation.setTimeStart(startDateTime);
            reservation.setTimeEnd(endDateTime);
            reservation.setUser(user);
            reservationService.insert(reservation);
        }

    }

    public List<Asset> getSelectedAssets() {
        List<Asset> result = new ArrayList<Asset>();
        for (Map.Entry<Asset, Boolean> entry : checkMap.entrySet()) {
            if (entry.getValue()) {
                result.add(entry.getKey());
            }
        }
        System.out.println("result size in getSelectedAssets: " + result.size());
        return result;
    }

    public void searchForReservable() {
        AgendaLogger.logVerbose("Searching for Asset");

        try {
        DateFormat datetimeFormat = new SimpleDateFormat("dd/MM/YYYY h:mm a");
        Date startDateTime = datetimeFormat.parse(DATE_FORMAT.format(date) + " " + startTime);
        Date endDateTime = datetimeFormat.parse(DATE_FORMAT.format(date) + " " + endTime);

            this.results = reservationService.findAvailableAssets(selectedAssetType, startDateTime, endDateTime);
        AgendaLogger.logVerbose(results.toString());
        } catch(ParseException e) {
            e.printStackTrace();
            this.results = null;
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        if (date != null) {
            AgendaLogger.logVerbose("Date Selected: " + DATE_FORMAT.format(date));
        } else {
            AgendaLogger.logVerbose("Date Empty - Setting to today");
            date = Calendar.getInstance().getTime();
        }

        this.startTime = null;
        this.endTimes = null;
        this.date = date;
    }

    public List<String> getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(List<String> startTimes) {
        this.startTimes = startTimes;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public List<String> getEndTimes(String startTime) {
        if(startTime == null || startTime.isEmpty())
            return null;
        if(startTime.equals(lastStartTimeCalculated) && endTimes != null)
            return endTimes;
        calculateEndTimes(startTime);
        return endTimes;
    }

    public void setEndTimes(List<String> endTimes) {
        this.endTimes = endTimes;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        AgendaLogger.logVerbose("End Time Selected: " + endTime);
        this.endTime = endTime;
    }

    public List<AssetType> getAssetTypes() {
        return assetTypes;
    }

    public void setAssetTypes(List<AssetType> assetTypes) {
        this.assetTypes = assetTypes;
    }

    public AssetType getSelectedAssetType() {
        return selectedAssetType;
    }

    public void setSelectedAssetType(AssetType selectedAssetType) {
        AgendaLogger.logVerbose("Asset Type Selected: " + selectedAssetType.getName());
        this.selectedAssetType = selectedAssetType;
        this.setDate(getDate());
    }

    public List<Asset> getResults()
    {
        return this.results;
    }

    public Map<Asset, Boolean> getCheckMap() {
        return checkMap;
    }

    public void setCheckMap(Map<Asset, Boolean> checkMap) {
        this.checkMap = checkMap;
    }
}
