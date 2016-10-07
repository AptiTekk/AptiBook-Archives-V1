/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.services;


import com.aptitekk.aptibook.core.domain.entities.Notification;
import com.aptitekk.aptibook.core.util.LogManager;
import com.sparkpost.Client;
import com.sparkpost.exception.SparkPostException;
import com.sparkpost.model.AddressAttributes;
import com.sparkpost.model.RecipientAttributes;
import com.sparkpost.model.TemplateContentAttributes;
import com.sparkpost.model.TransmissionWithRecipientArray;
import com.sparkpost.resources.ResourceTransmissions;
import com.sparkpost.transport.RestConnection;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Stateless
public class EmailService implements Serializable {

    private static final String API_KEY = System.getenv("SPARKPOST_API_KEY");
    private static final String API_URL = System.getenv("SPARKPOST_API_URL");

    private Client client;

    @PostConstruct
    private void init() {
        if (API_KEY != null && API_URL != null) {
            client = new Client(API_KEY);
        } else {
            LogManager.logError(getClass(), "Could not create EmailService Client. API_KEY or API_URL is null!");
            LogManager.logError(getClass(), "API_KEY: " + API_KEY + " | API_URL: " + API_URL);
        }
    }


    public boolean sendEmailNotification(Notification notification) {
        if (notification.getUser() == null || notification.getUser().getEmailAddress() == null || notification.getUser().getEmailAddress().isEmpty())
            return false;

        return sendEmailNotification(notification.getUser().getEmailAddress(), notification.getSubject(), notification.getBody());
    }

    public boolean sendEmailNotification(String emailAddress, String subject, String body) {
        if (emailAddress == null || subject == null || body == null || emailAddress.isEmpty() || subject.isEmpty() || body.isEmpty())
            return false;

        Map<String, Object> substitutionData = new HashMap<>();
        substitutionData.put("subject", subject);
        substitutionData.put("body", body);
        return sendEmail("notification", substitutionData, java.net.IDN.toASCII(emailAddress));
    }

    /**
     * Sends an email to the specified recipients with SparkPost using the specified template ID, substitution data, and recipients.
     *
     * @param templateId       The ID of the template from which the email will derive. Can be found in the SparkPost control panel.
     * @param substitutionData The substitution data, as required by the template.
     * @param recipients       The recipients (email addresses) to send the email to. More than one may be specified.
     * @return true if the email was sent, false otherwise.
     */
    private boolean sendEmail(String templateId, Map<String, Object> substitutionData, String... recipients) {
        if (client == null)
            return false;

        if (templateId == null || templateId.isEmpty() || recipients == null)
            return false;
        TransmissionWithRecipientArray transmission = new TransmissionWithRecipientArray();

        // Set Template ID
        TemplateContentAttributes template = new TemplateContentAttributes();
        template.setUseDraftTemplate(false);
        template.setTemplateId(templateId);
        transmission.setContentAttributes(template);

        // Set Substitution Data
        transmission.setSubstitutionData(substitutionData);

        // Set Recipients
        List<RecipientAttributes> recipientArray = new ArrayList<>();
        for (String recipient : recipients) {
            RecipientAttributes recipientAttribs = new RecipientAttributes();
            recipientAttribs.setAddress(new AddressAttributes(recipient));
            recipientArray.add(recipientAttribs);
        }
        transmission.setRecipientArray(recipientArray);

        // Send the Email
        RestConnection connection;
        try {
            connection = new RestConnection(client, API_URL);
            ResourceTransmissions.create(connection, 0, transmission);
            return true;
        } catch (SparkPostException e) {
            LogManager.logException(getClass(), "Unable to send an email to these addresses: "+recipientArray+".", e);
            return false;
        }

    }

}
