package de.ingrid.iplug.dsc;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;

import de.ingrid.admin.IConfig;
import de.ingrid.admin.command.PlugdescriptionCommandObject;
import de.ingrid.utils.PlugDescription;

@PropertiesFiles( {"config"} )
@PropertyLocations(directories = {"conf"}, fromClassLoader = true)
public class Configuration implements IConfig {
    
    private static Log log = LogFactory.getLog(Configuration.class);
    
    @PropertyValue("plugdescription.fields")
    public String fields;
    
    @PropertyValue("plugdescription.isRecordLoader")
    @DefaultValue("false")
    public boolean recordLoader;
    
    @PropertyValue("plugdescription.ranking")
    public String rankings;
    
    @PropertyValue("plugdescription.WebmapXmlConfigFile")
    @DefaultValue("")
    public String webmapclientConfig;
    
    @Override
    public void initialize() {
    }

    @Override
    public void addPlugdescriptionValues( PlugdescriptionCommandObject pdObject ) {
        pdObject.put( "iPlugClass", "de.ingrid.iplug.dsc.DscMapclientSearchPlug");
        
        // add necessary fields so iBus actually will query us
        // remove field first to prevent multiple equal entries
        pdObject.removeFromList(PlugDescription.FIELDS, "incl_meta");
        pdObject.addField("incl_meta");
        pdObject.removeFromList(PlugDescription.FIELDS, "t01_object.obj_class");
        pdObject.addField("t01_object.obj_class");
        pdObject.removeFromList(PlugDescription.FIELDS, "metaclass");
        pdObject.addField("metaclass");
        
        pdObject.setRecordLoader(recordLoader);
        
        if(pdObject.getRankingTypes().length == 0){
        	if(rankings != null){
        		String[] rankingList = rankings.split(",");
        		boolean score = true; // here it's always score
				boolean date = false;
				boolean notRanked = false;
				for(String ranking : rankingList){
        			if(ranking.equals("score")){
        				score = true;
        			}else if (ranking.equals("date")) {
        				date = true;
					}else if (ranking.equals("notRanked")) {
						notRanked = true;
					}
        		}
				pdObject.setRankinTypes(score, date, notRanked);
        	}
    	}
        
        pdObject.put("WebmapXmlConfigFile", webmapclientConfig);
    }

    @Override
    public void setPropertiesFromPlugdescription( Properties props, PlugdescriptionCommandObject pd ) {
    	if(pd.get("WebmapXmlConfigFile") != null){
        	props.setProperty("plugdescription.WebmapXmlConfigFile", (String) pd.get("WebmapXmlConfigFile"));
    	}
    }
}
