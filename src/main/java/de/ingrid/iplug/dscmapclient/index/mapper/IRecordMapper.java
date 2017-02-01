/*
 * **************************************************-
 * ingrid-iplug-dsc-mapclient
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
package de.ingrid.iplug.dscmapclient.index.mapper;

import de.ingrid.iplug.dsc.om.SourceRecord;
import de.ingrid.utils.ElasticDocument;

/**
 * Interface for all source record to lucene document mapper classes of dsc-mapclient.
 * NOTICE: differs from interfaces in other dsc iPlugs cause it can return null Document indicating NO INDEXING !
 * (e.g. if service not reachable) ! 
 * 
 * @author martin
 * 
 */
public interface IRecordMapper {

	/**
	 * Maps a source record into a lucene document. The content of the source
	 * record may vary. It is up to the implementing class to interpret the
	 * source record and throw exceptions, if the record does not comply with
	 * the needs of the mapper.</br>
	 * <b>NOTICE: Returns null document if it should not be indexed</b> (e.g. when service not available) !
	 * 
	 * @param record the record to map
	 * @param doc the index Document to extend
	 * @return the passed index Document <b>OR NULL if should not be indexed</b>
	 */
	ElasticDocument map(SourceRecord record, ElasticDocument doc) throws Exception;

}
