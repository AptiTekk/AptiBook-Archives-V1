package com.cintriq.agenda.core.utilities;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.cintriq.agenda.core.Properties;
import com.cintriq.agenda.core.entity.AppProperty;
import com.cintriq.agenda.core.entity.Notification;
import com.cintriq.agenda.core.entity.User;
import com.cintriq.agenda.core.utilities.notification.EmailNotification;
import com.cintriq.agenda.core.utilities.notification.VariableInjection;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

public class NotificationFactory {

    public static final String NOTIFICATION_TEMPLATE_KEY_PREFIX = "agenda.notifications.email.template.";

    public static class EmailNotificationBuilder extends NotificationBuilder<EmailNotification> {

        private User to;

        private String subject;

        private String template;

        private String html;

        private String body;

        public EmailNotificationBuilder() {
        }

        public User getTo() {
            return to;
        }

        public EmailNotificationBuilder setTo(User to) {
            this.to = to;
            return this;
        }

        public String getSubject() {
            return subject;
        }

        public EmailNotificationBuilder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public String getHtml() {
            return html;
        }

        public EmailNotificationBuilder setHtml(String html) {
            this.html = html;
            return this;
        }

        public EmailNotificationBuilder loadTemplate(Properties properties) {
            if (properties.get("agenda.notifications.email.template") == null) {
                try {
                    InputStream inputStream = NotificationFactory.class.getResourceAsStream("/WEB-INF/templates/notifications/email.html");
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(inputStream, writer, "UTF-8");
                    properties.insert(new AppProperty("agenda.notifications.email.template", writer.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return this;
                }
            }
            setTemplate(properties.get("agenda.notifications.email.template"));
            return this;
        }

        @Override
        public EmailNotification build(Object... dependencies)
                throws MessagingException, NoSuchMethodException, SecurityException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
            EmailNotification notif = new EmailNotification(to);
            notif.setSubject(subject);
            notif.setTo(to);

            Map<String, Object> depMap = VariableInjection.mapDependencies(dependencies);

            MimeMultipart multipart = new MimeMultipart("related");

            setHtml(template);
            setHtml(fillEmailParameters(multipart, getHtml(), getSubject(), getBody(), "", depMap));
            if (depMap != null) {
                setHtml(VariableInjection.fillParameters(getHtml(), depMap));
            }

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(html, "text/html");
            multipart.addBodyPart(messageBodyPart);

            notif.setContent(multipart);

            return notif;
        }

        public String getTemplate() {
            return template;
        }

        public EmailNotificationBuilder setTemplate(String template) {
            this.template = template;
            return this;
        }

        public String getBody() {
            return body;
        }

        public EmailNotificationBuilder setBody(String body) {
            this.body = body;
            return this;
        }

        /**
         * Similar to VariableInjection fillParameters, adds custom parameters
         * like {headerImage} and {body}
         *
         * @see
         * VariableInjection#fillParameters(String,
         * Map)
         * @param multipart
         * @param input
         * @param subject
         * @param body
         * @param imageLocation
         * @param depMap
         * @return parsedInput
         * @throws NoSuchMethodException
         * @throws SecurityException
         * @throws IllegalAccessException
         * @throws IllegalArgumentException
         * @throws InvocationTargetException
         * @throws MessagingException
         */
        public static String fillEmailParameters(MimeMultipart multipart, String input, String subject, String body,
                String imageLocation, Map<String, Object> depMap)
                throws NoSuchMethodException, SecurityException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException, MessagingException {
            Matcher m = Pattern.compile("\\{([^\\}]+)\\}").matcher(input);
            while (m.find()) {
                String parameter = m.group(1);
                if (parameter.equals("headerImage")) {
                    input = input.replace("{" + parameter + "}", "<img src=\"cid:headerImage\">");
                    // attach image
                    BodyPart messageBodyPart = new MimeBodyPart();;
                    DataSource fds = new FileDataSource(imageLocation); // TODO pull header image
                    messageBodyPart.setDataHandler(new DataHandler(fds));
                    messageBodyPart.setHeader("Content-ID", "<headerImage>");
                    // add image to the multipart
                    multipart.addBodyPart(messageBodyPart);
                } else if (parameter.equals("body")) {
                    input
                            = input.replace("{" + parameter + "}", VariableInjection.fillParameters(body, depMap));
                } else if (parameter.equals("subject")) {
                    input
                            = input.replace("{" + parameter + "}", subject);
                }
            }
            return input;
        }

    }

    public static class DefaultNotificationBuilder extends NotificationBuilder<Notification> {

        @Override
        public Notification build(Object... dependencies) throws MessagingException, 
                NoSuchMethodException, SecurityException, IllegalAccessException, 
                IllegalArgumentException, InvocationTargetException {
            Map<String, Object> depMap = VariableInjection.mapDependencies(dependencies);
            
            Notification notification = new Notification();
            notification.setUser(to);
            
            notification.setSubject(VariableInjection.fillParameters(subject, depMap));
            notification.setBody(VariableInjection.fillParameters(body, depMap));
            
            return notification;
        }

    }

    public static abstract class NotificationBuilder<T> {

        User to;
        String subject;
        String body;

        public User getTo() {
            return to;
        }

        public <A extends NotificationBuilder> A setTo(User to) {
            this.to = to;
            return (A) this;
        }

        public String getSubject() {
            return subject;
        }

        public <A extends NotificationBuilder> A setSubject(String subject) {
            this.subject = subject;
            return (A) this;
        }

        public String getBody() {
            return body;
        }

        public <A extends NotificationBuilder> A setBody(String body) {
            this.body = body;
            return (A) this;
        }

        public abstract T build(Object... dependencies) throws MessagingException,
                NoSuchMethodException, SecurityException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException;
    }

    public static EmailNotificationBuilder createEmailNotificationBuilder() {
        return new EmailNotificationBuilder();
    }
    
    public static DefaultNotificationBuilder createDefaultNotificationBuilder() {
        return new DefaultNotificationBuilder();
    }

    public static EmailNotification convert(Notification notification)
            throws MessagingException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        return createEmailNotificationBuilder()
                .setTo(notification.getUser())
                .setSubject(notification.getSubject())
                .setBody(notification.getBody())
                .build(null);

    }

}
