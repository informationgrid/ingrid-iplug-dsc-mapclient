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
/**
 * 
 */
package de.ingrid.iplug.dscmapclient.index.producer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.ingrid.admin.elasticsearch.StatusProvider;
import de.ingrid.iplug.dsc.index.producer.IRecordSetProducer;
import de.ingrid.iplug.dsc.om.SourceRecord;
import de.ingrid.utils.IConfigurable;
import de.ingrid.utils.PlugDescription;

	/**
	 * Takes care of selecting all source record Ids from a xml file. The xml tag is
	 * configurable via Spring. It needs to be an xpath expressions for maximum
	 * flexibility
	 * 
	 * The file path pointing to the xml is configured via the PlugDescription.
	 * 
	 * This class is basically an iterator over the url entries in the webmapclient
	 * config file
	 * 
	 * @author ma@wemove.com
	 * 
	 */
	public class PlugDescriptionConfiguredWmsRecordSetProducer implements
			IRecordSetProducer, IConfigurable {

	String idTag = "";
	Iterator<String> recordIdIterator = null;
	String xmlFilePath = null;
	
    @Autowired
    private StatusProvider statusProvider;

    final private static Log log = LogFactory
			.getLog(PlugDescriptionConfiguredWmsRecordSetProducer.class);

	public PlugDescriptionConfiguredWmsRecordSetProducer() {
		log.info("PlugDescriptionConfiguredXmlRecordSetProducer started.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.iplug.dsc.index.IRecordProducer#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (recordIdIterator == null) {

			createRecordIdsFromXmlFile();
		}
		if (recordIdIterator.hasNext()) {
			return true;
		} else {
			recordIdIterator = null;

			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.iplug.dsc.index.IRecordProducer#next()
	 */
	@Override
	public SourceRecord next() {
		SourceRecord dsr = new SourceRecord(recordIdIterator.next());
		return dsr;
	}

	@Override
	public void configure(PlugDescription plugDescription) {

		this.xmlFilePath = (String) plugDescription.get("WebmapXmlConfigFile");

	}

	public String getIdTag() {
		return idTag;
	}

	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}

	/**
	 * 
	 * create the ids form an xml file invokes private method readXmlFIle takes
	 * the received NodeList, parses it into a list which serves as the iterator
	 * or better iteratedlist
	 * 
	 */
	private void createRecordIdsFromXmlFile() {

		List<String> recordIds = new ArrayList<String>();
		NodeList nl = null;

		nl = readXmlFile(this.xmlFilePath, idTag);

		for (int i = 0; i < nl.getLength(); i++) {
			if(!recordIds.contains(nl.item(i).getTextContent()))
			recordIds.add(nl.item(i).getTextContent());
		}
		
        statusProvider.addState( "FETCH", "Found " + nl.getLength() + " records in mapclient configuration.");

		recordIdIterator = recordIds.listIterator();

	}

	/**
	 * this private method does all the dirty work, read the file, parse it into
	 * a document and find the desired ids, through the xpath expression
	 * 
	 * @param filePath
	 * @param expression
	 * @return NodeList
	 */
	private NodeList readXmlFile(String filePath, String expression) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		File fXmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			return (NodeList) xPath.evaluate(expression, doc,
					XPathConstants.NODESET);
		} catch (ParserConfigurationException e) {
			log.error("Error creating record ids.", e);
			e.printStackTrace();
		} catch (SAXException e) {
			log.error("Error creating record ids.", e);
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Error creating record ids.", e);
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			log.error("Error creating record ids.", e);
			e.printStackTrace();
		}
		return null;

	}

	public String getXmlFilePath() {
		return xmlFilePath;
	}

	public void setXmlFilePath(String xmlFilePath) {
		this.xmlFilePath = xmlFilePath;
	}

    @Override
    public void reset() {}

    @Override
    public int getDocCount() {
        return 0;
    }
    
    public void setStatusProvider(StatusProvider statusProvider) {
        this.statusProvider = statusProvider;
    }
}
