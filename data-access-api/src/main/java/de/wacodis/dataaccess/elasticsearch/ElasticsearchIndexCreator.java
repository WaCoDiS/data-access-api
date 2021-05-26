/*
 * Copyright 2018-2021 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.wacodis.dataaccess.elasticsearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.elasticsearch.ElasticsearchStatusException;
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
        try {
            CreateIndexResponse response = this.elasticsearchClient.indices().create(request, RequestOptions.DEFAULT);
            LOGGER.debug("creating index " + indexName + " acknowledged: " + response.isAcknowledged());
        } catch (ElasticsearchStatusException e) {
            // if the index is already present, to not fail
            if (e.getDetailedMessage().contains("resource_already_exists_exception")) {
                LOGGER.warn("Index already present. If you observe misbehaviour, the index might not be set up correctly.");
            } else {
                throw e;
            }
        }
        
        return true;
    }
    
    /**
     * initiate elasticsearch index
     * @param indexName
     * @param settingsStream read settings from stream (JSON)
     * @param timeoutMillis
     * @return true if index creation is acknowledged
     * @throws IOException
     */
    public boolean createIndex(String indexName, InputStream settingsStream, long timeoutMillis) throws IOException {
        String settingsJSON = readSettingsFromStream(settingsStream);
        return this.createIndex(indexName, settingsJSON, timeoutMillis);
    }

    

    /**
     * @param settingsFilePath
     * @return content of settings file (JSON string)
     */
    private String readSettingsFromStream(InputStream settingsStream) throws IOException {
        LOGGER.debug("reading elasticsearch index settings from stream");

        StringBuilder settingsStringBuilder = new StringBuilder();

        try (BufferedReader settingsReader = new BufferedReader(new InputStreamReader(settingsStream))) {
            String line;
            while ((line = settingsReader.readLine()) != null) {
                settingsStringBuilder.append(line);
            }
        }

        String settingsStr = settingsStringBuilder.toString();

        LOGGER.debug("successfully read elasticsearch index settings. Settings: " + System.lineSeparator() + settingsStr);

        return settingsStr;
    }

}
