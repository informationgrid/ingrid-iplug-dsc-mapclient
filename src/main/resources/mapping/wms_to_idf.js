	/**
	 * SourceRecord to IDF Document mapping
	 * Copyright (c) 2011 wemove digital solutions. All rights reserved.
	 *
	 * The following global variable are passed from the application:
	 *
     * @param sourceRecord A SourceRecord instance, that defines the input
     * @param luceneDoc A org.apache.lucene.document.Document instance, that defines the input
     * @param w3cDoc A org.w3c.dom.Document instance, that defines the output
     * @param log A Log instance
     * @param CAP CapabilitiesUtils helper class encapsulating utility methods
     * @param XML XMLUtils helper class encapsulating utility methods
     * @param IDX IndexUtils helper class encapsulatingutility methods
     * @param XPATH XPathUtils helper class encapsulating utility methods
	 * @param DOM DOMUtils helper class encapsulating processing DOM
	 */
	importPackage(Packages.org.w3c.dom);
	importPackage(Packages.de.ingrid.iplug.dsc.om);
	importPackage(Packages.org.apache.lucene.document);
	importPackage(Packages.de.ingrid.iplug.dsc.om);
	importPackage(Packages.de.ingrid.geo.utils.transformation);
	importPackage(Packages.de.ingrid.utils.xml);

	if (log.isDebugEnabled()) {
		log.debug("Mapping source record to idf document: " + sourceRecord.toString());
	}
	
	//we dont use a DatabaseSourcerecord here, for obvious reasons
	if (!(sourceRecord instanceof SourceRecord)) {
		throw new IllegalArgumentException("Record is no SourceRecord!");
	}
	//check the version of the wmsDoc
	var version;
	if(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_VERSION) == "1.1.1" || XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_VERSION) == "1.1.0")	
		version = "1.1.";
	else if(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_VERSION) == "1.3.0")
		version = "1.3.";
	// ---------- Initialize ----------
	//this used to be done by the ScriptedIdfMapper, but we now do it via js
	//because the whole indexing and mapping is done in one go via implemenation
	//of the IRecordMapper
//	var wmsDoc = sourceRecord.get("WmsDoc");
	DOM.addNS("idf", "http://www.portalu.de/IDF/1.0");
	
	var html = DOM.createElement("idf:html");
	w3cDoc.appendChild(html.getElement());
	html.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	html.addAttribute("xsi:schemaLocation", DOM.getNS("idf")
					+ " ingrid_detail_data_schema.xsd");
	html.addElement("idf:head");
	html.addElement("idf:body");
	
	// add Namespaces to Utility for convenient handling of NS !
	DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
	DOM.addNS("gco", "http://www.isotc211.org/2005/gco");
	DOM.addNS("srv", "http://www.isotc211.org/2005/srv");
	DOM.addNS("gml", "http://www.opengis.net/gml");
	DOM.addNS("gts", "http://www.isotc211.org/2005/gts");
	DOM.addNS("xlink", "http://www.w3.org/1999/xlink");
	
	// ---------- <idf:html> ----------
	var idfHtml = XPATH.getNode(w3cDoc, "/idf:html")
	DOM.addAttribute(idfHtml, "idf-version", "3.2.0");
	
	// ---------- <idf:body> ----------
	var idfBody = XPATH.getNode(w3cDoc, "/idf:html/idf:body");

	// ---------- <idf:idfMdMetadata> ----------
	var mdMetadata = DOM.addElement(idfBody, "idf:idfMdMetadata");
	// add needed "ISO" namespaces to top ISO node 
	mdMetadata.addAttribute("xmlns:gmd", DOM.getNS("gmd"));
	mdMetadata.addAttribute("xmlns:gco", DOM.getNS("gco"));
	mdMetadata.addAttribute("xmlns:srv", DOM.getNS("srv"));
	mdMetadata.addAttribute("xmlns:gml", DOM.getNS("gml"));
	mdMetadata.addAttribute("xmlns:gts", DOM.getNS("gts"));
	mdMetadata.addAttribute("xmlns:xlink", DOM.getNS("xlink"));
	// and schema references
	mdMetadata.addAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
	mdMetadata.addAttribute("xsi:schemaLocation", DOM.getNS("gmd")+" http://schemas.opengis.net/csw/2.0.2/profiles/apiso/1.0.0/apiso.xsd");

