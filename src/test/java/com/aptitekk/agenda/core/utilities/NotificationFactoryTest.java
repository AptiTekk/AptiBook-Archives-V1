package com.aptitekk.agenda.core.utilities;

import com.aptitekk.agenda.core.entity.Asset;
import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.Reservation;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.testingUtil.TestUtils;
import com.aptitekk.agenda.core.utilities.notification.EmailNotification;
import com.aptitekk.agenda.core.utilities.notification.VariableInjection;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.mail.MessagingException;
import javax.naming.NamingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

@RunWith(Arquillian.class)
public class NotificationFactoryTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return TestUtils.createDeployment(NotificationFactory.class, User.class, EmailNotification.class, Asset.class, Reservation.class, NotificationFactory.EmailNotificationBuilder.class);
    }

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
        item.setAssetType(type);

        Reservation res = new Reservation();
        res.setDateCreated(Calendar.getInstance());
        res.setAsset(item);

        String body =
                "Hello {user.fullname} {reservation.asset.name} {reservation.asset.assetType.id}";
        String html = "<html>{body} {user.email} {user.password} {unparseable}</html>";
        String expectedHtml = html
                .replace("{body}",
                        "Hello " + user.getFullname() + " " + res.getAsset().getName() + " "
                                + res.getAsset().getAssetType().getId())
                .replace("{user.email}", user.getEmail())
                .replace("{user.password}", VariableInjection.OMITTED_PARAM);

        NotificationFactory.EmailNotificationBuilder builder = NotificationFactory.createEmailNotificationBuilder()
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
