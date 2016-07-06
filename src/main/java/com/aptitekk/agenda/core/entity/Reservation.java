/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entity;

import com.aptitekk.agenda.core.entity.enums.ReservationStatus;
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
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateCreated;

    @Column(length = 32)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private ReservationStatus status;

    @Column(columnDefinition = "time")
    private SegmentedTime timeStart;

    @Column(columnDefinition = "time")
    private SegmentedTime timeEnd;

    @Temporal(TemporalType.DATE)
    private Calendar date;

    // bi-directional many-to-one association to Room
    @ManyToOne
    private Asset asset;

    // bi-directional many-to-one association to User
    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "reservation")
    private List<ReservationFieldEntry> fieldEntries;

    @OneToMany(mappedBy = "reservation")
    private List<ReservationApproval> approvals;

    @Column(length = 128)
    private String googleEventId;

    public Reservation() {
        this.setDateCreated(Calendar.getInstance());
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReservationStatus getStatus() {
        return this.status;
    }

    public void setStatus(ReservationStatus pendingApproval) {
        this.status = pendingApproval;
    }

    public SegmentedTime getTimeStart() {
        return this.timeStart;
    }

    public void setTimeStart(SegmentedTime timeStart) {
        this.timeStart = timeStart;
    }

    public SegmentedTime getTimeEnd() {
        return this.timeEnd;
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
        if (this.asset == null) {
            System.out.println("asset is null");
        }
        return this.asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ReservationFieldEntry> getFieldEntries() {
        return fieldEntries;
    }

    public void setFieldEntries(List<ReservationFieldEntry> fieldEntries) {
        this.fieldEntries = fieldEntries;
    }

    public List<ReservationApproval> getApprovals() {
        return approvals;
    }

    public void setApprovals(List<ReservationApproval> approvals) {
        this.approvals = approvals;
    }

    public String getGoogleEventId() {
        return googleEventId;
    }

    public void setGoogleEventId(String googleEventId) {
        this.googleEventId = googleEventId;
    }

    @PrePersist
    public void onCreate() {
        status = ReservationStatus.PENDING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Reservation)) return false;

        Reservation other = (Reservation) o;

        return EqualsHelper.areEquals(getTitle(), other.getTitle())
                && EqualsHelper.areEquals(getDescription(), other.getDescription())
                && EqualsHelper.areEquals(getDateCreated(), other.getDateCreated())
                && EqualsHelper.areEquals(getGoogleEventId(), other.getGoogleEventId())
                && EqualsHelper.areEquals(getStatus(), other.getStatus())
                && EqualsHelper.areEquals(getTimeStart(), other.getTimeStart())
                && EqualsHelper.areEquals(getTimeEnd(), other.getTimeEnd());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getTitle(), getDescription(), getDateCreated(), getGoogleEventId(), getStatus(), getTimeStart(), getTimeEnd());
    }
}
