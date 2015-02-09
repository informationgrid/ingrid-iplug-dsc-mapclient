/*
 * **************************************************-
 * ingrid-iplug-dsc-mapclient
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.dscmapclient;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tngtech.configbuilder.ConfigBuilder;

import de.ingrid.admin.JettyStarter;
import de.ingrid.admin.search.IngridIndexSearcher;
import de.ingrid.iplug.IPlugdescriptionFieldFilter;
import de.ingrid.iplug.dsc.DscSearchPlug;
import de.ingrid.iplug.dsc.record.DscRecordCreator;
import de.ingrid.utils.metadata.IMetadataInjector;
import de.ingrid.utils.processor.IPostProcessor;
import de.ingrid.utils.processor.IPreProcessor;

/**
 * This iPlug connects to the iBus and delivers search results based on a index.
 * Just "redefine" DscSearchPlug from other project.
 * IMPORTANT:
 * Main iPlug class has to be in own project ingrid-iplug-dsc-mapclient, so the metadata
 * (iplug type, version ...) is read from ingrid-iplug-dsc-mapclient jar !
 * In spring.xml DscSearchPlug has to be excluded from bean creation to guarantee only
 * one iPlug class !
 */
@Service
public class DscMapclientSearchPlug extends DscSearchPlug {

	public static ConfigurationDscMapClient conf;
	
	@Autowired
    public DscMapclientSearchPlug(final IngridIndexSearcher indexSearcher,
            IPlugdescriptionFieldFilter[] fieldFilters,
            IMetadataInjector[] injector, IPreProcessor[] preProcessors,
            IPostProcessor[] postProcessors, DscRecordCreator producer) throws IOException {
		super(indexSearcher, fieldFilters, injector, preProcessors, postProcessors, producer);
    }
	
	public static void main(String[] args) throws Exception {
        conf = new ConfigBuilder<ConfigurationDscMapClient>(ConfigurationDscMapClient.class).withCommandLineArgs(args).build();
        new JettyStarter( conf );
    }
}
