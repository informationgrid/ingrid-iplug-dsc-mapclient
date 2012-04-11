package de.ingrid.iplug.dsc.webapp.validation;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import de.ingrid.admin.validation.AbstractValidator;
import de.ingrid.iplug.dsc.index.data.WmsFormModel;

/**
 * Validator for Wms connection dialog.
 * 
 * 
 * @author ma@wemove.com
 *
 */
@Service
public class WmsFormModelValidator extends
        AbstractValidator<WmsFormModel> {

    public final Errors validateWmsParams(final BindingResult errors) {
    	rejectIfEmptyOrWhitespace(errors, "xmlFilePath");
        return errors;
    }
}
