/*
 * **************************************************-
 * ingrid-iplug-dsc-mapclient
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
package de.ingrid.iplug.dscmapclient.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ingrid.admin.command.PlugdescriptionCommandObject;
import de.ingrid.admin.controller.AbstractController;
import de.ingrid.iplug.dscmapclient.DscMapclientSearchPlug;
import de.ingrid.iplug.dscmapclient.index.data.WmsFormModel;
import de.ingrid.iplug.dscmapclient.webapp.validation.WmsFormModelValidator;

/**
 * Control the wms parameter page.
 * 
 * @author ma@wemove.com
 * 
 */
@Controller
@SessionAttributes("plugDescription")
public class WmsFormContainerController extends AbstractController {
    private final WmsFormModelValidator _validator;

    @Autowired
    public WmsFormContainerController(WmsFormModelValidator validator) {
        _validator = validator;
    }

    @RequestMapping(value = { "/iplug-pages/welcome.html",
            "/iplug-pages/wmsConfigs.html" }, method = RequestMethod.GET)
    public String getParameters(
            final ModelMap modelMap,
            @ModelAttribute("plugDescription") final PlugdescriptionCommandObject commandObject) {

        WmsFormModel wmsFormModel = new WmsFormModel();
        wmsFormModel.setXmlFilePath((String)commandObject.get("WebmapXmlConfigFile"));
        // write object into session
        modelMap.addAttribute("wmsConfig", wmsFormModel);
        return AdminViews.WMS_CONFIGS;
    }

    @RequestMapping(value = "/iplug-pages/wmsConfigs.html", method = RequestMethod.POST)
    public String post(
            @ModelAttribute("wmsConfig") final WmsFormModel wmsModel,
            final BindingResult errors,
            @ModelAttribute("plugDescription") final PlugdescriptionCommandObject pdCommandObject) {

        // check if page contains any errors
        if (_validator.validateWmsParams(errors).hasErrors()) {
            return AdminViews.WMS_CONFIGS;
        }

        // put values into plugdescription
        mapParamsToPD(wmsModel, pdCommandObject);

        return AdminViews.SAVE;
    }

    private void mapParamsToPD(WmsFormModel wmsModel,
            PlugdescriptionCommandObject pdCommandObject) {

        // only set in configuration, which will update the plugdescription
        DscMapclientSearchPlug.conf.webmapclientConfig = wmsModel.getXmlFilePath();
        
        // add required datatypes to PD
        //pdCommandObject.addDataType("IDF_1.0");
    }

    public boolean rankSupported(String rankType, String[] types) {
        for (String type : types) {
            if (type.contains(rankType))
                return true;
        }
        return false;
    }

}
