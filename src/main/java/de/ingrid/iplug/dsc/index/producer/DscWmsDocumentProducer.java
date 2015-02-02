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
