	/**
	 * SourceRecord to IDF Document mapping
	 * Copyright (c) 2011 wemove digital solutions. All rights reserved.
	 *
	 * The following global variable are passed from the application:
	 *
	 * @param sourceRecord A SourceRecord instance, that defines the input
	 * @param idfDoc A IDF Document (XML-DOM) instance, that defines the output
	 * @param log A Log instance
	 * @param XPATH xpath helper class encapsulating utility methods
	 * @param TRANSF Helper class for transforming, processing values
	 * @param DOM Helper class encapsulating processing DOM
	 */
	importPackage(Packages.org.w3c.dom);
	importPackage(Packages.de.ingrid.iplug.dsc.om);
	importPackage(Packages.org.apache.lucene.document);
	importPackage(Packages.de.ingrid.iplug.dsc.om);
	importPackage(Packages.de.ingrid.geo.utils.transformation);
	importPackage(Packages.de.ingrid.utils.xml);
	if (log.isDebugEnabled()) {
		log.debug("Mapping source record to lucene document: "
				+ sourceRecord.toString());
	}
	
	//we dont use a DatabaseSourcerecord here, for obvious reasons
	if (!(sourceRecord instanceof SourceRecord)) {
		throw new IllegalArgumentException("Record is no SourceRecord!");
	}
	
	
	// ---------- Initialize ----------
	//this used to be done by the ScriptedIdfMapper, but we now do it via js
	//because the whole indexing and mapping is done in one go via implemenation
	//of the IRecordMapper
	
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

    var fileIdentifier = luceneDoc.get("t011_obj_serv_op_connpoint.connect_point");
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
	identificationInfo.addAttribute("uuid", luceneDoc.get("id"));
	
	// ---------- <gmd:identificationInfo/gmd:citation/gmd:CI_Citation> ----------
	var ciCitation = identificationInfo.addElement("gmd:citation/gmd:CI_Citation");
	// ---------- <gmd:identificationInfo/gmd:citation/gmd:CI_Citation/gmd:title> ----------
	ciCitation.addElement("gmd:title/gco:CharacterString").addText(luceneDoc.get("title"));
	// ---------- <gmd:identificationInfo/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier> ----------
	ciCitation.addElement("gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString").addText(luceneDoc.get("id")); 
	
	identificationInfo.addElement("gmd:abstract/gco:CharacterString").addText(luceneDoc.get("summary"));
	identificationInfo.addElement("gmd:pointOfContact").addElement(getIdfResponsibleParty("pointOfContact"));
	var mdKeywords = getMdKeywords();
	if (mdKeywords != null)
	identificationInfo.addElement("gmd:descriptiveKeywords").addElement(mdKeywords);

