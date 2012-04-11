package de.ingrid.iplug.dsc.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class does two things basically, firstly it holds the constants, that
 * contain xpath-expressions, which are needed to get the payload out of the
 * wms-responses. And lastly the getCaps() method, which makes the request for
 * the wms-documents the rest are some helper methods to use in the javascript
 * mappings
 * 
 * @author ma
 * 
 */
public class CapabilitiesUtils {

	public final static String XPATH_EXP_WMS_1_1_1_TITLE = "/WMT_MS_Capabilities/Service[1]/Title[1]";
	public final static String XPATH_EXP_WMS_1_1_1_ABSTRACT = "/WMT_MS_Capabilities/Service[1]/Abstract[1]";
	public final static String XPATH_EXP_WMS_1_1_1_VERSION = "/WMT_MS_Capabilities/@version";
	public final static String XPATH_EXP_WMS_1_1_1_OP_GET_CAPABILITIES_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetCapabilities[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	public final static String XPATH_EXP_WMS_1_1_1_OP_GET_MAP_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetMap[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	public final static String XPATH_EXP_WMS_1_1_1_OP_GET_FEATURE_INFO_HREF = "/WMT_MS_Capabilities/Capability[1]/Request[1]/GetFeatureInfo[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	public final static String XPATH_EXP_WMS_1_1_1_CONTACT_PERSON = "/WMT_MS_Capabilities/Service[1]/ContactInformation[1]/ContactPersonPrimary[1]/ContactPerson[1]";
	public final static String XPATH_EXP_WMS_1_1_1_CONTACT_ORGANIZATION = "/WMT_MS_Capabilities/Service[1]/ContactInformation[1]/ContactPersonPrimary[1]/ContactOrganization[1]";
	public final static String XPATH_EXP_WMS_1_1_1_CONTACT_VOICE_TELEPHONE = "/WMT_MS_Capabilities/Service[1]/ContactInformation[1]/ContactVoiceTelephone[1]";
	public final static String XPATH_EXP_WMS_1_1_1_CONTACT_EMAIL_ADDRESS = "/WMT_MS_Capabilities/Service[1]/ContactInformation[1]/ContactElectronicMailAddress[1]";
	public final static String XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_ADDRESS = "/WMT_MS_Capabilities/Service[1]/ContactInformation[1]/ContactAddress[1]/Address[1]";
	public final static String XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_CITY = "/WMT_MS_Capabilities/Service[1]/ContactInformation[1]/ContactAddress[1]/City[1]";
	public final static String XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_PROVINCE = "/WMT_MS_Capabilities/Service[1]/ContactInformation[1]/ContactAddress[1]/StateOrProvince[1]";
	public final static String XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_POSTCODE = "/WMT_MS_Capabilities/Service[1]/ContactInformation[1]/ContactAddress[1]/PostCode[1]";
	public final static String XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_COUNTRY = "/WMT_MS_Capabilities/Service[1]/ContactInformation[1]/ContactAddress[1]/Country[1]";
	public final static String XPATH_EXP_WMS_1_1_1_ACCESS_CONSTRAINTS = "/WMT_MS_Capabilities/Service[1]/AccessConstraints[1]";
	public final static String XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MINX = "/WMT_MS_Capabilities/Capability/Layer/LatLonBoundingBox[1]/@minx";
	public final static String XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MINY = "/WMT_MS_Capabilities/Capability/Layer/LatLonBoundingBox[1]/@miny";
	public final static String XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MAXX = "/WMT_MS_Capabilities/Capability/Layer/LatLonBoundingBox[1]/@maxx";
	public final static String XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MAXY = "/WMT_MS_Capabilities/Capability/Layer/LatLonBoundingBox[1]/@maxy";
	public final static String XPATH_EXP_WMS_1_1_1_KEYWORDS = "/WMT_MS_Capabilities/Service[1]/KeywordList/Keyword";

	public final static String XPATH_EXP_WMS_1_3_0_TITLE = "/WMS_Capabilities/Service[1]/Title[1]";
	public final static String XPATH_EXP_WMS_1_3_0_ABSTRACT = "/WMS_Capabilities/Service[1]/Abstract[1]";
	public final static String XPATH_EXP_WMS_1_3_0_VERSION = "/WMS_Capabilities/@version";
	public final static String XPATH_EXP_WMS_1_3_0_OP_GET_CAPABILITIES_HREF = "/WMS_Capabilities/Capability[1]/Request[1]/GetCapabilities[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	public final static String XPATH_EXP_WMS_1_3_0_OP_GET_MAP_HREF = "/WMS_Capabilities/Capability[1]/Request[1]/GetMap[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	public final static String XPATH_EXP_WMS_1_3_0_OP_GET_FEATURE_INFO_HREF = "/WMS_Capabilities/Capability[1]/Request[1]/GetFeatureInfo[1]/DCPType[1]/HTTP[1]/Get[1]/OnlineResource[1]/@href";
	public final static String XPATH_EXP_WMS_1_3_0_CONTACT_PERSON = "/WMS_Capabilities/Service[1]/ContactInformation[1]/ContactPersonPrimary[1]/ContactPerson[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CONTACT_ORGANIZATION = "/WMS_Capabilities/Service[1]/ContactInformation[1]/ContactPersonPrimary[1]/ContactOrganization[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CONTACT_VOICE_TELEPHONE = "/WMS_Capabilities/Service[1]/ContactInformation[1]/ContactVoiceTelephone[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CONTACT_EMAIL_ADDRESS = "/WMS_Capabilities/Service[1]/ContactInformation[1]/ContactElectronicMailAddress[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_ADDRESS = "/WMS_Capabilities/Service[1]/ContactInformation[1]/ContactAddress[1]/Address[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_CITY = "/WMS_Capabilities/Service[1]/ContactInformation[1]/ContactAddress[1]/City[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_PROVINCE = "/WMS_Capabilities/Service[1]/ContactInformation[1]/ContactAddress[1]/StateOrProvince[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_POSTCODE = "/WMS_Capabilities/Service[1]/ContactInformation[1]/ContactAddress[1]/PostCode[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_COUNTRY = "/WMS_Capabilities/Service[1]/ContactInformation[1]/ContactAddress[1]/Country[1]";
	public final static String XPATH_EXP_WMS_1_3_0_ACCESS_CONSTRAINTS = "/WMS_Capabilities/Service[1]/AccessConstraints[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MINX = "/WMS_Capabilities/Capability/Layer/EX_GeographicBoundingBox[1]/eastBoundLongitude[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MINY = "/WMS_Capabilities/Capability/Layer/EX_GeographicBoundingBox[1]/southBoundLatitude[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MAXX = "/WMS_Capabilities/Capability/Layer/EX_GeographicBoundingBox[1]/westBoundLongitude[1]";
	public final static String XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MAXY = "/WMS_Capabilities/Capability/Layer/EX_GeographicBoundingBox[1]/northBoundLatitude[1]";
	public final static String XPATH_EXP_WMS_1_3_0_KEYWORDS = "/WMS_Capabilities/Service[1]/KeywordList/Keyword";

	private String urlStr;
	private XPath xpath = null;
	final private static Log log = LogFactory.getLog(CapabilitiesUtils.class);

	public CapabilitiesUtils() {
		xpath = XPathFactory.newInstance().newXPath();
	}

	public Document requestCaps() {
		Document doc = null;

		try {

			
			URL url;
			url = new URL(urlStr);
			URLConnection conn;
			conn = url.openConnection();
			conn.setConnectTimeout(5000);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(conn.getInputStream());
			doc.getDocumentElement().normalize();
			// insert the dom into the hashmap
			// in case of exception null entry is written
		} catch (ConnectTimeoutException e) {
			log.error("Url timed out: " + urlStr);
			log.error("Error creating record ids.", e);
			System.out.println("Url timed out: " + urlStr);
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			log.error("Error creating record ids.", e);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			log.error("Error creating record ids.", e);
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			log.error("Error creating record ids.", e);
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			log.error("Error creating record ids.", e);
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			log.error("Error creating record ids.", e);
			e.printStackTrace();
			return null;
		}

		return doc;
	}

	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}

	public XPath getXpath() {
		return xpath;
	}

	public String evaluate(String evalExpression, Document xmlDoc) {
		try {
			return (String) xpath.evaluate(evalExpression, xmlDoc,
					XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			log.error("Error creating record ids.", e);
			e.printStackTrace();
		}
		return "";
	}

	public String[] evaluateList(String evalExpression, Document xmlDoc) {
		try {
			NodeList nl = (NodeList) xpath.evaluate(evalExpression, xmlDoc,
					XPathConstants.NODESET);
			if (nl != null) {
				String[] entries = new String[nl.getLength()];
				for (int i = 0; i < nl.getLength(); i++) {
					entries[i] = nl.item(i).getTextContent();
				}
				return entries;
			}
		} catch (XPathExpressionException e) {
			log.error("Error creating record ids.", e);
			e.printStackTrace();
		}
		return new String[0];
	}

	public String[] getLuceneStringArray(String field,
			org.apache.lucene.document.Document luceneDoc) {
		if (luceneDoc.getValues(field) != null)
			return luceneDoc.getValues(field);
		else
			return null;
	}

	/**
	 * Transforms an igc number string (e.g. x1, x2, y1, y2 from index) to a
	 * valid ISO gco:Decimal string. Returns "NaN" if problems occur !
	 */
	public String getISODecimalFromIGCNumber(String igcNumber) {
		String retValue;
		try {
			double n = Double.parseDouble(igcNumber.replaceAll(",", "."));
			retValue = String.valueOf(n);
		} catch (NumberFormatException e) {
			if (log.isDebugEnabled()) {
				log.debug("Could not convert to gco:Decimal: " + igcNumber, e);
			}
			retValue = "NaN";
		}
		return retValue;
	}
}
