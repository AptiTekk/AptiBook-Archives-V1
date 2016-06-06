package com.cintriq.agenda.web.controllers;

import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.ReservationService;
import com.cintriq.agenda.core.entity.Asset;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.Reservation;
import com.cintriq.agenda.core.entity.User;
import com.cintriq.agenda.core.utilities.AgendaLogger;
import com.cintriq.agenda.core.utilities.TimeRange;
import com.sun.javafx.binding.StringFormatter;

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

    private Date selectedDate = Calendar.getInstance().getTime();

    private List<String> startTimes;
    private String selectedStartTimeString;

    private List<String> endTimes;
    private String selectedEndTimeString;

    //TODO: Figure out what this is for
    private String lastStartTimeCalculated;

    private TimeRange allowedTimes;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/YYYY");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a");
    private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/YYYY h:mm a");

    private List<AssetType> assetTypes;
    private AssetType selectedAssetType;

    private List<Asset> results;

    private Map<Asset, Boolean> checkMap = new HashMap<>();

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

    static Calendar convertDateAndTimeStringToCalendar(Date date, String timeString) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(DATETIME_FORMAT.parse(DATE_FORMAT.format(date) + " " + timeString));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return calendar;
    }

    static String convertCalendarToTimeString(Calendar calendar) {
        if (calendar != null) {
            return calendar.get(Calendar.HOUR) + ":" + String.format("%02d", calendar.get(Calendar.MINUTE)) + " " + (calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
        }

        return null;
    }

    public void onPicked(User user) {
        if (user != null) {
            for (Asset asset : getSelectedAssets()) {
                Reservation reservation = new Reservation();
                reservation.setAsset(asset);
                reservation.setTimeStart(convertDateAndTimeStringToCalendar(selectedDate, selectedStartTimeString));
                reservation.setTimeEnd(convertDateAndTimeStringToCalendar(selectedDate, selectedEndTimeString));
                reservation.setUser(user);
                try {
                    reservationService.insert(reservation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

    public void searchForAssets() {
        AgendaLogger.logVerbose("Searching for Asset");

        this.results = reservationService.findAvailableAssets(
                selectedAssetType,
                convertDateAndTimeStringToCalendar(selectedDate, selectedStartTimeString),
                convertDateAndTimeStringToCalendar(selectedDate, selectedEndTimeString),
                0f);

        AgendaLogger.logVerbose(results.toString());
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        if (selectedDate != null) {
            AgendaLogger.logVerbose("Date Selected: " + DATE_FORMAT.format(selectedDate));
        } else {
            AgendaLogger.logVerbose("Date Empty - Setting to today");
            selectedDate = Calendar.getInstance().getTime();
        }

        this.selectedStartTimeString = null;
        this.endTimes = null;
        this.selectedDate = selectedDate;
    }

    public List<String> getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(List<String> startTimes) {
        this.startTimes = startTimes;
    }

    public String getSelectedStartTimeString() {
        return selectedStartTimeString;
    }

    public void setSelectedStartTimeString(String selectedStartTimeString) {
        this.selectedStartTimeString = selectedStartTimeString;
    }

    public List<String> getEndTimes(String startTime) {
        if (startTime == null || startTime.isEmpty())
            return null;
        if (startTime.equals(lastStartTimeCalculated) && endTimes != null)
            return endTimes;
        calculateEndTimes(startTime);
        return endTimes;
    }

    public void setEndTimes(List<String> endTimes) {
        this.endTimes = endTimes;
    }

    public String getSelectedEndTimeString() {
        return selectedEndTimeString;
    }

    public void setSelectedEndTimeString(String selectedEndTimeString) {
        AgendaLogger.logVerbose("End Time Selected: " + selectedEndTimeString);
        this.selectedEndTimeString = selectedEndTimeString;
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
        this.setSelectedDate(getSelectedDate());
    }

    public List<Asset> getResults() {
        return this.results;
    }

    public Map<Asset, Boolean> getCheckMap() {
        return checkMap;
    }

    public void setCheckMap(Map<Asset, Boolean> checkMap) {
        this.checkMap = checkMap;
    }
}
