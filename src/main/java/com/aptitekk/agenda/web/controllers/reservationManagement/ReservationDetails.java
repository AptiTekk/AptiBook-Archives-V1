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

    /**
     * This list contains the User Groups with decisions that will be overridden if the behalfUserGroup chooses to approve.
     */
    private List<UserGroup> lowerApprovalOverrides;

    /**
     * This list contains the User Groups with decisions that will be overridden if the behalfUserGroup chooses to reject.
     */
    private List<UserGroup> lowerRejectionOverrides;


    public ReservationDetails(Reservation reservation, UserGroup behalfUserGroup, ReservationDecision currentDecision, LinkedHashMap<UserGroup, ReservationDecision> hierarchyDecisions) {
        this.reservation = reservation;
        this.behalfUserGroup = behalfUserGroup;
        this.currentDecision = currentDecision;
        this.hierarchyDecisions = hierarchyDecisions;
        this.hierarchyGroups = new ArrayList<>(hierarchyDecisions.keySet());

        organizeDecisions();
    }

    private void organizeDecisions() {
        overridingDecisions = new HashMap<>();
        lowerApprovalOverrides = new ArrayList<>();
        lowerRejectionOverrides = new ArrayList<>();

        //Iterate through all the UserGroups of the decision hierarchy.
        for (UserGroup userGroup : hierarchyDecisions.keySet()) {
            boolean reachedUserGroup = false;
            ReservationDecision overridingDecision = null;

            //Iterate backwards (from bottom to top) along the hierarchyDecisions map.
            ListIterator<Map.Entry<UserGroup, ReservationDecision>> entryListIterator = new ArrayList<>(hierarchyDecisions.entrySet()).listIterator(hierarchyDecisions.size());
            while (entryListIterator.hasPrevious()) {
                Map.Entry<UserGroup, ReservationDecision> entry = entryListIterator.previous();

                //We have reached the current User Group -- now we will find the decision that overrides theirs (if one exists).
                if (!reachedUserGroup && entry.getKey().equals(userGroup)) {
                    reachedUserGroup = true;
                    continue;
                }

                //All these Groups are above the current User Group.
                //Check each one to see if they are overriding the current User Group.
                if (reachedUserGroup) {

                    //If this Group has made a decision and the current User Group is either undecided or conflicts with this Group's decision...
                    if (entry.getValue() != null && (hierarchyDecisions.get(userGroup) == null || entry.getValue().isApproved() != hierarchyDecisions.get(userGroup).isApproved())) {
                        overridingDecision = entry.getValue(); //Then this is ultimately the overriding decision.
                    }
                }
            }

            overridingDecisions.putIfAbsent(userGroup, overridingDecision);
        }

        boolean reachedUserGroup = false;
        //Iterate through all the entries of the hierarchyDecisions map.
        for (Map.Entry<UserGroup, ReservationDecision> entry : hierarchyDecisions.entrySet()) {

            //We have reached the behalfUserGroup.
            //Now we will find what will be overridden depending on their decision.
            if (!reachedUserGroup && entry.getKey().equals(behalfUserGroup)) {
                reachedUserGroup = true;
                continue;
            }

            //All these Groups are below the behalfUserGroup.
            if (reachedUserGroup) {
                //If this Group has not decided yet, then add it to both override lists.
                if (entry.getValue() == null) {
                    lowerApprovalOverrides.add(entry.getKey());
                    lowerRejectionOverrides.add(entry.getKey());
                } else if (entry.getValue().isApproved()) { //If the Group approved, add it to the rejection override list.
                    lowerRejectionOverrides.add(entry.getKey());
                } else { //Otherwise (they rejected), add it to the approval override list.
                    lowerApprovalOverrides.add(entry.getKey());
                }
            }
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

    public List<UserGroup> getLowerApprovalOverrides() {
        return lowerApprovalOverrides;
    }

    public List<UserGroup> getLowerRejectionOverrides() {
        return lowerRejectionOverrides;
    }
}
