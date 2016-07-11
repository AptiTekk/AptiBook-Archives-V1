/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.reservationManagement;

import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.entities.ReservationDecision;
import com.aptitekk.agenda.core.entities.UserGroup;

import java.util.*;

public class ReservationDetails {

    private Reservation reservation;
    private UserGroup behalfUserGroup;
    private ReservationDecision currentDecision;
    private LinkedHashMap<UserGroup, ReservationDecision> hierarchyDecisions;
    private List<UserGroup> hierarchyGroups;
    private Map<UserGroup, ReservationDecision> overridingDecisions;

    public ReservationDetails(Reservation reservation, UserGroup behalfUserGroup, ReservationDecision currentDecision, LinkedHashMap<UserGroup, ReservationDecision> hierarchyDecisions) {
        this.reservation = reservation;
        this.behalfUserGroup = behalfUserGroup;
        this.currentDecision = currentDecision;
        this.hierarchyDecisions = hierarchyDecisions;
        this.hierarchyGroups = new ArrayList<>(hierarchyDecisions.keySet());

        findOverridingDecisions();
    }

    private void findOverridingDecisions() {
        overridingDecisions = new HashMap<>();

        for (UserGroup userGroup : hierarchyDecisions.keySet()) {
            boolean reachedUserGroup = false;
            ReservationDecision overridingDecision = null;

            ListIterator<Map.Entry<UserGroup, ReservationDecision>> entryListIterator = new ArrayList<>(hierarchyDecisions.entrySet()).listIterator(hierarchyDecisions.size());
            while (entryListIterator.hasPrevious()) {
                Map.Entry<UserGroup, ReservationDecision> entry = entryListIterator.previous();
                if (!reachedUserGroup && entry.getKey().equals(userGroup)) {
                    reachedUserGroup = true;
                    continue;
                }
                if (reachedUserGroup) {
                    if (entry.getValue() != null && (hierarchyDecisions.get(userGroup) == null || entry.getValue().isApproved() != hierarchyDecisions.get(userGroup).isApproved())) {
                        overridingDecision = entry.getValue();
                    }
                }
            }

            overridingDecisions.putIfAbsent(userGroup, overridingDecision);
        }
    }

    public Reservation getReservation() {
        return reservation;
    }

    /**
     * This UserGroup is the UserGroup that the currently authenticated User is acting on behalf of
     * when he or she chooses to approve or reject the Reservation.
     *
     * @return The UserGroup.
     */
    public UserGroup getBehalfUserGroup() {
        return behalfUserGroup;
    }

    public ReservationDecision getCurrentDecision() {
        return currentDecision;
    }

    public List<UserGroup> getUserGroups() {
        return hierarchyGroups;
    }

    public ReservationDecision getReservationDecisionFromUserGroup(UserGroup userGroup) {
        return hierarchyDecisions.get(userGroup);
    }

    public ReservationDecision getOverridingDecisionForGroup(UserGroup userGroup) {
        return overridingDecisions.get(userGroup);
    }
}
