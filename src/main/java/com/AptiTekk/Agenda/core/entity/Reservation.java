package com.AptiTekk.Agenda.core.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the Reservation database table.
 * 
 */
@Entity
@NamedQuery(name = "Reservation.findAll", query = "SELECT r FROM Reservation r")
public class Reservation implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @Temporal(TemporalType.TIMESTAMP)
  private Date dateCreated;

  @Column(length = 32)
  private String title;

  @Lob
  private String description;

  private byte pendingApproval;

  @Temporal(TemporalType.TIMESTAMP)
  private Date timeEnd;

  @Temporal(TemporalType.TIMESTAMP)
  private Date timeStart;

  // bi-directional many-to-one association to Room
  @ManyToOne
  private Reservable reservable;

  // bi-directional many-to-one association to User
  @ManyToOne
  private User user;

  @OneToMany(mappedBy = "reservation")
  private List<ReservationFieldEntry> fieldEntries;

  @OneToMany(mappedBy = "reservation")
  private List<ReservationApproval> approvals;

  @Column(length = 128)
  private String googleEventId;

  public Reservation() {}

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Date getDateCreated() {
    return this.dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
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

  public Date getTimeEnd() {
    return this.timeEnd;
  }

  public void setTimeEnd(Date timeEnd) {
    this.timeEnd = timeEnd;
  }

  public Date getTimeStart() {
    return this.timeStart;
  }

  public void setTimeStart(Date timeStart) {
    this.timeStart = timeStart;
  }

  public Reservable getReservable() {
    return this.reservable;
  }

  public void setReservable(Reservable reservable) {
    this.reservable = reservable;
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
}
