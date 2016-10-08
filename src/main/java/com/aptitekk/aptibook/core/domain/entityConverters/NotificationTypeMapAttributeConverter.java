/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.entityConverters;

import com.aptitekk.aptibook.core.domain.entities.Notification;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class converts the NotificationTypeMap (which contains user preferences for email notifications) to and from a database column.
 */
@Converter(autoApply = true)
public class NotificationTypeMapAttributeConverter implements AttributeConverter<Map<Notification.Type, Boolean>, String> {

    @Override
    public String convertToDatabaseColumn(Map<Notification.Type, Boolean> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Notification.Type, Boolean> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                stringBuilder.append(entry.getKey().name());
                stringBuilder.append(":");
                stringBuilder.append(entry.getValue());
                stringBuilder.append(";");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public Map<Notification.Type, Boolean> convertToEntityAttribute(String serialized) {
        Map<Notification.Type, Boolean> map = new HashMap<>();
        if (serialized != null) {
            String[] keyValuePairs = serialized.split(";");
            for (String keyValue : keyValuePairs) {
                String[] split = keyValue.split(":");

                try {
                    Notification.Type type = Notification.Type.valueOf(split[0]);
                    map.put(type, Boolean.parseBoolean(split[1]));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        //Load default values for types that haven't been set.
        for (Notification.Type type : Notification.Type.values()) {
            map.putIfAbsent(type, type.getDefaultValue());
        }
        return map;
    }
}
