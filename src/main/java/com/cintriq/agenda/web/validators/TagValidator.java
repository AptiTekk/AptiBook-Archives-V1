package com.cintriq.agenda.web.validators;

import com.cintriq.agenda.core.entity.Tag;
import org.primefaces.component.autocomplete.AutoComplete;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "TagValidator")
@RequestScoped
public class TagValidator implements Validator {

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
        if (uiComponent instanceof AutoComplete) {
            if (((AutoComplete) uiComponent).getSubmittedValue() instanceof String[]) {
                String[] values = (String[]) ((AutoComplete) uiComponent).getSubmittedValue();

                //If the last value is the same as the value before it (eliminate duplicate)
                if (values.length > 1 && values[values.length - 1].equals(values[values.length - 2])) {

                    //Then truncate the array to remove the duplicate.
                    String[] truncatedValues = new String[values.length-1];

                    System.arraycopy(values, 0, truncatedValues, 0, values.length - 1);
                    ((AutoComplete) uiComponent).setSubmittedValue(truncatedValues);
                }
            }
        }

    }
}
