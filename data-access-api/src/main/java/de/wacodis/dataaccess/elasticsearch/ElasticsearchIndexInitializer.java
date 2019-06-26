/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.dataaccess.elasticsearch;

import de.wacodis.dataaccess.configuration.ElasticsearchDataEnvelopesAPIConfiguration;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class ElasticsearchIndexInitializer implements ApplicationRunner {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ElasticsearchIndexInitializer.class);
    
    private final static String SETTINGSFILENAME = "elasticsearch_indexsettings.json";
    
    @Autowired
    private ElasticsearchDataEnvelopesAPIConfiguration elasticsearchConfig;
    
    @Autowired
    ElasticsearchClientFactory elasticsearchClientFactory;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("initializing elasticsearch index " + this.elasticsearchConfig.getIndexName() + ", server address: " + this.elasticsearchConfig.getUri());
        
        RestHighLevelClient elasticsearchClient = getElasticsearchClient();
        ElasticsearchIndexCreator indexCreator = new ElasticsearchIndexCreator(elasticsearchClient); 
        
        boolean isIndexAcknowledged = indexCreator.createIndex(this.elasticsearchConfig.getIndexName(), getSettingsFile(), this.elasticsearchConfig.getRequestTimeout_Millis());
        
        if(isIndexAcknowledged){
            LOGGER.info("successfully initialized elasticsearch index " + this.elasticsearchConfig.getIndexName() + ", server address: " + this.elasticsearchConfig.getUri());
        }
    }
    
    
    private RestHighLevelClient getElasticsearchClient(){
        return this.elasticsearchClientFactory.buildElasticsearchClient(this.elasticsearchConfig.getUri());
    }
    
    private File getSettingsFile() throws URISyntaxException{
        //get file from resources
        LOGGER.debug("searching for elasticsearch index settings file " + SETTINGSFILENAME + " in resources");
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL settingsFileURL = classloader.getResource(SETTINGSFILENAME);
        File settingsFile = new File(settingsFileURL.toURI());
        LOGGER.debug("found lasticsearch index settings file" + settingsFile.getAbsolutePath());
        
        return settingsFile;
    }
}
