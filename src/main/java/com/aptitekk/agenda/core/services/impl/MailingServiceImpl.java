/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.services.MailingService;
import com.aptitekk.agenda.core.utilities.notification.EmailNotification;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Serializable;

@Stateless
public class MailingServiceImpl implements MailingService, Serializable {

    @Resource(name = "java:jboss/Resource/AgendaMail")
    private Session mailSession;

    @Override
    public boolean send(EmailNotification email) {
        try {

            Message message = new MimeMessage(mailSession);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getTo().getEmail()));
            message.setSubject(email.getSubject());
            message.setContent(email.getContent());

            Transport.send(message);

            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }

    }

}
