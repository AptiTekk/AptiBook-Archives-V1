/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.entities;


import com.aptitekk.aptibook.core.domain.propertyGroupChangeListeners.DateTimeChangeListener;
import com.aptitekk.aptibook.core.util.EqualsHelper;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.web.components.propertyTypes.BooleanField;
import com.aptitekk.aptibook.web.components.propertyTypes.SingleLineField;
import com.aptitekk.aptibook.web.components.propertyTypes.abstractTypes.PropertyType;
import org.joda.time.DateTimeZone;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class Property extends MultiTenantEntity implements Serializable {

    public enum Group {

        GOOGLE_SIGN_IN("Google Sign In", null),
        DATE_TIME("Date And Time", DateTimeChangeListener.class);

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

        GOOGLE_SIGN_IN_ENABLED("false", Group.GOOGLE_SIGN_IN, new BooleanField("Enable Google Sign In")),
        GOOGLE_SIGN_IN_WHITELIST("gmail.com, example.com", Group.GOOGLE_SIGN_IN, new SingleLineField("Allowed Domain Names (Comma separated)", 256)),

        DATE_TIME_TIMEZONE("UTC", Group.DATE_TIME, new SingleLineField("Timezone", 4), (key, submittedValue) -> {
            try {
                DateTimeZone dateTimeZone = DateTimeZone.forID(submittedValue);
                if (dateTimeZone == null)
                    throw new Exception("Timezone not found");
            } catch (Exception e) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "The specified timezone was invalid."));
            }
        });

        private final String defaultValue;
        private final Group group;
        private PropertyType propertyType;
        private final PropertyValidator validator;

        Key(String defaultValue, Group group, PropertyType propertyType) {
            this(defaultValue, group, propertyType, null);
        }

        Key(String defaultValue, Group group, PropertyType propertyType, PropertyValidator validator) {
            this.defaultValue = defaultValue;
            this.group = group;
            this.propertyType = propertyType;
            this.validator = validator;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public Group getGroup() {
            return group;
        }

        public PropertyType getPropertyType() {
            return propertyType;
        }

        public PropertyValidator getValidator() {
            return validator;
        }
    }

    public interface PropertyValidator {
        void validate(Key key, String submittedValue) throws ValidatorException;
    }

    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    private Key propertyKey;

    private String propertyValue;

    private static final long serialVersionUID = 1L;

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
