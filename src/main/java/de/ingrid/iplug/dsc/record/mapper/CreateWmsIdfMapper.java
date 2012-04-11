/**
 * 
 */
package de.ingrid.iplug.dsc.record.mapper;

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
