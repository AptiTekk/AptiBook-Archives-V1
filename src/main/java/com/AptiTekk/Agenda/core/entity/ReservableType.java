package com.AptiTekk.Agenda.core.entity;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReservableType that = (ReservableType) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return 31 * (id + getClass().getName().hashCode());
    }
}
