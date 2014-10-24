/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.dsc;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tngtech.configbuilder.ConfigBuilder;

import de.ingrid.admin.JettyStarter;
import de.ingrid.admin.search.IngridIndexSearcher;
import de.ingrid.iplug.IPlugdescriptionFieldFilter;
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

	public static Configuration conf;
	
	@Autowired
    public DscMapclientSearchPlug(final IngridIndexSearcher indexSearcher,
            IPlugdescriptionFieldFilter[] fieldFilters,
            IMetadataInjector[] injector, IPreProcessor[] preProcessors,
            IPostProcessor[] postProcessors) throws IOException {
		super(indexSearcher, fieldFilters, injector, preProcessors, postProcessors);
    }
	
	public static void main(String[] args) throws Exception {
        conf = new ConfigBuilder<Configuration>(Configuration.class).withCommandLineArgs(args).build();
        new JettyStarter( conf );
    }
}
