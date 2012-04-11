package de.ingrid.iplug.dsc.index.data;

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
