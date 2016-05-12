package com.AptiTekk.Agenda.core.entity;

import java.io.Serializable;

import javax.jws.soap.SOAPBinding.Use;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the Group database table.
 */
@Entity
public class UserGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    // bi-directional many-to-one association to Room
    @ManyToMany(mappedBy = "owners")
    private List<Reservable> rooms;

    // bi-directional many-to-many association to User
    @ManyToMany(mappedBy = "userGroups")
    private List<User> users = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private UserGroup parent;

    @OneToMany
    @OrderColumn
    @JoinColumn(name = "parent_id")
    private List<UserGroup> children = new ArrayList<>();

    public UserGroup() {
    }

    public UserGroup(String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Reservable> getRooms() {
        return this.rooms;
    }

    public void setRooms(List<Reservable> rooms) {
        this.rooms = rooms;
    }

    public Reservable addReservable(Reservable room) {
        getRooms().add(room);
        return room;
    }

    public Reservable removeReservable(Reservable room) {
        getRooms().remove(room);
        return room;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User addUser(User user) {
        getUsers().add(user);
        return user;
    }

    public User removeUser(User user) {
        getUsers().remove(user);
        return user;
    }

    public UserGroup getParent() {
        return parent;
    }

    public void setParent(UserGroup parent) {
        this.parent = parent;
    }

    public List<UserGroup> getChildren() {
        return children;
    }

    public void setChildren(List<UserGroup> children) {
        this.children = children;
    }

    public String toString() {
        return this.getName();
    }

    public boolean isImmutable() {
        return this.getParent() == null;
    }

}
