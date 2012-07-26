/**
 * 
 */
package de.ingrid.iplug.dsc.index.producer;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;

import de.ingrid.iplug.dsc.index.DscDocumentProducer;
import de.ingrid.iplug.dsc.index.mapper.IRecordMapper;
import de.ingrid.iplug.dsc.index.producer.IRecordSetProducer;
import de.ingrid.iplug.dsc.om.SourceRecord;

/**
 * Document Producer which may return null document (e.g. if service is unavailable) !
 * Then nothing is added to index in base webapp
 * @author martin
 */
public class DscWmsDocumentProducer extends DscDocumentProducer {

    final private static Log log = LogFactory.getLog(DscWmsDocumentProducer.class);
    
    public DscWmsDocumentProducer() {
        log.info("DscWmsDocumentProducer started.");
    }
    
    /* (non-Javadoc)
     * @see de.ingrid.iplug.dsc.index.DscDocumentProducer#next()
     */
    @Override
    public Document next() {
        Document doc = new Document();
        try {
        	IRecordSetProducer recProducer = getRecordSetProducer();
        	List<IRecordMapper> recMappers = getRecordMapperList();
            SourceRecord record = recProducer.next();
            for (IRecordMapper mapper : recMappers) {
                long start = 0;
                if (log.isDebugEnabled()) {
                    start = System.currentTimeMillis();
                }
                doc = mapper.map(record, doc);
                if (doc == null) {
                    log.warn("!!! Mapper " + mapper + " returned null document, we stop mapping and return null Document so will not be indexed !");
                	break;
                }
                if (log.isDebugEnabled()) {
                    log.debug("Mapping of source record with " + mapper + " took: " + (System.currentTimeMillis() - start) + " ms.");
                }
            }
            return doc;
        } catch (Exception e) {
            log.error("Error obtaining next record.", e);
            return null;
        }
    }
}
