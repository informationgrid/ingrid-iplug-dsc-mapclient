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
package de.ingrid.iplug.dscmapclient.index.data;

	/**
	 * This model is the datacontainer for the Plugdescription menu 
	 * at this moment we only need the path to the webmapclient config file
	 * 
	 * @author ma@wemove.com
	 *
	 */
	public class WmsFormModel {
	
		protected String xmlFilePath;
	
		
		
		public WmsFormModel() {
			super();
			// TODO Auto-generated constructor stub
		}
	
		public WmsFormModel(String xmlFilePath) {
			super();
			this.xmlFilePath = xmlFilePath;
		}
	
		public String getXmlFilePath() {
			return xmlFilePath;
		}
	
		public void setXmlFilePath(String xmlFilePath) {
			this.xmlFilePath = xmlFilePath;
		}
		
	}
