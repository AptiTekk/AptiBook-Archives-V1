package com.AptiTekk.Agenda.core.entity;

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
        if (o == null || getClass() != o.getClass()) return false;

        ReservationApproval that = (ReservationApproval) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return 31 * (id + getClass().getName().hashCode());
    }
}
