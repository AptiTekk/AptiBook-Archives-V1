package com.cintriq.agenda.core.utilities.notification;

import javax.mail.Multipart;

import com.cintriq.agenda.core.entity.User;

public class EmailNotification {
  
  private User to;
  private String subject;
  
  private Multipart content;
  
  public EmailNotification(User to) {
    setTo(to);
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public User getTo() {
    return to;
  }

  public void setTo(User to) {
    this.to = to;
  }

  public Multipart getContent() {   
    return content;
  }

  public void setContent(Multipart content) {
    this.content = content;
  }

  //

}