// ---------- <gmd:identificationInfo/gmd:resourceConstraints> ----------
    // ---------- <gmd:MD_LegalConstraints> ----------
    addResourceConstraints(identificationInfo);			
	identificationInfo.addElement("srv:serviceType/gco:LocalName").addText("view");				
    identificationInfo.addElement("srv:serviceTypeVersion/gco:CharacterString").addText(luceneDoc.get("t011_obj_serv_version.version"));		
    
    var exExtent = null;
    var extentElemName = "srv:extent";
    if (!exExtent) {
	    exExtent = identificationInfo.addElement(extentElemName).addElement("gmd:EX_Extent");
	}
    if (hasValue(luceneDoc.get("spatial_ref_value.x1")) && hasValue(luceneDoc.get("spatial_ref_value.x2")) && 
    	hasValue(luceneDoc.get("spatial_ref_value.y1")) && hasValue(luceneDoc.get("spatial_ref_value.y2"))) {
    	// Spatial_ref_value.x1 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox.westBoundLongitude/gmd:approximateLongitude
		var exGeographicBoundingBox = exExtent.addElement("gmd:geographicElement/gmd:EX_GeographicBoundingBox");
		exGeographicBoundingBox.addElement("gmd:extentTypeCode/gco:Boolean").addText("true");
		exGeographicBoundingBox.addElement("gmd:westBoundLongitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(luceneDoc.get("spatial_ref_value.x1")));
		exGeographicBoundingBox.addElement("gmd:eastBoundLongitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(luceneDoc.get("spatial_ref_value.x2")));
		exGeographicBoundingBox.addElement("gmd:southBoundLatitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(luceneDoc.get("spatial_ref_value.y1")));
		exGeographicBoundingBox.addElement("gmd:northBoundLatitude/gco:Decimal").addText(CAP.getISODecimalFromIGCNumber(luceneDoc.get("spatial_ref_value.y2")));
	}	
    
                var svContainsOperations = identificationInfo.addElement("srv:containsOperations");
                var svOperationMetadata = svContainsOperations.addElement("srv:SV_OperationMetadata");

        // ---------- <srv:SV_OperationMetadata/srv:operationName> ----------
                svOperationMetadata.addElement("srv:operationName/gco:CharacterString").addText("GetCapabilities");
                svOperationMetadata.addElement("srv:connectPoint/gmd:CI_OnlineResource/gmd:linkage/gmd:URL").addText(luceneDoc.get("t011_obj_serv_op_connpoint.connect_point"));
    
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
				"uuid", luceneDoc.get("id")).addAttribute("type", "0");//erstmal 0 für institution später differenzieren wir
		var individualName = luceneDoc.get("t02_address.lastname");//the whole person of contact gets mapped into this field        
	
		if (hasValue(individualName)) {
			idfResponsibleParty.addElement("gmd:individualName")
					.addElement("gco:CharacterString").addText(individualName);
		}
	
		var institution = luceneDoc.get("t02_address.institution");
		if (hasValue(institution)) {
			idfResponsibleParty.addElement("gmd:organisationName")
					.addElement("gco:CharacterString").addText(institution);
		}
	
		var ciContact = idfResponsibleParty.addElement("gmd:contactInfo")
				.addElement("gmd:CI_Contact");
	
		var ciTelephone = ciContact.addElement("gmd:phone")
				.addElement("gmd:CI_Telephone");
		ciTelephone.addElement("gmd:voice/gco:CharacterString").addText(luceneDoc
				.get("t021_communication.comm_value_phone"));
		var emailAddress = luceneDoc.get("t021_communication.comm_value_email");
	
		var ciAddress;
	
		if (hasValue(luceneDoc.get("t02_address.postbox_pc"))
				|| hasValue(luceneDoc.get("t02_address.city"))
				|| hasValue(luceneDoc.get("t02_address.street"))) {
			if (!ciAddress)
				ciAddress = ciContact.addElement("gmd:address")
						.addElement("gmd:CI_Address");
	
			ciAddress.addElement("gmd:deliveryPoint")
					.addElement("gco:CharacterString").addText(luceneDoc
							.get("t02_address.street"));
			ciAddress.addElement("gmd:city").addElement("gco:CharacterString")
					.addText(luceneDoc.get("t02_address.city"));
			ciAddress.addElement("gmd:postalCode")
					.addElement("gco:CharacterString").addText(luceneDoc
							.get("t02_address.postcode"));
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
	
		return idfResponsibleParty;
	}
	/**
	 * Creates an ISO MD_Keywords element based on the rows passed.
	 * NOTICE: All passed rows (keywords) have to be of same type (UMTHES || GEMET || INSPIRE || FREE || SERVICE classifications || ...).
	 * Only first row is analyzed.
	 * Returns null if no keywords added (no rows found or type of keywords cannot be determined ...) !
	 */
	function getMdKeywords() {
		var kws = luceneDoc.getValues("searchterm_value");
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
	        if (!hasValue(luceneDoc.get("object_use.terms_of_use_value"))) {
	        	var termsOfUse = luceneDoc.get("object_use.terms_of_use_value");
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
