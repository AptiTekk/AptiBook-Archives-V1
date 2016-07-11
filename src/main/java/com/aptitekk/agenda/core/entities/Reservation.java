/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities;

import com.aptitekk.agenda.core.utilities.EqualsHelper;
import com.aptitekk.agenda.core.utilities.time.SegmentedTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;


/**
 * The persistent class for the Reservation database table.
 */
@Entity
@NamedQuery(name = "Reservation.findAll", query = "SELECT r FROM Reservation r")
public class Reservation implements Serializable {
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    @Entity(name = "ReservationDecision")
    @NamedQuery(name = "Decision.findAll", query = "SELECT r FROM ReservationDecision r")
    public class Decision implements Serializable {

        @Id
        @GeneratedValue
        private int id;

        @ManyToOne
        private User user;

        @ManyToOne
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
    }

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateCreated = Calendar.getInstance();

    @Column(length = 32)
    private String title;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(columnDefinition = "time")
    private SegmentedTime timeStart;

    @Column(columnDefinition = "time")
    private SegmentedTime timeEnd;

    @Temporal(TemporalType.DATE)
    private Calendar date;

    @ManyToOne
    private Asset asset;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "reservation")
    private List<Decision> decisions;

    @OneToMany(mappedBy = "reservation")
    private List<ReservationFieldEntry> fieldEntries;

    public int getId() {
        return id;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SegmentedTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(SegmentedTime timeStart) {
        this.timeStart = timeStart;
    }

    public SegmentedTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(SegmentedTime timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Decision> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<Decision> decisions) {
        this.decisions = decisions;
    }

    public List<ReservationFieldEntry> getFieldEntries() {
        return fieldEntries;
    }

    public void setFieldEntries(List<ReservationFieldEntry> fieldEntries) {
        this.fieldEntries = fieldEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Reservation)) return false;

        Reservation other = (Reservation) o;

        return EqualsHelper.areEquals(getTitle(), other.getTitle())
                && EqualsHelper.areEquals(getDateCreated(), other.getDateCreated())
                && EqualsHelper.areEquals(getStatus(), other.getStatus())
                && EqualsHelper.areEquals(getTimeStart(), other.getTimeStart())
                && EqualsHelper.areEquals(getTimeEnd(), other.getTimeEnd());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getTitle(), getDateCreated(), getStatus(), getTimeStart(), getTimeEnd());
    }
}
