/*
 * **************************************************-
 * ingrid-iplug-dsc-mapclient
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.iplug.dscmapclient.index.producer;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.admin.elasticsearch.IndexInfo;
import de.ingrid.admin.object.IDocumentProducer;
import de.ingrid.iplug.dsc.index.producer.IRecordSetProducer;
import de.ingrid.iplug.dsc.om.SourceRecord;
import de.ingrid.iplug.dscmapclient.index.mapper.IRecordMapper;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.PlugDescription;

/**
 * Document Producer which may return null document (e.g. if service is unavailable) !
 * Then nothing is added to index in base webapp
 * @author martin
 */
@Service
public class DscWmsDocumentProducer implements IDocumentProducer {

    final private static Log log = LogFactory.getLog(DscWmsDocumentProducer.class);
    
    @Autowired
    private IRecordSetProducer recordSetProducer = null;

    @Autowired
    private List<IRecordMapper> recordMapperList = null;
    
    public DscWmsDocumentProducer() {
        log.info("DscWmsDocumentProducer started.");
    }
    
    /* (non-Javadoc)
     * @see de.ingrid.iplug.dsc.index.DscDocumentProducer#next()
     */
    @Override
    public ElasticDocument next() {
        ElasticDocument doc = new ElasticDocument();
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
    
    /*
     * (non-Javadoc)
     * 
     * @see de.ingrid.admin.object.IDocumentProducer#hasNext()
     */
    @Override
    public boolean hasNext() {
        try {
            return recordSetProducer.hasNext();
        } catch (Exception e) {
            log.error("Error obtaining information about a next record. Skip all records.", e);
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ingrid.utils.IConfigurable#configure(de.ingrid.utils.PlugDescription)
     */
    @Override
    public void configure(PlugDescription arg0) {
        log.info("DscDocumentProducer: configuring...");
    }
    
    public IRecordSetProducer getRecordSetProducer() {
        return recordSetProducer;
    }

    public void setRecordSetProducer(IRecordSetProducer recordProducer) {
        this.recordSetProducer = recordProducer;
    }

    public List<IRecordMapper> getRecordMapperList() {
        return recordMapperList;
    }

    public void setRecordMapperList(List<IRecordMapper> recordMapperList) {
        this.recordMapperList = recordMapperList;
    }

    @Override
    public IndexInfo getIndexInfo() {
        return null;
    }

    @Override
    public Integer getDocumentCount() {
        return null;
    }
}
