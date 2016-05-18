package com.AptiTekk.Agenda.core.entity;

import com.AptiTekk.Agenda.core.utilities.EqualsHelper;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the Room database table.
 */
@Entity
@NamedQuery(name = "Reservable.findAll", query = "SELECT r FROM Reservable r")
public class Reservable implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date availabilityEnd;

    @Temporal(TemporalType.TIMESTAMP)
    private Date availabilityStart;

    private String name;

    private Boolean needsApproval = false;

    // bi-directional many-to-one association to Reservation
    @OneToMany(mappedBy = "reservable")
    private List<Reservation> reservations;

    @ManyToOne
    private ReservableType type;

    @ManyToOne
    private UserGroup owner;

    private String imageFileName;

    public Reservable() {
    }

    public Reservable(String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getAvailabilityEnd() {
        return this.availabilityEnd;
    }

    public void setAvailabilityEnd(Date availabilityEnd) {
        this.availabilityEnd = availabilityEnd;
    }

    public Date getAvailabilityStart() {
        return this.availabilityStart;
    }

    public void setAvailabilityStart(Date availabilityStart) {
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
        reservation.setReservable(this);

        return reservation;
    }

    public Reservation removeReservation(Reservation reservation) {
        getReservations().remove(reservation);
        reservation.setReservable(null);

        return reservation;
    }

    public ReservableType getType() {
        return type;
    }

    public void setType(ReservableType type) {
        this.type = type;
    }

    public UserGroup getOwner() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Reservable)) return false;

        Reservable other = (Reservable) o;

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
