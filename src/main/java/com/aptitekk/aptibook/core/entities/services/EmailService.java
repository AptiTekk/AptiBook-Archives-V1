/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.entities.services;


import com.aptitekk.aptibook.core.entities.Notification;
import com.aptitekk.aptibook.core.util.LogManager;
import com.sparkpost.Client;
import com.sparkpost.exception.SparkPostException;
import com.sparkpost.model.AddressAttributes;
import com.sparkpost.model.RecipientAttributes;
import com.sparkpost.model.TemplateContentAttributes;
import com.sparkpost.model.TransmissionWithRecipientArray;
import com.sparkpost.model.responses.Response;
import com.sparkpost.resources.ResourceTransmissions;
import com.sparkpost.transport.RestConnection;


import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;


@Stateless
public class EmailService implements Serializable {

    //private Properties mailSessionProps = new Properties();
    String API_KEY = "0bdd338ba5de5083cef4c6908eb6719e0c20caae";
    Client client = new Client(API_KEY);
    String endPoint = "https://api.sparkpost.com/api/v1";

    @Inject
    private PropertiesService propertiesService;

    @PostConstruct
    private void init() {
    }


    public void sendEmailNotification(Notification notification) throws SparkPostException {
        if (notification.getUser() == null || notification.getUser().getEmail() == null || notification.getUser().getEmail().isEmpty())
            return;
        String[] recipients = new String[1];
        recipients[0] = java.net.IDN.toASCII(notification.getUser().getEmail());
        String from = "noreply@aptibook.aptitekk.com";
        String subject = notification.getSubject();
        String body = notification.getBody();
        String templateId = "notification";
        sendEmail(from, recipients, subject, body, templateId);
    }


    private void sendEmail(String from, String[] recipients, String subject, String body, String templateId) throws SparkPostException {
        TransmissionWithRecipientArray transmission = new TransmissionWithRecipientArray();

        // Populate Recipients
        List<RecipientAttributes> recipientArray = new ArrayList<RecipientAttributes>();
        for (String recipient : recipients) {
            RecipientAttributes recipientAttribs = new RecipientAttributes();
            recipientAttribs.setAddress(new AddressAttributes(recipient));
            recipientArray.add(recipientAttribs);
        }
        transmission.setRecipientArray(recipientArray);

        // Populate Substitution Data
        Map<String, Object> substitutionData = new HashMap<String, Object>();
        substitutionData.put("yourContent", "You can add substitution data too.");
        transmission.setSubstitutionData(substitutionData);

        // Populate Email Body, Will Change if Using a Template
        TemplateContentAttributes contentAttributes = new TemplateContentAttributes();
        contentAttributes.setFrom(new AddressAttributes(from));
        contentAttributes.setSubject(subject);
        contentAttributes.setText(body);
        contentAttributes.setHtml("<p>Your <b>HTML</b> content here.  {{yourContent}}</p>");
        transmission.setContentAttributes(contentAttributes);

        //Set Template if Template id is Passed in
        if (templateId != null) {
            TemplateContentAttributes template = new TemplateContentAttributes();
            template.setUseDraftTemplate(false); //Set to true if you want to use draft.
            template.setTemplateId(templateId);
            transmission.setContentAttributes(template);
        }
        // Send the Email
        RestConnection connection = new RestConnection(client, getEndPoint());
        Response response = ResourceTransmissions.create(connection, 0, transmission);

        LogManager.logDebug("Transmission Response: " + response);
    }

    public String getEndPoint() {
        return endPoint;
    }
}
