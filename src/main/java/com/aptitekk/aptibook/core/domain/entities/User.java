/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.entities;

import com.aptitekk.aptibook.core.domain.entities.property.Property;
import com.aptitekk.aptibook.core.domain.services.UserService;
import com.aptitekk.aptibook.core.util.EqualsHelper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "\"user\"")
public class User extends MultiTenantEntity implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    private String username;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String location;

    private byte[] password;

    private boolean wantsEmailNotifications;

    private String verificationCode;

    private boolean verified;

    @Enumerated(EnumType.STRING)
    private User.Key userState;

    public enum Key {
        APPROVED,
        PENDING;
    }


    @ManyToMany
    private List<UserGroup> userGroups = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @OrderBy("dateCreated desc")
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ReservationDecision> reservationDecisions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    private List<Permission> permissions;

    public int getId() {
        return this.id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verficationCode) {
        this.verificationCode = verficationCode;
    }

    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<ReservationDecision> getReservationDecisions() {
        return reservationDecisions;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean getWantsEmailNotifications() {
        return wantsEmailNotifications;
    }

    public Key getUserState() {
        return userState;
    }

    public void setUserState(Key userState) {
        this.userState = userState;
    }

    public void setWantsEmailNotifications(boolean wantsEmailNotifications) {
        this.wantsEmailNotifications = wantsEmailNotifications;
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
            return getFirstName() + (getLastName() == null ? "" : " " + getLastName());
    }

    /**
     * Determines if the user is the admin.
     *
     * @return True if the user is the admin, false otherwise.
     */
    public boolean isAdmin() {
        return getUsername().equalsIgnoreCase(UserService.ADMIN_USERNAME);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof User)) return false;

        User other = (User) o;

        return EqualsHelper.areEquals(getUsername(), other.getUsername())
                && EqualsHelper.areEquals(getFirstName(), other.getFirstName())
                && EqualsHelper.areEquals(getLastName(), other.getLastName())
                && EqualsHelper.areEquals(getPhoneNumber(), other.getPhoneNumber())
                && EqualsHelper.areEquals(getLocation(), other.getLocation())
                && EqualsHelper.areEquals(getPassword(), other.getPassword())
                && EqualsHelper.areEquals(getVerificationCode(), other.getVerificationCode())
                && EqualsHelper.areEquals(isVerified(), other.isVerified());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getUsername(), getFirstName(), getLastName(), getPhoneNumber(), getLocation(), getPassword(), getVerificationCode(), isVerified());
    }

}
