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

    //private static final String API_KEY = System.getenv("SPARKPOST_API_KEY");
    // private static final String API_URL = System.getenv("SPARKPOST_API_URL");


    private static final String API_KEY = "0bdd338ba5de5083cef4c6908eb6719e0c20caae";
    private static final String API_URL = "https://api.sparkpost.com/api/v1";
    private Client client;

    @PostConstruct
    private void init() {
        if (API_KEY != null && API_URL != null) {
            client = new Client(API_KEY);
        } else {
            LogManager.logError("Could not create EmailService Client. API_KEY or API_URL is null!");
            LogManager.logError("API_KEY: " + API_KEY + " | API_URL: " + API_URL);
        }
    }


    public void sendEmailNotification(Notification notification) throws SparkPostException {
        if (notification.getUser() == null || notification.getUser().getUsername() == null || notification.getUser().getUsername().isEmpty() || !notification.getUser().getWantsEmailNotifications())
            return;
        Map<String, Object> substitutionData = new HashMap<>();
        substitutionData.put("subject", notification.getSubject());
        substitutionData.put("body", notification.getBody());
        sendEmail("notification", substitutionData, java.net.IDN.toASCII(notification.getUser().getUsername()));
    }

    /**
     * Sends an email to the specified recipients with SparkPost using the specified template ID, substitution data.
     *
     * @param templateId       The ID of the template from which the email will derive. Can be found in the SparkPost control panel.
     * @param substitutionData The substitution data, as required by the template.
     * @param recipients       The recipients to send the email to. More than one may be specified.
     * @throws SparkPostException If a problem occurs while sending the emails.
     */
    public void sendEmail(String templateId, Map<String, Object> substitutionData, String... recipients) throws SparkPostException {
        if (client == null)
            return;

        if (templateId == null || templateId.isEmpty() || recipients == null)
            return;
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
            String email = recipient;
        }
        transmission.setRecipientArray(recipientArray);

        // Populate Email Body
       /* if(subject != null && email != null) {
            TemplateContentAttributes contentAttributes = new TemplateContentAttributes();
            contentAttributes.setFrom(new AddressAttributes("noreply@aptitekk.com"));
            contentAttributes.setSubject(subject);
            contentAttributes.setText(email);
            contentAttributes.setHtml(email);
            transmission.setContentAttributes(contentAttributes);
        }*/

        // Send the Email
        RestConnection connection = new RestConnection(client, API_URL);
        ResourceTransmissions.create(connection, 0, transmission);
    }

}
