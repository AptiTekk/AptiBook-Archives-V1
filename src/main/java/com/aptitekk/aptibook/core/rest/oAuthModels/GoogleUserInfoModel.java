/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.rest.oAuthModels;

public class GoogleUserInfoModel {
    private String given_name;
    private String family_name;
    private String email;
    private String picture;

    public String getGivenName() {
        return given_name;
    }

    public String getFamilyName() {
        return family_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }

    @Override
    public String toString() {
        return given_name + " - " + email;
    }

}
