/**
 * SourceRecord to Lucene Document mapping
 * Copyright (c) 2011 wemove digital solutions. All rights reserved.
 *
 * The following global variable are passed from the application:
 *
 * @param sourceRecord A SourceRecord instance, that defines the input
 * @param luceneDoc A lucene Document instance, that defines the output
 * @param log A Log instance
 * @param SQL SQL helper class encapsulating utility methods
 * @param IDX Lucene index helper class encapsulating utility methods for output
 * @param TRANSF Helper class for transforming, processing values/fields.
 */
importPackage(Packages.org.apache.lucene.document);
importPackage(Packages.de.ingrid.iplug.dsc.om);
importPackage(Packages.de.ingrid.geo.utils.transformation);
importPackage(Packages.de.ingrid.iplug.dsc.index.mapper);
importPackage(Packages.org.w3c.dom);
importPackage(Packages.de.ingrid.utils.xml);


if (log.isDebugEnabled()) {
	log.debug("Mapping source record to lucene document: " + sourceRecord.toString());
}

//we dont us a DatabaseSourcerecord here, for obvious reasons
if (!(sourceRecord instanceof SourceRecord)) {
    throw new IllegalArgumentException("Record is no DatabaseRecord!");
}

var xpath = CAP.getXpath();
var objectClass = 3;
IDX.add("id", CAP.getUrlStr());

if(CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_VERSION,xmlDoc) == "1.1.1" ){
	IDX.add("t01_object.obj_class",objectClass); //TODO stimmt das?!?
	IDX.add("title",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_TITLE,xmlDoc));
	IDX.add("t011_obj_serv_version.version",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_VERSION,xmlDoc));
	IDX.add("t011_obj_serv.type","wms");
	IDX.add("t011_obj_serv_op_connpoint.connect_point",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_OP_GET_CAPABILITIES_HREF,xmlDoc));
	IDX.add("summary",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_ABSTRACT,xmlDoc));
	
	IDX.add("t02_address.lastname",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CONTACT_PERSON,xmlDoc));
	IDX.add("t02_address.firstname",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CONTACT_PERSON,xmlDoc));
	IDX.add("t02_address.institution",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CONTACT_ORGANIZATION,xmlDoc));
	IDX.add("t021_communication.comm_value_phone",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CONTACT_VOICE_TELEPHONE,xmlDoc));
	IDX.add("t021_communication.comm_value_email",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CONTACT_EMAIL_ADDRESS,xmlDoc));
	IDX.add("t02_address.street",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_ADDRESS,xmlDoc));
	IDX.add("t02_address.city",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_CITY,xmlDoc));
	IDX.add("t02_address.postcode",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_POSTCODE,xmlDoc));
	IDX.add("t02_address.country",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CONTACT_POSTAL_COUNTRY,xmlDoc));
	IDX.add("object_use.terms_of_use_value",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_ACCESS_CONSTRAINTS,xmlDoc));
	IDX.add("spatial_ref_value.x1",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MINX,xmlDoc));
	IDX.add("spatial_ref_value.y1",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MINY,xmlDoc));
	IDX.add("spatial_ref_value.x2",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MAXX,xmlDoc));
	IDX.add("spatial_ref_value.y2",CAP.evaluate(CAP.XPATH_EXP_WMS_1_1_1_CAPABILITIES_BBOX_MAXY,xmlDoc));
//	
	var entries = CAP.evaluateList(CAP.XPATH_EXP_WMS_1_1_1_KEYWORDS, xmlDoc);
	for(var i = 0; i < entries.length; i++){
		if(entries[i] != '')
		IDX.add("searchterm_value", entries[i]);
	}
}else if(CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_VERSION,xmlDoc) == "1.3.0"){
	IDX.add("t01_object.obj_class",objectClass);
	IDX.add("title",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_TITLE,xmlDoc));
	IDX.add("t011_obj_serv_version.version",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_VERSION,xmlDoc));
	
	IDX.add("t011_obj_serv_op_connpoint.connect_point",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_OP_GET_CAPABILITIES_HREF,xmlDoc));
	IDX.add("t011_obj_serv.type","wms");
	IDX.add("summary",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_ABSTRACT,xmlDoc));
	
	IDX.add("t02_address.lastname",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CONTACT_PERSON,xmlDoc));
	IDX.add("t02_address.firstname",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CONTACT_PERSON,xmlDoc));
	IDX.add("t02_address.institution",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CONTACT_ORGANIZATION,xmlDoc));
	IDX.add("t021_communication.comm_value_phone",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CONTACT_VOICE_TELEPHONE,xmlDoc));
	IDX.add("t021_communication.comm_value_email",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CONTACT_EMAIL_ADDRESS,xmlDoc));
	IDX.add("t02_address.street",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_ADDRESS,xmlDoc));
	IDX.add("t02_address.city",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_CITY,xmlDoc));
	IDX.add("t02_address.postcode",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_POSTCODE,xmlDoc));
	IDX.add("t02_address.country",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CONTACT_POSTAL_COUNTRY,xmlDoc));
	IDX.add("object_use.terms_of_use_value",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_ACCESS_CONSTRAINTS,xmlDoc));
	IDX.add("spatial_ref_value.x1",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MINX,xmlDoc));
	IDX.add("spatial_ref_value.y1",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MINY,xmlDoc));
	IDX.add("spatial_ref_value.x2",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MAXX,xmlDoc));
	IDX.add("spatial_ref_value.y2",CAP.evaluate(CAP.XPATH_EXP_WMS_1_3_0_CAPABILITIES_BBOX_MAXY,xmlDoc));
	var entries = CAP.evaluateList(CAP.XPATH_EXP_WMS_1_3_0_KEYWORDS, xmlDoc);
}else{
	IDX.add("t01_object.obj_class",objectClass); //TODO stimmt das?!?
	IDX.add("serviceUnavailable","serviceUnavailable"); //TODO stimmt das?!?
}


