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
 * SourceRecord to Lucene Document mapping
 * Copyright (c) 2011 wemove digital solutions. All rights reserved.
 *
 * The following global variable are passed from the application:
 *
 * @param wmsDoc A org.w3c.dom.Document instance, that defines the capabilities input
 * @param log A Log instance
 * @param CAP CapabilitiesUtils helper class encapsulating utility methods
 * @param IDX IndexUtils helper class encapsulatingutility methods
 * @param XPATH XPathUtils helper class encapsulating utility methods
 */
if (javaVersion.indexOf( "1.8" ) === 0) {
	load("nashorn:mozilla_compat.js");
}

importPackage(Packages.org.apache.lucene.document);
importPackage(Packages.de.ingrid.iplug.dsc.om);
importPackage(Packages.de.ingrid.geo.utils.transformation);
importPackage(Packages.de.ingrid.iplug.dsc.index.mapper);
importPackage(Packages.org.w3c.dom);
importPackage(Packages.de.ingrid.utils.xml);
importPackage(Packages.de.ingrid.iplug.dsc.utils);


if (log.isDebugEnabled()) {
    log.debug("Mapping wmsDoc to lucene document: " + wmsDoc.toString());
}

if (!(wmsDoc instanceof org.w3c.dom.Document)) {
    throw new IllegalArgumentException("wmsDoc is no org.w3c.dom.Document!");
}

var objectClass = 3;
IDX.add("id", escape(CAP.getUrlStr()));
// we check for version 1.1.0 just as version 1.1.1. since they basically have the same structure
if(XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_VERSION) == "1.1.1" || XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_VERSION) == "1.1.0"){
	IDX.add("t01_object.obj_class",objectClass);
	IDX.add("title",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_TITLE));
	IDX.add("t011_obj_serv_version.version",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_VERSION));
	IDX.add("t011_obj_serv.type","view");
	IDX.add("t011_obj_serv_op_connpoint.connect_point",CAP.getUrlStr());
	IDX.add("t01_object.obj_id",CAP.getUrlStr());
	IDX.add("summary",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_ABSTRACT));
	
	IDX.add("t02_address.lastname",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CONTACT_PERSON));
	IDX.add("t02_address.firstname",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CONTACT_PERSON));
	IDX.add("t02_address.institution",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CONTACT_ORGANIZATION));
	IDX.add("t021_communication.comm_value_phone",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CONTACT_VOICE_TELEPHONE));
	IDX.add("t021_communication.comm_value_email",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CONTACT_EMAIL_ADDRESS));
	IDX.add("t02_address.street",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_ADDRESS));
	IDX.add("t02_address.city",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_CITY));
	IDX.add("t02_address.postcode",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_POSTCODE));
	IDX.add("t02_address.country",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_COUNTRY));
	IDX.add("object_use.terms_of_use_value",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_ACCESS_CONSTRAINTS));
	IDX.add("spatial_ref_value.x1",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MINX));
	IDX.add("spatial_ref_value.y1",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MINY));
	IDX.add("spatial_ref_value.x2",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MAXX));
	IDX.add("spatial_ref_value.y2",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MAXY));
//	
	var entries = XPATH.getStringArray(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_KEYWORDS);
	for(var i = 0; i < entries.length; i++){
		if(entries[i] != '')
		IDX.add("t04_search.searchterm", entries[i]);
	}
	var layerTitles = XPATH.getStringArray(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_LAYERTITLES);
	for(var i = 0; i < layerTitles.length; i++){
		if(layerTitles[i] != '')
		IDX.add("layer_titles", layerTitles[i]);
	}
}else if(XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_VERSION) == "1.3.0"){
	IDX.add("t01_object.obj_class",objectClass);
	IDX.add("title",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_TITLE));
	IDX.add("t011_obj_serv_version.version",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_VERSION));
	
	IDX.add("t011_obj_serv_op_connpoint.connect_point",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_OP_GET_CAPABILITIES_HREF));
	IDX.add("t01_object.obj_id",CAP.getUrlStr());
	IDX.add("t011_obj_serv.type","wms");
	IDX.add("summary",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_ABSTRACT));
	
	IDX.add("t02_address.lastname",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CONTACT_PERSON));
	IDX.add("t02_address.firstname",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CONTACT_PERSON));
	IDX.add("t02_address.institution",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CONTACT_ORGANIZATION));
	IDX.add("t021_communication.comm_value_phone",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CONTACT_VOICE_TELEPHONE));
	IDX.add("t021_communication.comm_value_email",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CONTACT_EMAIL_ADDRESS));
	IDX.add("t02_address.street",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_ADDRESS));
	IDX.add("t02_address.city",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_CITY));
	IDX.add("t02_address.postcode",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_POSTCODE));
	IDX.add("t02_address.country",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_COUNTRY));
	IDX.add("object_use.terms_of_use_value",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_ACCESS_CONSTRAINTS));
	IDX.add("spatial_ref_value.x1",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MINX));
	IDX.add("spatial_ref_value.y1",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MINY));
	IDX.add("spatial_ref_value.x2",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MAXX));
	IDX.add("spatial_ref_value.y2",XPATH.getString(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MAXY));
	var entries = XPATH.getStringArray(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_3_0_KEYWORDS);
	for(var i = 0; i < entries.length; i++){
		if(entries[i] != '')
		IDX.add("t04_search.searchterm", entries[i]);
	}
	var layerTitles = XPATH.getStringArray(wmsDoc, CapabilitiesUtils.XPATH_EXP_WMS_1_1_1_LAYERTITLES);
	for(var i = 0; i < layerTitles.length; i++){
		if(layerTitles[i] != '')
		IDX.add("layer_titles", layerTitles[i]);
	}
}else{
	IDX.add("t01_object.obj_class",objectClass); //TODO stimmt das?!?
	IDX.add("serviceUnavailable","serviceUnavailable"); //TODO stimmt das?!?
}


