/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;


import com.aptitekk.agenda.core.entities.Notification;
import com.aptitekk.agenda.core.entities.Property;
import com.aptitekk.agenda.core.util.LogManager;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;


@Stateless
public class EmailService implements Serializable {

    private Properties mailSessionProps = new Properties();
    private String username;
    private String password;

    @Inject
    private PropertiesService propertiesService;

    @PostConstruct
    private void init() {
        resetProperties();
    }

    public void resetProperties() {
        mailSessionProps.put("mail.smtp.auth", propertiesService.getPropertyByKey(Property.Key.EMAIL_AUTH).getPropertyValue());
        mailSessionProps.put("mail.smtp.starttls.enable", propertiesService.getPropertyByKey(Property.Key.EMAIL_STARTTLS).getPropertyValue());
        mailSessionProps.put("mail.smtp.host", propertiesService.getPropertyByKey(Property.Key.SMTP_HOST).getPropertyValue());
        mailSessionProps.put("mail.smtp.user", (username = propertiesService.getPropertyByKey(Property.Key.EMAIL_USERNAME).getPropertyValue()));
        mailSessionProps.put("mail.smtp.password", (password = propertiesService.getPropertyByKey(Property.Key.EMAIL_PASSWORD).getPropertyValue()));
        mailSessionProps.put("mail.smtp.port", Integer.parseInt(propertiesService.getPropertyByKey(Property.Key.SMTP_PORT).getPropertyValue()));
        mailSessionProps.put("mail.smtp.connectiontimeout", Integer.parseInt(propertiesService.getPropertyByKey(Property.Key.EMAIL_CONNECTIONTIMEOUT).getPropertyValue()));
        mailSessionProps.put("mail.smtp.timeout", Integer.parseInt(propertiesService.getPropertyByKey(Property.Key.SMTP_TIMEOUT).getPropertyValue()));
    }

    public void sendEmailNotification(Notification notification) {
        if (notification.getUser() == null || notification.getUser().getEmail() == null || notification.getUser().getEmail().isEmpty())
            return;

        Session mailSession = Session.getInstance(mailSessionProps, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage mimeMessage = new MimeMessage(mailSession);
            Address from = new InternetAddress(username);
            mimeMessage.setFrom(from);
            Address recipient = new InternetAddress(java.net.IDN.toASCII(notification.getUser().getEmail()));
            mimeMessage.setRecipient(Message.RecipientType.TO, recipient);
            mimeMessage.setSubject(notification.getSubject());
            mimeMessage.setSentDate(new Date());
            BodyPart messageBodyPart = new MimeBodyPart();
            // Fill the message
            messageBodyPart.setContent(notification.getBody(), "text/html");
            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);
            /*Next would be attachement, but not gonna worry about that yet*/
            mimeMessage.setContent(multipart);
            Transport.send(mimeMessage);
            mailSession.getTransport().close(); //technically not needed, because not sending through mailSession object
        } catch (javax.mail.MessagingException e) {
            LogManager.logError("Error sending email");
            e.printStackTrace();
        }
    }

}
