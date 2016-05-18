package com.AptiTekk.Agenda.core.entity;

import com.AptiTekk.Agenda.core.utilities.EqualsHelper;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Entity implementation class for Entity: ReservationApproval
 */
@Entity

public class ReservationApproval implements Serializable {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Reservation reservation;
    private boolean approved;
    private static final long serialVersionUID = 1L;

    public ReservationApproval() {
        super();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean getApproved() {
        return this.approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof ReservationApproval)) return false;

        ReservationApproval other = (ReservationApproval) o;

        return  EqualsHelper.areEquals(getUser(), other.getUser())
                && EqualsHelper.areEquals(getReservation(), other.getReservation())
                && EqualsHelper.areEquals(getApproved(), other.getApproved());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getUser(), getReservation(), getApproved());
    }
}
