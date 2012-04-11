/**
 * 
 */
package de.ingrid.iplug.dsc.record.producer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import de.ingrid.iplug.dsc.index.DatabaseConnection;
import de.ingrid.iplug.dsc.om.IClosableDataSource;
import de.ingrid.iplug.dsc.om.SourceRecord;
import de.ingrid.utils.IConfigurable;
import de.ingrid.utils.PlugDescription;

/**
 * This class retrieves a record from a data source. It retrieves an
 * id from a lucene document ({@link getRecord}) and creates a
 * {@link SourceRecord} containing the ID that identifies the
 * record.
 * This used to be done via a database connection, which we dont have anymore
 * since we get our urls though the webmapclient xml file and
 * map the responses into the lucene document directly.
 * The DscRecordCreator though still needs a database connection, which we 
 * set to null. And we return a SourceRecord now not a DatabaseSourceRecord
 * anymore. 
 * 
 * 
 * @author ma@wemove.com
 * 
 */
public class PlugDescriptionConfiguredWmsRecordProducer implements IRecordProducer, IConfigurable {

    private String indexFieldID;

    DatabaseConnection internalDatabaseConnection = null;

    final private static Log log = LogFactory.getLog(PlugDescriptionConfiguredWmsRecordProducer.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ingrid.iplug.dsc.record.IRecordProducer#getRecord(org.apache.lucene
     * .document.Document)
     */
    @Override
    public SourceRecord getRecord(Document doc, IClosableDataSource ds) {
        if (indexFieldID == null) {
            log.error("Name of ID-Field in Lucene Doc is not set!");
            throw new IllegalArgumentException("Name of ID-Field in Lucene Doc is not set!");
        }

        Field field = doc.getField(indexFieldID);

        return new SourceRecord(field.stringValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ingrid.iplug.dsc.record.IRecordProducer#getRecord(org.apache.lucene
     * .document.Document)
     */
    @Override
    public void configure(PlugDescription plugDescription) {
        this.internalDatabaseConnection = (DatabaseConnection) plugDescription.getConnection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ingrid.iplug.dsc.record.IRecordProducer#openDatasource()
     */
    @Override
    public IClosableDataSource openDatasource() {
    	//we dont have a database connection, therefore we return null
        return null;
    }

    public String getIndexFieldID() {
        return indexFieldID;
    }

    public void setIndexFieldID(String indexFieldID) {
        this.indexFieldID = indexFieldID;
    }
}
