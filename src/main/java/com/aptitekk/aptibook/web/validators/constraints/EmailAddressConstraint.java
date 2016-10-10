/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.validators.constraints;

import com.aptitekk.aptibook.web.validators.constraints.annotations.EmailAddress;
import org.apache.commons.validator.routines.EmailValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailAddressConstraint implements ConstraintValidator<EmailAddress, String> {

    @Override
    public void initialize(EmailAddress emailAddress) {

    }

    @Override
    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        return EmailValidator.getInstance().isValid(input);
    }
}
