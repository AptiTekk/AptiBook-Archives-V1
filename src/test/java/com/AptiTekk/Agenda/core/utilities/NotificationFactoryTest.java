package com.AptiTekk.Agenda.core.utilities;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.naming.NamingException;

import com.AptiTekk.Agenda.core.entity.AssetType;
import org.junit.Assert;
import org.junit.Test;

import com.AptiTekk.Agenda.core.entity.Asset;
import com.AptiTekk.Agenda.core.entity.Reservation;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.utilities.NotificationFactory.EmailNotificationBuilder;
import com.AptiTekk.Agenda.core.utilities.notification.EmailNotification;
import com.AptiTekk.Agenda.core.utilities.notification.VariableInjection;

public class NotificationFactoryTest {

  @Test
  public void variableInjectionTest()
      throws NamingException, NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, MessagingException {
    User user = new User();
    user.setEmail("kevint@gm-thorne.com");
    user.setFirstName("Mitchell");
    user.setLastName("Talmadge");

    AssetType type = new AssetType();
    type.setId(999);

    Asset item = new Asset();
    item.setName("item name!");
    item.setType(type);

    Reservation res = new Reservation();
    res.setDateCreated(new Date());
    res.setAsset(item);

    String body =
        "Hello {user.fullname} {reservation.asset.name} {reservation.asset.type.id}";
    String html = "<html>{body} {user.email} {user.password} {unparseable}</html>";
    String expectedHtml = html
        .replace("{body}",
            "Hello " + user.getFullname() + " " + res.getAsset().getName() + " "
                + res.getAsset().getType().getId())
        .replace("{user.email}", user.getEmail())
        .replace("{user.password}", VariableInjection.OMITTED_PARAM);

    EmailNotificationBuilder builder = NotificationFactory.createEmailNotificationBuilder()
        .setSubject("Test Subject").setBody(body).setTemplate(html);

    builder.build(user, res);

    Assert.assertEquals("Variable injection failed!",
        builder.getHtml(), expectedHtml);
  }

  @Test
  public void emailNotificationFactoryTest() throws Exception {
    User user = new User();
    user.setEmail("kevint@gm-thorne.com");
    user.setFirstName("Mitchell");
    user.setLastName("Talmadge");

    String body = "Hi there {user.fullname}";
    String template = "<html>{body}</html>";
    String subject = "Subject";

    String expectedHtmlRender =
        template.replace("{body}", body.replace("{user.fullname}", user.getFullname()));

    EmailNotification emailNotification = NotificationFactory.createEmailNotificationBuilder()
        .setTemplate(template).setBody(body).setTo(user).setSubject(subject).build(user);

    Assert.assertEquals("Email recipients didn't match!", user.getEmail(),
        emailNotification.getTo().getEmail());
    Assert.assertEquals("Subjects didn't match!", subject, emailNotification.getSubject());
    Assert.assertEquals("HTML parts didn't match!", expectedHtmlRender,
        emailNotification.getContent().getBodyPart(0).getContent().toString());
  }

}
