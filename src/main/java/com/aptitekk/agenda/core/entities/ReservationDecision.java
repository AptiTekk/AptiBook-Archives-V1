/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities;

import com.aptitekk.agenda.core.utilities.EqualsHelper;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NamedQuery(name = "ReservationDecision.findAll", query = "SELECT r FROM ReservationDecision r")
public class ReservationDecision implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private UserGroup userGroup;

    @ManyToOne(optional = false)
    private Reservation reservation;

    private boolean approved;

    @Column(length = 512)
    private String comment;

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof ReservationDecision)) return false;

        ReservationDecision other = (ReservationDecision) o;

        return EqualsHelper.areEquals(getUser(), other.getUser())
                && EqualsHelper.areEquals(getUserGroup(), other.getUserGroup())
                && EqualsHelper.areEquals(getReservation(), other.getReservation())
                && EqualsHelper.areEquals(isApproved(), other.isApproved())
                && EqualsHelper.areEquals(getComment(), other.getComment());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getUser(), getUserGroup(), getReservation(), isApproved(), getComment());
    }

}