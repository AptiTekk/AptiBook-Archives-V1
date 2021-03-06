/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.util;

import javax.faces.application.FacesMessage;

/**
 * Contains common messages displayed to users that are used throughout the application.
 */
public class CommonFacesMessages {

    public static final String EXCEPTION_MESSAGE = "There was an internal error while fulfilling your request. We apologize for the inconvenience!";
    public static final FacesMessage EXCEPTION_FACES_MESSAGE = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, EXCEPTION_MESSAGE);

    public static final String GOOGLE_SIGN_IN_FAIL_MESSAGE = "Unfortunately, we were unable to Sign In with Google. Please try again later!";
    public static final FacesMessage GOOGLE_SIGN_IN_FAIL_FACES_MESSAGE = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, GOOGLE_SIGN_IN_FAIL_MESSAGE);

}
