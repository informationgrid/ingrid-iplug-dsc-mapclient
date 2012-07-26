package de.ingrid.iplug.dsc.index.mapper;

import org.apache.lucene.document.Document;

import de.ingrid.iplug.dsc.om.SourceRecord;

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
	Document map(SourceRecord record, Document doc) throws Exception;

}
