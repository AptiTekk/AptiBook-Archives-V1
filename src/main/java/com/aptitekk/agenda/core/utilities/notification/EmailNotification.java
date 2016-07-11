/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.utilities.notification;

import com.aptitekk.agenda.core.entities.User;

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
