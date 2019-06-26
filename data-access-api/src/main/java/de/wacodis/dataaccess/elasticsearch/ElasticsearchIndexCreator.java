/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.dataaccess.elasticsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ElasticsearchIndexCreator {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ElasticsearchIndexCreator.class);

    private final RestHighLevelClient elasticsearchClient;

    public ElasticsearchIndexCreator(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

   /**
    * initiate elasticsearch index
    * @param indexName
    * @param settingsJSON
    * @param timeoutMillis
    * @return true if index creation is acknowledged
    * @throws IOException 
    */
    public boolean createIndex(String indexName, String settingsJSON, long timeoutMillis) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest();
        
        //prepare request
        request.index(indexName)
               .timeout(TimeValue.timeValueMillis(timeoutMillis))
               .source(settingsJSON, XContentType.JSON); //including all sections (mappings, settings and aliases)   

        //start synchronous request
        LOGGER.debug("creating index " + indexName);
        CreateIndexResponse response = this.elasticsearchClient.indices().create(request, RequestOptions.DEFAULT);
        LOGGER.debug("creating index " + indexName + " acknowledged: " + response.isAcknowledged());
        
        return response.isAcknowledged();
    }
    
    /**
     * initiate elasticsearch index
     * @param indexName
     * @param settingsJSONFile read settings from file (JSON)
     * @param timeoutMillis
     * @return true if index creation is acknowledged
     * @throws IOException
     */
    public boolean createIndex(String indexName, File settingsJSONFile, long timeoutMillis) throws IOException {
        String settingsJSON = readSettingsFromFile(settingsJSONFile);
        return this.createIndex(indexName, settingsJSON, timeoutMillis);
    }

    

    /**
     * @param settingsFilePath
     * @return content of settings file (JSON string)
     */
    private String readSettingsFromFile(File settingsFile) throws IOException {
        LOGGER.debug("reading elasticsearch index settings from file " + settingsFile.getAbsolutePath());

        StringBuilder settingsStringBuilder = new StringBuilder();

        try (BufferedReader settingsReader = new BufferedReader(new FileReader(settingsFile))) {
            String line;
            while ((line = settingsReader.readLine()) != null) {
                settingsStringBuilder.append(line);
            }
        }

        String settingsStr = settingsStringBuilder.toString();

        LOGGER.debug("successfully read elasticsearch index settings from file " + settingsFile.getAbsolutePath() + " Settings: " + System.lineSeparator() + settingsStr);

        return settingsStr;
    }

}
