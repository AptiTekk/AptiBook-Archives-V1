package com.cintriq.agenda.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import com.cintriq.agenda.core.UserGroupService;
import com.cintriq.agenda.core.UserService;
import com.cintriq.agenda.core.utilities.EqualsHelper;
import com.cintriq.agenda.core.utilities.notification.OmitInjection;


/**
 * The persistent class for the User database table.
 */
@Entity
@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String email;

    private boolean enabled;

    private String firstName;

    private String lastName;

    private String location;

    private byte[] password;

    private String phoneNumber;

    private String username;

    // bi-directional many-to-one association to Reservation
    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations = new ArrayList<>();

    @ManyToMany
    private List<UserGroup> userGroups = new ArrayList<>();

    private Boolean receiveEmailNotifications;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();

    public User() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @OmitInjection
    public byte[] getPassword() {
        return this.password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Reservation> getReservations() {
        return this.reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Reservation addReservation(Reservation reservation) {
        getReservations().add(reservation);
        return reservation;
    }

    public Reservation removeReservation(Reservation reservation) {
        getReservations().remove(reservation);
        return reservation;
    }

    public List<UserGroup> getUserGroups() {
        return this.userGroups;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    /**
     * Gets the full name of the user, or the username if the first name is empty.
     *
     * @return The user's full name.
     */
    public String getFullname() {
        if (getFirstName() == null || getFirstName().isEmpty())
            return getUsername();
        else
            return getFirstName() + " " + (getLastName() == null ? "" : getLastName());
    }

    public boolean isAdmin() {
        return userGroups.stream().anyMatch((userGroup) -> (userGroup.getName().equals(UserGroupService.ROOT_GROUP_NAME)));
    }

    public Boolean getReceiveEmailNotifications() {
        return receiveEmailNotifications;
    }

    public void setReceiveEmailNotifications(Boolean receiveEmailNotifications) {
        this.receiveEmailNotifications = receiveEmailNotifications;
    }

    public UserGroup addGroup(UserGroup userGroup) {
        getUserGroups().add(userGroup);
        return userGroup;
    }

    public UserGroup removeGroup(UserGroup userGroup) {
        getUserGroups().remove(userGroup);
        return userGroup;
    }

    public boolean isImmutableUsername() {
        return this.username != null && this.username.equals(UserService.ADMIN_USERNAME);
    }

    public Notification addNotification(Notification notification) {
        getNotifications().add(notification);
        return notification;
    }

    public Notification removeNotification(Notification notification) {
        getNotifications().remove(notification);
        return notification;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof User)) return false;

        User other = (User) o;

        return EqualsHelper.areEquals(getUsername(), other.getUsername())
                && EqualsHelper.areEquals(getPassword(), other.getPassword())
                && EqualsHelper.areEquals(getFirstName(), other.getFirstName())
                && EqualsHelper.areEquals(getLastName(), other.getLastName())
                && EqualsHelper.areEquals(getEmail(), other.getEmail())
                && EqualsHelper.areEquals(getPhoneNumber(), other.getPhoneNumber())
                && EqualsHelper.areEquals(getLocation(), other.getLocation())
                && EqualsHelper.areEquals(getEnabled(), other.getEnabled());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getUsername(), getPassword(), getFirstName(), getLastName(), getEmail(), getPhoneNumber(), getLocation(), getEnabled());
    }

}
