/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities;

import com.aptitekk.agenda.core.entities.util.MultiTenantEntity;
import com.aptitekk.agenda.core.util.EqualsHelper;
import com.aptitekk.agenda.core.util.time.SegmentedTime;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


@Entity
public class Reservation extends MultiTenantEntity implements Serializable {
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
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

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.REMOVE)
    private List<ReservationDecision> decisions;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.REMOVE)
    private List<ReservationFieldEntry> fieldEntries;

    public int getId() {
        return id;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public String getFormattedDateCreated() {
        return new SimpleDateFormat("EEEE, dd MMMM, yyyy").format(dateCreated.getTime());
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

    public boolean isPending() {
        return status == Status.PENDING;
    }

    public boolean isApproved() {
        return status == Status.APPROVED;
    }

    public boolean isRejected() {
        return status == Status.REJECTED;
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

    public String getFormattedDate() {
        return new SimpleDateFormat("EEEE, dd MMMM, yyyy").format(date.getTime());
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

    public List<ReservationDecision> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<ReservationDecision> decisions) {
        this.decisions = decisions;
    }

    public String getReservationFieldEntryText(ReservationField reservationField) {
        if (reservationField == null)
            return null;

        for (ReservationFieldEntry fieldEntry : fieldEntries) {
            if (fieldEntry.getField().equals(reservationField))
                return fieldEntry.getContent();
        }
        return null;
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
