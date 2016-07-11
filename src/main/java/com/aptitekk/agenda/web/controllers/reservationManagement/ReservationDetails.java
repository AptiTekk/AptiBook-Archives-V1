/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.reservationManagement;

import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.entities.ReservationDecision;
import com.aptitekk.agenda.core.entities.UserGroup;

import java.util.Map;

public class ReservationDetails {

    private Reservation reservation;
    private UserGroup behalfUserGroup;
    private ReservationDecision currentDecision;
    private Map<UserGroup, ReservationDecision> hierarchyDecisions;

    public ReservationDetails(Reservation reservation, UserGroup behalfUserGroup, ReservationDecision currentDecision, Map<UserGroup, ReservationDecision> hierarchyDecisions) {
        this.reservation = reservation;
        this.behalfUserGroup = behalfUserGroup;
        this.currentDecision = currentDecision;
        this.hierarchyDecisions = hierarchyDecisions;
    }

    public Reservation getReservation() {
        return reservation;
    }

    /**
     * This UserGroup is the UserGroup that the currently authenticated User is acting on behalf of
     * when he or she chooses to approve or reject the Reservation.
     * @return The UserGroup.
     */
    public UserGroup getBehalfUserGroup() {
        return behalfUserGroup;
    }

    public ReservationDecision getCurrentDecision() {
        return currentDecision;
    }

    public Map<UserGroup, ReservationDecision> getHierarchyDecisions() {
        return hierarchyDecisions;
    }
}
