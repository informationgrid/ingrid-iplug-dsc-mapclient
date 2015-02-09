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
/**
 * 
 */
package de.ingrid.iplug.dscmapclient.record.mapper;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.ingrid.iplug.dsc.om.SourceRecord;
import de.ingrid.iplug.dsc.record.mapper.IIdfMapper;

/**
 * This class used to create an Idf-skeleton, now it only serves to pass
 * the idf-document stored in the lucene index to the given document.
 * 
 * @author joachim@wemove.com
 * 
 */
public class CreateWmsIdfMapper implements IIdfMapper {

	protected static final Logger log = Logger
			.getLogger(CreateWmsIdfMapper.class);
	protected DocumentBuilderFactory dbFactory;
	
	/**
	 *
	 * take the doc and add another document to it,
	 * since if we parse it directly into the doc, we loose 
	 * the scope of the doc
	 * 
	 */
	@Override
	public void map(SourceRecord record, Document doc) {
		String idf = (String) record.get("id");
		dbFactory = DocumentBuilderFactory.newInstance();
		Document document;
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(new InputSource(new StringReader(idf)));

			Node n = document.getDocumentElement();
			doc.appendChild(doc.adoptNode(n.cloneNode(true)));

		} catch (SAXException e) {
			log.error("Xml error.", e);
			e.printStackTrace();
		} catch (IOException e) {
			log.error("IO error.", e);
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			log.error("Parse error.", e);
			e.printStackTrace();
		}
	}

}
