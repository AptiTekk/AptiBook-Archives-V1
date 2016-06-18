package com.aptitekk.agenda.core.entity;

import com.aptitekk.agenda.core.utilities.EqualsHelper;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Entity implementation class for Entity: ReservationFieldEntry
 */
@Entity
public class ReservationFieldEntry implements Serializable {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    private Reservation reservation;
    @ManyToOne
    private ReservationField field;
    @Lob
    private String content;
    private static final long serialVersionUID = 1L;

    public ReservationFieldEntry() {
        super();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return this.reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public ReservationField getField() {
        return this.field;
    }

    public void setField(ReservationField field) {
        this.field = field;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof ReservationFieldEntry)) return false;

        ReservationFieldEntry other = (ReservationFieldEntry) o;

        return EqualsHelper.areEquals(getContent(), other.getContent());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getContent());
    }
}
