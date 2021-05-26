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
package de.wacodis.dataaccess.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Configuration
@ConfigurationProperties("spring.dataenvelopes-api.elasticsearch")
@EnableConfigurationProperties
@EnableRetry
public class ElasticsearchDataEnvelopesAPIConfiguration {
    @Value("${spring.dataenvelopes-api.elasticsearch.uri:http://localhost:9200}")
    private String uri;
    @Value("${spring.dataenvelopes-api.elasticsearch.indexName:dataenvelope}")
    private String indexName;
    @Value("${spring.dataenvelopes-api.elasticsearch.type:dataenvelope}")
    private String type;
    @Value("${spring.dataenvelopes-api.elasticsearch.requestTimeout_Millis:5000}")
    private long requestTimeout_Millis;
    @Value("${spring.dataenvelopes-api.elasticsearch.indexInitialization_RetryMaxAttempts:1}")
    private int indexInitialization_RetryMaxAttempts;
    @Value("${spring.dataenvelopes-api.elasticsearch.indexInitialization_RetryDelay_Millis:15000}")
    private long indexInitialization_RetryDelay_Millis;
    @Value("${spring.dataenvelopes-api.elasticsearch.indexInitialization_SettingsFile:./src/main/resources/elasticsearch_indexsettings.json}")
    private String indexInitialization_SettingsFile;
    //only used if indexInitialization_SettingsFile is not provided or not file not found, relativ path from src/main/resources, without leading slash
    @Value("${spring.dataenvelopes-api.elasticsearch.indexInitialization_SettingsFile_Resources:elasticsearch_indexsettings.json}")
    private String indexInitialization_SettingsFile_Resources;

    /**
     * get uri of elasticsearch instance
     * @return 
     */
    public String getUri() {
        return uri;
    }

    /**
     * set uri of elasticsearch instance
     * @param uri 
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

   /**
    * get index that should be queried
    * @return 
    */ 
    public String getIndexName() {
        return indexName;
    }

    /**
     * set index that should be queried
     * @param indexName 
     */
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    /**
     * get request requestTimeout_Millis in millis
     * @return 
     */
    public long getRequestTimeout_Millis() {
        return requestTimeout_Millis;
    }

    /**
     * set request requestTimeout_Millis in millis
     * @param requestTimeout_Millis 
     */
    public void setRequestTimeout_Millis(long requestTimeout_Millis) {
        this.requestTimeout_Millis = requestTimeout_Millis;
    }

    /**
     * get document/mapping type
     * @return 
     */
    public String getType() {
        return type;
    }

    /**
     * set document/mapping type
     * @param type 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * get max attempts for index initialization during start up process
     * @return 
     */
    public int getIndexInitialization_RetryMaxAttempts() {
        return indexInitialization_RetryMaxAttempts;
    }

    /**
     * set max attempts for index initialization during start up process
     * @param indexInitialization_RetryMaxAttempts 
     */
    public void setIndexInitialization_RetryMaxAttempts(int indexInitialization_RetryMaxAttempts) {
        this.indexInitialization_RetryMaxAttempts = indexInitialization_RetryMaxAttempts;
    }

    /**
     * get delay between atempts for index initialization during start up process
     * @return 
     */
    public long getIndexInitialization_RetryDelay_Millis() {
        return indexInitialization_RetryDelay_Millis;
    }

    /**
     * set delay between atempts for index initialization during start up process
     * @param indexInitialization_RetryDelay_Millis 
     */
    public void setIndexInitialization_RetryDelay_Millis(int indexInitialization_RetryDelay_Millis) {
        this.indexInitialization_RetryDelay_Millis = indexInitialization_RetryDelay_Millis;
    }
    
    /**
     * get file path for index settings (json file)
     * @return 
     */
    public String getIndexInitialization_SettingsFile() {
        return indexInitialization_SettingsFile;
    }

    /**
     * set file path for index settings (json file)
     * @param indexInitialization_SettingsFile 
     */
    public void setIndexInitialization_SettingsFile(String indexInitialization_SettingsFile) {
        this.indexInitialization_SettingsFile = indexInitialization_SettingsFile;
    }

    /**
     * //only used if indexInitialization_SettingsFile is not provided or not file not found, relativ path from src/main/resources, without leading slash
     * @return 
     */
    public String getIndexInitialization_SettingsFile_Resources() {
        return indexInitialization_SettingsFile_Resources;
    }

    /**
     * //only used if indexInitialization_SettingsFile is not provided or not file not found, relativ path from src/main/resources, without leading slash
     * @param indexInitialization_SettingsFile_Resources 
     */
    public void setIndexInitialization_SettingsFile_Resources(String indexInitialization_SettingsFile_Resources) {
        this.indexInitialization_SettingsFile_Resources = indexInitialization_SettingsFile_Resources;
    }
}
