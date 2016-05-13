/*
 * **************************************************-
 * ingrid-iplug-dsc-mapclient
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.iplug.dscmapclient.webapp.validation;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import de.ingrid.admin.validation.AbstractValidator;
import de.ingrid.iplug.dscmapclient.index.data.WmsFormModel;

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
