/*
 * **************************************************-
 * ingrid-iplug-dsc-mapclient
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.iplug.dsc.index;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;
import org.springframework.core.io.FileSystemResource;

import de.ingrid.iplug.dsc.index.mapper.IRecordMapper;
import de.ingrid.iplug.dsc.index.mapper.ScriptedIdfDocumentMapper;
import de.ingrid.iplug.dsc.index.mapper.ScriptedWmsDocumentMapper;
import de.ingrid.iplug.dsc.index.producer.DscWmsDocumentProducer;
import de.ingrid.iplug.dsc.index.producer.PlugDescriptionConfiguredWmsRecordSetProducer;
import de.ingrid.utils.PlugDescription;

public class ScriptedWmsDocumentProducerTest extends TestCase {



    public void testScriptedDatabaseDocumentProducer() throws Exception {



        PlugDescription pd = new PlugDescription();
        pd.put("WebmapXmlConfigFile", "src/test/resources/ingrid_webmap_client_config.xml");

        PlugDescriptionConfiguredWmsRecordSetProducer p = new PlugDescriptionConfiguredWmsRecordSetProducer();
        p.setIdTag("//capabilitiesUrl");
        p.configure(pd);
        
        ScriptedWmsDocumentMapper m = new ScriptedWmsDocumentMapper();
        m.setMappingScript(new FileSystemResource("src/main/resources/mapping/wms_to_lucene.js"));
        m.setCompile(false);
        ScriptedIdfDocumentMapper mapper = new ScriptedIdfDocumentMapper();
        mapper.setMappingScript(new FileSystemResource("src/main/resources/mapping/wms_to_idf.js"));
        mapper.setCompile(false);

        List<IRecordMapper> mList = new ArrayList<IRecordMapper>();
        mList.add(m);
        mList.add(mapper);
        DscWmsDocumentProducer dp = new DscWmsDocumentProducer();
        dp.setRecordSetProducer(p);
        dp.setRecordMapperList(mList);
        //TODO define proper directory
        String indexDirectory = "luceneTestIndex";
        final IndexWriter writer = new IndexWriter(indexDirectory, new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.LIMITED);
        if (dp.hasNext()) {
            while (dp.hasNext()) {
                Document doc = dp.next();
                // NOTICE: may be null (e.g. if service not reachable) !
                if (doc == null) {
                	continue;
                }
                writer.addDocument(doc);
            }
        } else {
            fail("No document produced");
        }
        

        writer.optimize();
        writer.close();
    }
    
}
