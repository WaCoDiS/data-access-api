/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.dataaccess.configuration;

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
    private String uri;
    private String indexName;
    private String type;
    private long requestTimeout_Millis;
    private int indexInitialization_RetryMaxAttempts = 1;
    private long indexInitialization_RetryDelay_Millis;
    private String indexInitialization_SettingsFile;

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
}
