package de.ingrid.iplug.dsc.index;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;
import org.springframework.core.io.ClassPathResource;

import de.ingrid.iplug.dsc.index.mapper.IRecordMapper;
import de.ingrid.iplug.dsc.index.mapper.ScriptedIdfDocumentMapper;
import de.ingrid.iplug.dsc.index.mapper.ScriptedWmsDocumentMapper;
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
        m.setMappingScript(new ClassPathResource("wms_to_lucene.js"));
        m.setCompile(false);
        ScriptedIdfDocumentMapper mapper = new ScriptedIdfDocumentMapper();
        mapper.setMappingScript(new ClassPathResource("wms_to_idf.js"));
        m.setCompile(false);

        List<IRecordMapper> mList = new ArrayList<IRecordMapper>();
        mList.add(m);
        mList.add(mapper);
        DscDocumentProducer dp = new DscDocumentProducer();
        dp.setRecordSetProducer(p);
        dp.setRecordMapperList(mList);
        //TODO define proper directory
        String indexDirectory = "luceneTestIndex";
        final IndexWriter writer = new IndexWriter(indexDirectory, new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.LIMITED);
        if (dp.hasNext()) {
            while (dp.hasNext()) {
                Document doc = dp.next();
                assertNotNull(doc);                
                writer.addDocument(doc);
            }
        } else {
            fail("No document produced");
        }
        

        writer.optimize();
        writer.close();
    }
    
}