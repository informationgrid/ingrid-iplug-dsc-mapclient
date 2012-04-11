/**
 * 
 */
package de.ingrid.iplug.dsc.index.mapper;

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
import org.apache.lucene.document.Document;
import org.springframework.core.io.Resource;

import de.ingrid.iplug.dsc.om.SourceRecord;
import de.ingrid.iplug.dsc.utils.CapabilitiesUtils;
import de.ingrid.iplug.dsc.utils.DOMUtils;
import de.ingrid.iplug.dsc.utils.IndexUtils;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XMLUtils;
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
    public void map(SourceRecord record, Document doc) throws Exception {
    	org.w3c.dom.Document w3cDoc = docBuilder.newDocument();
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
            IndexUtils idxUtils = new IndexUtils(doc);
            org.w3c.dom.Document xmlDoc = capabilitiesUtils.requestCaps(); 
            XPathUtils xPathUtils = new XPathUtils(new IDFNamespaceContext());
            
            DOMUtils domUtils = new DOMUtils(w3cDoc, xPathUtils);
            XMLUtils xmlUtils = new XMLUtils();
            Bindings bindings = engine.createBindings();
            bindings.put("sourceRecord", record);
            bindings.put("luceneDoc", doc);
            bindings.put("xmlDoc", xmlDoc);
            bindings.put("log", log);
            bindings.put("CAP", capabilitiesUtils);
            bindings.put("IDX", idxUtils);
            bindings.put("XPATH", xPathUtils);
            bindings.put("DOM", domUtils);
            bindings.put("w3cDoc", w3cDoc);
            bindings.put("XML", xmlUtils);            

            if (compiledScript != null) {
                compiledScript.eval(bindings);
            } else {
                engine.eval(new InputStreamReader(mappingScript
                        .getInputStream()), bindings);
            }

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
