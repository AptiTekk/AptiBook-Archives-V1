package com.cintriq.agenda.core.entity;

import com.cintriq.agenda.core.utilities.EqualsHelper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
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

    private byte pendingApproval;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar timeEnd;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar timeStart;

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

    public byte getPendingApproval() {
        return this.pendingApproval;
    }

    public void setPendingApproval(byte pendingApproval) {
        this.pendingApproval = pendingApproval;
    }

    public Calendar getTimeEnd() {
        return this.timeEnd;
    }

    public void setTimeEnd(Calendar timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Calendar getTimeStart() {
        return this.timeStart;
    }

    public void setTimeStart(Calendar timeStart) {
        this.timeStart = timeStart;
    }

    public Asset getAsset() {
        if(this.asset == null) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Reservation)) return false;

        Reservation other = (Reservation) o;

        return  EqualsHelper.areEquals(getTitle(), other.getTitle())
                && EqualsHelper.areEquals(getDescription(), other.getDescription())
                && EqualsHelper.areEquals(getDateCreated(), other.getDateCreated())
                && EqualsHelper.areEquals(getGoogleEventId(), other.getGoogleEventId())
                && EqualsHelper.areEquals(getPendingApproval(), other.getPendingApproval())
                && EqualsHelper.areEquals(getTimeStart(), other.getTimeStart())
                && EqualsHelper.areEquals(getTimeEnd(), other.getTimeEnd());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getTitle(), getDescription(), getDateCreated(), getGoogleEventId(), getPendingApproval(), getTimeStart(), getTimeEnd());
    }
}
