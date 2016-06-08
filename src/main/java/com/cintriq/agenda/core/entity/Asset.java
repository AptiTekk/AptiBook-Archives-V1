package com.cintriq.agenda.core.entity;

import com.cintriq.agenda.core.utilities.EqualsHelper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the Room database table.
 */
@Entity
@NamedQuery(name = "Asset.findAll", query = "SELECT r FROM Asset r")
public class Asset implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar availabilityEnd;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar availabilityStart;

    private String name;

    private Boolean needsApproval = false;

    // bi-directional many-to-one association to Reservation
    @OneToMany(mappedBy = "asset")
    private List<Reservation> reservations;

    @ManyToOne
    private AssetType type;

    @ManyToOne
    private UserGroup owner;

    @ManyToMany
    private List<Tag> tags;

    private String imageFileName;

    public Asset() {
    }

    public Asset(String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getAvailabilityEnd() {
        return this.availabilityEnd;
    }

    public void setAvailabilityEnd(Calendar availabilityEnd) {
        this.availabilityEnd = availabilityEnd;
    }

    public Calendar getAvailabilityStart() {
        return this.availabilityStart;
    }

    public void setAvailabilityStart(Calendar availabilityStart) {
        this.availabilityStart = availabilityStart;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNeedsApproval() {
        return this.needsApproval;
    }

    public void setNeedsApproval(Boolean needsApproval) {
        this.needsApproval = needsApproval;
    }

    public List<Reservation> getReservations() {
        return this.reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Reservation addReservation(Reservation reservation) {
        getReservations().add(reservation);
        reservation.setAsset(this);

        return reservation;
    }

    public Reservation removeReservation(Reservation reservation) {
        getReservations().remove(reservation);
        reservation.setAsset(null);

        return reservation;
    }

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    public UserGroup getOwner() {
        if(owner == null){
            System.out.println("owner is null in Asset entity");
        }
        return owner;

    }

    public void setOwner(UserGroup owner) {
        this.owner = owner;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Asset)) return false;

        Asset other = (Asset) o;

        return EqualsHelper.areEquals(getName(), other.getName())
                && EqualsHelper.areEquals(getAvailabilityStart(), other.getAvailabilityStart())
                && EqualsHelper.areEquals(getAvailabilityEnd(), other.getAvailabilityEnd())
                && EqualsHelper.areEquals(getNeedsApproval(), other.getNeedsApproval())
                && EqualsHelper.areEquals(getImageFileName(), other.getImageFileName());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getName(), getAvailabilityStart(), getAvailabilityEnd(), getNeedsApproval(), getImageFileName());
    }

}
