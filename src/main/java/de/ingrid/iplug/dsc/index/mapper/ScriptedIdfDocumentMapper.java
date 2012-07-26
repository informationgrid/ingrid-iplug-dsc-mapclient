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
	 * The main difference to the other dsc-iplugs is that we map the idf-document into
	 * the lucene index as xml-string. 
	 * 
	 * @author ma@wemove.com
	 * 
	 */
	public class ScriptedIdfDocumentMapper implements IRecordMapper {

    private Resource mappingScript;

    private boolean compile = false;

    private ScriptEngine engine;
    private CompiledScript compiledScript;
    private DocumentBuilderFactory dbf = null;
    DocumentBuilder docBuilder = null;

    private static final Logger log = Logger.getLogger(ScriptedDocumentMapper.class);

    public ScriptedIdfDocumentMapper(){
         dbf = DocumentBuilderFactory.newInstance();
         try {
			docBuilder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.error("Error instantiating class.", e);
            e.printStackTrace();
		}
    }
    /**
     * map in this case means, map the sourcerecord to 
     * an idf document and store it as xml-string
     */
    public org.apache.lucene.document.Document map(SourceRecord record, org.apache.lucene.document.Document luceneDoc) throws Exception {

    	if (luceneDoc.get("id") == null || luceneDoc.get("serviceUnavailable") != null) {
            log.warn("!!! No 'id' set in index document (id=" + luceneDoc.get("id") +
            		") or 'serviceUnavailable' set (serviceUnavailable=" + luceneDoc.get("serviceUnavailable") +
            		") !!! No IDF possible, we return null Document so will not be indexed !");
        	return null;
    	}

        if (mappingScript == null) {
            log.error("Mapping script is not set!");
            throw new IllegalArgumentException("Mapping script is not set!");
        }

        org.w3c.dom.Document w3cDoc = docBuilder.newDocument();
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



            IndexUtils idxUtils = new IndexUtils(luceneDoc);
            CapabilitiesUtils capUtils = new CapabilitiesUtils();
            XPathUtils xPathUtils = new XPathUtils(new IDFNamespaceContext());
            DOMUtils domUtils = new DOMUtils(w3cDoc, xPathUtils);
            XMLUtils xmlUtils = new XMLUtils();
            
            Bindings bindings = engine.createBindings();
            bindings.put("CAP", capUtils);
            bindings.put("XML", xmlUtils);
            bindings.put("sourceRecord", record);
            bindings.put("luceneDoc", luceneDoc);
            bindings.put("w3cDoc", w3cDoc);
            bindings.put("log", log);
            bindings.put("IDX", idxUtils);
            bindings.put("XPATH", xPathUtils);
            bindings.put("DOM", domUtils);

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

    	return luceneDoc;
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
