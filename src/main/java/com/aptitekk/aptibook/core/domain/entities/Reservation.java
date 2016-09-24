/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.entities;

import com.aptitekk.aptibook.core.util.EqualsHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.*;
import java.io.Serializable;
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

    @Column(columnDefinition = "TIMESTAMP")
    private DateTime dateCreated = new DateTime(DateTimeZone.UTC);

    @Column(length = 32)
    private String title;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(columnDefinition = "TIMESTAMP")
    private DateTime startTime;

    @Column(columnDefinition = "TIMESTAMP")
    private DateTime endTime;

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

    public DateTime getDateCreated() {
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

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
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
                && EqualsHelper.areEquals(getStartTime(), other.getStartTime())
                && EqualsHelper.areEquals(getEndTime(), other.getEndTime());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getTitle(), getDateCreated(), getStatus(), getStartTime(), getEndTime());
    }
}
