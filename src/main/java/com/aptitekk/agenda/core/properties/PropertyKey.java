/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.properties;

public enum PropertyKey {

    POLICY_BOX("Policy Box Content",
            "Here lies a disclaimer. It will be ignored, just like all disclaimers usually are. If you are not ignoring it, you will not follow it. That is the way of life; and that is how we as a society innovate. For innovation shall not be hindered by policies.",
            PropertyGroup.FRONT_PAGE, 3, false, 256, null, null);

    private final String friendlyName;
    private final String defaultValue;
    private final PropertyGroup group;
    private int rows;
    private boolean secret;
    private final int maxLength;
    private final String regex;
    private final String regexMessage;

    PropertyKey(String friendlyName, String defaultValue, PropertyGroup group, int rows, boolean secret, int maxLength, String regex, String regexMessage) {
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
