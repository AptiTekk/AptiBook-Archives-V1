/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities;

import com.aptitekk.agenda.core.entities.propertyChangeListeners.EmailPropertyChangeListener;
import com.aptitekk.agenda.core.entities.util.MultiTenantEntity;
import com.aptitekk.agenda.core.util.EqualsHelper;
import com.aptitekk.agenda.core.util.LogManager;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class Property extends MultiTenantEntity implements Serializable {

    public enum Group {

        FRONT_PAGE("Front Page", null),
        EMAIL_SETTINGS("Email Settings", EmailPropertyChangeListener.class);

        private String friendlyName;
        private Class<? extends ChangeListener> propertyGroupChangeListenerClass;

        Group(String friendlyName, Class<? extends ChangeListener> propertyGroupChangeListenerClass) {
            this.friendlyName = friendlyName;
            this.propertyGroupChangeListenerClass = propertyGroupChangeListenerClass;
        }

        public String getFriendlyName() {
            return friendlyName;
        }

        public void firePropertiesChangedEvent() {
            if (propertyGroupChangeListenerClass != null) {
                try {
                    BeanManager beanManager = CDI.current().getBeanManager();
                    Set<Bean<?>> beanSet = beanManager.getBeans(ChangeListener.class);
                    Bean<?> bean = null;
                    for (Bean<?> beanInSet : beanSet) {
                        if (beanInSet.getBeanClass().equals(propertyGroupChangeListenerClass)) {
                            bean = beanInSet;
                            break;
                        }
                    }

                    if (bean != null) {
                        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
                        ChangeListener listener = (ChangeListener) beanManager.getReference(bean, ChangeListener.class, creationalContext);
                        if (listener != null)
                            listener.onPropertiesChanged(this);
                    } else {
                        LogManager.logError("Tried to access listener for " + propertyGroupChangeListenerClass.getName() + " but could not find it in the bean set.");
                    }
                } catch (UnsatisfiedResolutionException e) {
                    LogManager.logError("Tried to access listener for " + propertyGroupChangeListenerClass.getName() + " but could not find it." +
                            "\nError: " + e.getMessage());
                }
            }
        }

        public interface ChangeListener {
            void onPropertiesChanged(Group propertyGroup);
        }
    }

    public enum Key {

        POLICY_BOX("Policy Box Content",
                "Default Policies Message.",
                Group.FRONT_PAGE, 3, false, 256, null, null),

        EMAIL_AUTH("Email Authentication", "true", Group.EMAIL_SETTINGS, 0, false, 5, "^true|false$", "Please enter true or false."),
        EMAIL_STARTTLS("Start TLS enable", "true", Group.EMAIL_SETTINGS, 0, false, 5, "^true|false$", "Please enter true of false"),
        SMTP_HOST("SMTP Host", "smtp.gmail.com", Group.EMAIL_SETTINGS, 0, false, 20, null, ""),
        EMAIL_USERNAME("Username", "Username", Group.EMAIL_SETTINGS, 0, false, 20, null, ""),
        EMAIL_PASSWORD("Password", "Password", Group.EMAIL_SETTINGS, 0, true, 40, null, ""),
        SMTP_PORT("Post number", "587", Group.EMAIL_SETTINGS, 0, false, 5,  "[0-9]+", "Only numbers are allowed"),
        EMAIL_CONNECTIONTIMEOUT("Connection time out(milliseconds)", "5000", Group.EMAIL_SETTINGS, 0, false, 8,"[0-9]+", "Only numbers are allowed"),
        SMTP_TIMEOUT("SMTP timeout", "5000", Group.EMAIL_SETTINGS, 0, false,8,"[0-9]+", "Only numbers are allowed" );

        private final String friendlyName;
        private final String defaultValue;
        private final Group group;
        private int rows;
        private boolean secret;
        private final int maxLength;
        private final String regex;
        private final String regexMessage;

        Key(String friendlyName, String defaultValue, Group group, int rows, boolean secret, int maxLength, String regex, String regexMessage) {
            this.friendlyName = friendlyName;
            this.defaultValue = defaultValue;
            this.group = group;
            this.rows = rows;
            this.secret = secret;
            this.maxLength = maxLength;
            this.regex = regex;
            this.regexMessage = regexMessage;
        }

        public String getFriendlyName() {
            return friendlyName;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public Group getGroup() {
            return group;
        }

        public int getRows() {
            return rows;
        }

        public boolean isSecret() {
            return secret;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public String getRegex() {
            return regex;
        }

        public String getRegexMessage() {
            return regexMessage;
        }
    }

    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    private Key propertyKey;

    private String propertyValue;

    private static final long serialVersionUID = 1L;

    public Property() {
    }

    public Property(Key propertyKey, String propertyValue) {
        setPropertyKey(propertyKey);
        setPropertyValue(propertyValue);
    }

    public Key getPropertyKey() {
        return this.propertyKey;
    }

    public void setPropertyKey(Key key) {
        this.propertyKey = key;
    }

    public String getPropertyValue() {
        return this.propertyValue;
    }

    public void setPropertyValue(String value) {
        this.propertyValue = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Property)) return false;

        Property other = (Property) o;

        return EqualsHelper.areEquals(getPropertyKey(), other.getPropertyKey())
                && EqualsHelper.areEquals(getPropertyValue(), other.getPropertyValue());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getPropertyKey(), getPropertyValue());
    }
}
