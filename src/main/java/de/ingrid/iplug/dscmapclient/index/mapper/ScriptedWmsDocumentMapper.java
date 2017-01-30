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
/**
 * 
 */
package de.ingrid.iplug.dscmapclient.index.mapper;

import java.io.InputStreamReader;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import de.ingrid.iplug.dsc.index.mapper.ScriptedDocumentMapper;
import de.ingrid.iplug.dsc.om.SourceRecord;
import de.ingrid.iplug.dscmapclient.utils.CapabilitiesUtils;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.index.IndexUtils;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * Script based source record to lucene document mapping. This class takes a
 * {@link Resource} as parameter to specify the mapping script. The scripting
 * engine will be automatically determined from the extension of the mapping
 * script.
 * <p />
 * If the {@link compile} parameter is set to true, the script is compiled, if
 * the ScriptEngine supports compilation.
 * 
 * @author joachim@wemove.com
 * 
 */
public class ScriptedWmsDocumentMapper implements IRecordMapper {

    private Resource mappingScript;

    private boolean compile = false;

    private ScriptEngine engine;
    private CompiledScript compiledScript;

    private static final Logger log = Logger.getLogger(ScriptedDocumentMapper.class);
    private DocumentBuilderFactory dbf = null;
    DocumentBuilder docBuilder = null;
    

    public ScriptedWmsDocumentMapper() {
        dbf = DocumentBuilderFactory.newInstance();
        try {
			docBuilder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.error("Error instantiating class.", e);
           e.printStackTrace();
		}
	}

	@Override
    public ElasticDocument map(SourceRecord record, ElasticDocument doc) throws Exception {
        if (mappingScript == null) {
            log.error("Mapping script is not set!");
            throw new IllegalArgumentException("Mapping script is not set!");
        }
        try {
            if (engine == null) {
                String scriptName = mappingScript.getFilename();
                String extension = scriptName.substring(scriptName
                        .lastIndexOf('.') + 1, scriptName.length());
                ScriptEngineManager mgr = new ScriptEngineManager();
                engine = mgr.getEngineByExtension(extension);
                if (compile) {
                    if (engine instanceof Compilable) {
                        Compilable compilable = (Compilable) engine;
                        compiledScript = compilable
                                .compile(new InputStreamReader(mappingScript
                                        .getInputStream()));
                    }
                }
            }
            
            // create utils for script

            CapabilitiesUtils capabilitiesUtils = new CapabilitiesUtils();
            capabilitiesUtils.setUrlStr((String)record.get(SourceRecord.ID));

            if (log.isDebugEnabled()) {
				log.debug("Requesting " + capabilitiesUtils.getUrlStr());
			}
            org.w3c.dom.Document wmsDoc = capabilitiesUtils.requestCaps();
            // we check for null and return -> NO Exception, so oncoming URLs are processed
            if (wmsDoc == null) {
                log.warn("!!! Problems requesting " + capabilitiesUtils.getUrlStr() + " !!! We return null Document so will not be indexed !");
                return null;
            }
            //we put the xmlDoc(WMS doc) into the record and thereby pass it to the idf-mapper 
            record.put("WmsDoc", wmsDoc);
            
            IndexUtils idxUtils = new IndexUtils(doc);
            XPathUtils xPathUtils = new XPathUtils(new IDFNamespaceContext());
            
            Bindings bindings = engine.createBindings();
            bindings.put("wmsDoc", wmsDoc);
            bindings.put("log", log);
            bindings.put("CAP", capabilitiesUtils);
            bindings.put("IDX", idxUtils);
            bindings.put("XPATH", xPathUtils);
            bindings.put("javaVersion", System.getProperty( "java.version" ));

            if (compiledScript != null) {
                compiledScript.eval(bindings);
            } else {
                engine.eval(new InputStreamReader(mappingScript
                        .getInputStream()), bindings);
            }
            
            return doc;

        } catch (Exception e) {
            log.error("Error mapping source record to lucene document.", e);
            e.printStackTrace();
            throw e;
        }
        
    }

    public Resource getMappingScript() {
        return mappingScript;
    }

    public void setMappingScript(Resource mappingScript) {
        this.mappingScript = mappingScript;
    }

    public boolean isCompile() {
        return compile;
    }

    public void setCompile(boolean compile) {
        this.compile = compile;
    }

}
