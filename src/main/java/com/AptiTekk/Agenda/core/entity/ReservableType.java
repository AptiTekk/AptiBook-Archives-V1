package com.AptiTekk.Agenda.core.entity;

import com.AptiTekk.Agenda.core.utilities.EqualsHelper;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: ReservableType
 */
@Entity
public class ReservableType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    @OneToMany(mappedBy = "type", cascade = CascadeType.REMOVE)
    private List<Reservable> reservables;

    @OneToMany(mappedBy = "reservableType", cascade = CascadeType.REMOVE)
    @OrderColumn(name = "Order")
    private List<ReservationField> reservationFields;

    private static final long serialVersionUID = 1L;

    public ReservableType() {
        super();
    }

    public ReservableType(String name) {
        super();
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Reservable> getReservables() {
        return reservables;
    }

    public void setReservables(List<Reservable> reservables) {
        this.reservables = reservables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ReservationField> getReservationFields() {
        return reservationFields;
    }

    public void setReservationFields(List<ReservationField> reservationFields) {
        this.reservationFields = reservationFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof ReservableType)) return false;

        ReservableType other = (ReservableType) o;

        return EqualsHelper.areEquals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getName());
    }
}
