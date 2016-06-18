package com.aptitekk.agenda.core.utilities.notification;

import com.aptitekk.agenda.core.entity.User;

import javax.mail.Multipart;

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