//    var fileIdentifier = luceneDoc.get("t011_obj_serv_op_connpoint.connect_point");
	if(version == "1.1.")
		var fileIdentifier = XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_OP_GET_CAPABILITIES_HREF);
	else if(version == "1.3.")		
		var fileIdentifier = XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_OP_GET_CAPABILITIES_HREF);
    if (hasValue(fileIdentifier)) {
    	mdMetadata.addElement("gmd:fileIdentifier/gco:CharacterString").addText(fileIdentifier);
    }
	
	
//	mdMetadata.addElement("gmd:language/gmd:LanguageCode")
//			  .addAttribute("codeList","http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/ML_gmxCodelists.xml#LanguageCode")
//			  .addAttribute("codeListValue", "service").addText("service");

	mdMetadata.addElement("gmd:hierarchyLevel/gmd:MD_ScopeCode")
		.addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml#MD_ScopeCode")
		.addAttribute("codeListValue", "service").addText("service");			  
	mdMetadata.addElement("gmd:hierarchyLevelName/gco:CharacterString").addText("service");

	mdMetadata.addElement("gmd:contact").addElement(getIdfResponsibleParty("pointOfContact"));
	
	// ---------- <gmd:metadataStandardName> ----------
	var mdStandardName = "ISO19119";
	
	mdMetadata.addElement("gmd:metadataStandardName/gco:CharacterString").addText(mdStandardName);
	// ---------- <gmd:metadataStandardVersion> ----------
	var mdStandardVersion = "2005/PDAM 1";
	mdMetadata.addElement("gmd:metadataStandardVersion/gco:CharacterString").addText(mdStandardVersion);

	var identificationInfo;
	
	identificationInfo = mdMetadata.addElement("gmd:identificationInfo/srv:SV_ServiceIdentification");
	//we define the unique id as the url of the service
	identificationInfo.addAttribute("uuid", CAP.getUrlStr());
	
	// ---------- <gmd:identificationInfo/gmd:citation/gmd:CI_Citation> ----------
	var ciCitation = identificationInfo.addElement("gmd:citation/gmd:CI_Citation");
	// ---------- <gmd:identificationInfo/gmd:citation/gmd:CI_Citation/gmd:title> ----------
	if(version == "1.1.")
		ciCitation.addElement("gmd:title/gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_TITLE));
	else if(version == "1.3.")
		ciCitation.addElement("gmd:title/gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_TITLE));
	// ---------- <gmd:identificationInfo/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier> ----------
	ciCitation.addElement("gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString").addText(CAP.getUrlStr()); 
	if(version == "1.1.")
		identificationInfo.addElement("gmd:abstract/gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_ABSTRACT));
	else if(version == "1.3.")
		identificationInfo.addElement("gmd:abstract/gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_ABSTRACT));
	identificationInfo.addElement("gmd:pointOfContact").addElement(getIdfResponsibleParty("pointOfContact"));
	var mdKeywords = getMdKeywords();
	if (mdKeywords != null)
	identificationInfo.addElement("gmd:descriptiveKeywords").addElement(mdKeywords);

// ---------- <gmd:identificationInfo/gmd:resourceConstraints> ----------
    // ---------- <gmd:MD_LegalConstraints> ----------
    addResourceConstraints(identificationInfo);			
	identificationInfo.addElement("srv:serviceType/gco:LocalName").addText("view");		
	if(version == "1.1.")
    	identificationInfo.addElement("srv:serviceTypeVersion/gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_VERSION));		
    else if(version == "1.3.")
    	identificationInfo.addElement("srv:serviceTypeVersion/gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_VERSION));
    var exExtent = null;
    var extentElemName = "srv:extent";
    if (!exExtent) {
	    exExtent = identificationInfo.addElement(extentElemName).addElement("gmd:EX_Extent");
	}
	if(version == "1.1."){
	    if (hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MINX)) && hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MAXX)) && 
	    	hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MINY)) && hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MAXY))) {
	    	// Spatial_ref_value.x1 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox.westBoundLongitude/gmd:approximateLongitude
			var exGeographicBoundingBox = exExtent.addElement("gmd:geographicElement/gmd:EX_GeographicBoundingBox");
			exGeographicBoundingBox.addElement("gmd:extentTypeCode/gco:Boolean").addText("true");
			exGeographicBoundingBox.addElement("gmd:westBoundLongitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MINX)));
			exGeographicBoundingBox.addElement("gmd:eastBoundLongitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MAXX)));
			exGeographicBoundingBox.addElement("gmd:southBoundLatitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MINY)));
			exGeographicBoundingBox.addElement("gmd:northBoundLatitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MAXY)));
		}	
	}else if(version == "1.3."){
	    if (hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MINX)) && hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MAXX)) && 
	    	hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MINY)) && hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MAXY))) {
	    	// Spatial_ref_value.x1 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox.westBoundLongitude/gmd:approximateLongitude
			var exGeographicBoundingBox = exExtent.addElement("gmd:geographicElement/gmd:EX_GeographicBoundingBox");
			exGeographicBoundingBox.addElement("gmd:extentTypeCode/gco:Boolean").addText("true");
			exGeographicBoundingBox.addElement("gmd:westBoundLongitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MINX)));
			exGeographicBoundingBox.addElement("gmd:eastBoundLongitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MAXX)));
			exGeographicBoundingBox.addElement("gmd:southBoundLatitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MINY)));
			exGeographicBoundingBox.addElement("gmd:northBoundLatitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MAXY)));
		}		
	}
    
                var svContainsOperations = identificationInfo.addElement("srv:containsOperations");
                var svOperationMetadata = svContainsOperations.addElement("srv:SV_OperationMetadata");

        // ---------- <srv:SV_OperationMetadata/srv:operationName> ----------
                svOperationMetadata.addElement("srv:operationName/gco:CharacterString").addText("GetCapabilities");
            if(version == "1.1.")
                svOperationMetadata.addElement("srv:connectPoint/gmd:CI_OnlineResource/gmd:linkage/gmd:URL").addText(sourceRecord.get("id"));
    		else if(version == "1.3.")
    			svOperationMetadata.addElement("srv:connectPoint/gmd:CI_OnlineResource/gmd:linkage/gmd:URL").addText(sourceRecord.get("id"));
	function hasValue(val) {
		if (typeof val == "undefined") {
			return false;
		} else if (val == null) {
			return false;
		} else if (typeof val == "string" && val == "") {
			return false;
		} else if (typeof val == "object" && val.toString().equals("")) {
			return false;
		} else {
			return true;
		}
	}
	
	function getIdfResponsibleParty(role) {
	
		var myElementName = "idf:idfResponsibleParty";
	
		var idfResponsibleParty = DOM.createElement(myElementName).addAttribute(
				"uuid", CAP.getUrlStr()).addAttribute("type", "0");//erstmal 0 für institution später differenzieren wir
		if(version == "1.1."){				
			var individualName = XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CONTACT_PERSON);//the whole person of contact gets mapped into this field        
			if (hasValue(individualName)) {
				idfResponsibleParty.addElement("gmd:individualName")
						.addElement("gco:CharacterString").addText(individualName);
			}
			if(version == "1.1.")
			var institution = XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CONTACT_ORGANIZATION);
			if (hasValue(institution)) {
				idfResponsibleParty.addElement("gmd:organisationName")
						.addElement("gco:CharacterString").addText(institution);
			}
		
			var ciContact = idfResponsibleParty.addElement("gmd:contactInfo")
					.addElement("gmd:CI_Contact");
		
			var ciTelephone = ciContact.addElement("gmd:phone")
					.addElement("gmd:CI_Telephone");
			ciTelephone.addElement("gmd:voice/gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CONTACT_VOICE_TELEPHONE));
			var emailAddress = XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CONTACT_EMAIL_ADDRESS);
		
			var ciAddress;
		
			if (hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_CITY))
					|| hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_ADDRESS))) {
				if (!ciAddress)
					ciAddress = ciContact.addElement("gmd:address")
							.addElement("gmd:CI_Address");
		
				ciAddress.addElement("gmd:deliveryPoint")
						.addElement("gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_ADDRESS));
				ciAddress.addElement("gmd:city").addElement("gco:CharacterString")
						.addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_CITY));
				ciAddress.addElement("gmd:postalCode")
						.addElement("gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_POSTCODE));
			}
		
			if (!ciAddress)
				ciAddress = ciContact.addElement("gmd:address/gmd:CI_Address");
		
			ciAddress.addElement("gmd:electronicMailAddress/gco:CharacterString")
					.addText(emailAddress);
		
			if (hasValue(role)) {
				idfResponsibleParty
						.addElement("gmd:role/gmd:CI_RoleCode")
						.addAttribute("codeList",
								"http://www.tc211.org/ISO19139/resources/codeList.xml#CI_RoleCode")
						.addAttribute("codeListValue", role);
			} else {
				idfResponsibleParty.addElement("gmd:role").addAttribute(
						"gco:nilReason", "inapplicable");
			}
		}else if(version == "1.3."){
			var institution = XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CONTACT_ORGANIZATION);
			if (hasValue(institution)) {
				idfResponsibleParty.addElement("gmd:organisationName")
						.addElement("gco:CharacterString").addText(institution);
			}
		
			var ciContact = idfResponsibleParty.addElement("gmd:contactInfo")
					.addElement("gmd:CI_Contact");
		
			var ciTelephone = ciContact.addElement("gmd:phone")
					.addElement("gmd:CI_Telephone");
			ciTelephone.addElement("gmd:voice/gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CONTACT_VOICE_TELEPHONE));
			var emailAddress = XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CONTACT_EMAIL_ADDRESS);
		
			var ciAddress;
		
			if (hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_CITY))
					|| hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_ADDRESS))) {
				if (!ciAddress)
					ciAddress = ciContact.addElement("gmd:address")
							.addElement("gmd:CI_Address");
		
				ciAddress.addElement("gmd:deliveryPoint")
						.addElement("gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_ADDRESS));
				ciAddress.addElement("gmd:city").addElement("gco:CharacterString")
						.addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_CITY));
				ciAddress.addElement("gmd:postalCode")
						.addElement("gco:CharacterString").addText(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_POSTCODE));
			}
		
			if (!ciAddress)
				ciAddress = ciContact.addElement("gmd:address/gmd:CI_Address");
		
			ciAddress.addElement("gmd:electronicMailAddress/gco:CharacterString")
					.addText(emailAddress);
		
			if (hasValue(role)) {
				idfResponsibleParty
						.addElement("gmd:role/gmd:CI_RoleCode")
						.addAttribute("codeList",
								"http://www.tc211.org/ISO19139/resources/codeList.xml#CI_RoleCode")
						.addAttribute("codeListValue", role);
			} else {
				idfResponsibleParty.addElement("gmd:role").addAttribute(
						"gco:nilReason", "inapplicable");
			}			
		}
		return idfResponsibleParty;
	}
	/**
	 * Creates an ISO MD_Keywords element based on the rows passed.
	 * NOTICE: All passed rows (keywords) have to be of same type (UMTHES || GEMET || INSPIRE || FREE || SERVICE classifications || ...).
	 * Only first row is analyzed.
	 * Returns null if no keywords added (no rows found or type of keywords cannot be determined ...) !
	 */
	function getMdKeywords() {
		if(version == "1.1.")
			var kws = XPATH.getStringArray(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_KEYWORDS);
		else if(version == "1.3.")
			var kws = XPATH.getStringArray(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_KEYWORDS);			
		var mdKeywords = DOM.createElement("gmd:MD_Keywords");
		var keywordsAdded = false;
		for (i = 0; i < kws.length; i++) {
			var sTerm = kws[i];
			var keywordValue = null;
	
			// "searchterm_value" table
			if (hasValue(sTerm)) {
				keywordValue = sTerm;
	
				if (hasValue(keywordValue)) {
					mdKeywords.addElement("gmd:keyword/gco:CharacterString")
							.addText(keywordValue);
					keywordsAdded = true;
				}
			}
		}
		if (!keywordsAdded)
			return null;
	
		return mdKeywords;
	}
	
	function addResourceConstraints(identificationInfo) {
	
	       var termsOfUse = null;
	       if(version == "1.1."){
	        	if (!hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_ACCESS_CONSTRAINTS))) {
	        		var termsOfUse = XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_1_1_ACCESS_CONSTRAINTS);
	        	}            
	       }else if(version == "1.3."){
	        	if (!hasValue(XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_ACCESS_CONSTRAINTS))) {
	        		var termsOfUse = XPATH.getString(wmsDoc, CAP.XPATH_EXP_WMS_1_3_0_ACCESS_CONSTRAINTS);
	        	}            	
	       }
			
	
	        if (hasValue(termsOfUse)) {
	            identificationInfo.addElement("gmd:resourceConstraints/gmd:MD_Constraints/gmd:useLimitation/gco:CharacterString").addText(termsOfUse);
	            var accessConstraints = termsOfUse;
	        }
	    	var otherConstraints = null;
	
	
	
	        // ---------- <gmd:MD_LegalConstraints/gmd:accessConstraints> ----------
	        // first map gmd:accessConstraints
	
	            identificationInfo.addElement("gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode")
	                    .addAttribute("codeListValue", accessConstraints)
	                    .addAttribute("codeList", "http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/gmxCodelists.xml#MD_RestrictionCode")
	                    .addText(accessConstraints);
	
		
	}
	
	//this happens lastly
	IDX.store("idf", XML.xmlDocToString(w3cDoc));
