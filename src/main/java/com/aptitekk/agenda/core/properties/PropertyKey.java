/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.properties;

public enum PropertyKey {

    TEST_KEY_1("Test Key 1", "default", PropertyGroup.TEST_GROUP_1, 1, false, 64, null, null),
    TEST_KEY_2("Test Key 2", "default", PropertyGroup.TEST_GROUP_1, 1, false, 64, null, null),
    TEST_KEY_3("Test Key 3", "default", PropertyGroup.TEST_GROUP_1, 1, false, 64, null, null),
    TEST_KEY_4("Test Key 4", "default", PropertyGroup.TEST_GROUP_2, 1, false, 64, null, null),
    TEST_KEY_5("Test Key 5", "default", PropertyGroup.TEST_GROUP_2, 1, false, 64, null, null),
    TEST_KEY_6("Test Key 6", "default", PropertyGroup.TEST_GROUP_2, 1, false, 64, null, null);

    private final String friendlyName;
    private final String defaultValue;
    private final PropertyGroup group;
    private int rows;
    private boolean secret;
    private final int maxLength;
    private final String regex;
    private final String regexMessage;

    PropertyKey(String defaultValue, String friendlyName, PropertyGroup group, int rows, boolean secret, int maxLength, String regex, String regexMessage) {
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

    public PropertyGroup getGroup() {
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
