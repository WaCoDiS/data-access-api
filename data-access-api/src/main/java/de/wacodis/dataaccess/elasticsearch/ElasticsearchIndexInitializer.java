/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.dataaccess.elasticsearch;

import de.wacodis.dataaccess.configuration.ElasticsearchDataEnvelopesAPIConfiguration;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class ElasticsearchIndexInitializer implements ApplicationRunner {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ElasticsearchIndexInitializer.class);

    @Autowired
    private ElasticsearchDataEnvelopesAPIConfiguration elasticsearchConfig;

    @Autowired
    ElasticsearchClientFactory elasticsearchClientFactory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RestHighLevelClient elasticsearchClient = getElasticsearchClient();
        ElasticsearchIndexCreator indexCreator = new ElasticsearchIndexCreator(elasticsearchClient);

        InitializationStatus initStatus = new InitializationStatus();
        createRetryTemplate().execute(new RetryCallback<Void, Exception>() {
            @Override
            public Void doWithRetry(RetryContext context) throws Exception { //in case of ConncetException this method is called multiple times (according to retry configuration) 

                LOGGER.info("initializing elasticsearch index " + elasticsearchConfig.getIndexName() + ", server address: " + elasticsearchConfig.getUri() + ", attempt " + (context.getRetryCount() + 1) + " of " + elasticsearchConfig.getIndexInitialization_RetryMaxAttempts());

                try {
                    InputStream indexSettings = getIndexSettingsAsStream();
                    initStatus.isIndexAcknowledged = indexCreator.createIndex(elasticsearchConfig.getIndexName(), indexSettings, elasticsearchConfig.getRequestTimeout_Millis());
                } catch (Exception e) {
                    //only retry on ConnectException
                    if (IOException.class.isAssignableFrom(e.getClass())) {
                        throw e;
                    } else {
                        initStatus.exception = Optional.of(e); //memorize possible exception besides ConnectException, no retry in this case
                    }
                }

                return null;  //return statement to satisfy Void
            }
        });

        //throw occuring exception (besides ConnectException)
        if (initStatus.exception.isPresent()) {
            throw initStatus.exception.get();
        }

        //check if index creation was successful
        if (initStatus.isIndexAcknowledged) {
            LOGGER.info("successfully initialized elasticsearch index " + this.elasticsearchConfig.getIndexName() + ", server address: " + this.elasticsearchConfig.getUri());
        } else {
            throw new IOException("unable to initialize elasticsearch index, index was not acknowledged from host " + this.elasticsearchConfig.getUri());
        }
    }

    private RestHighLevelClient getElasticsearchClient() {
        return this.elasticsearchClientFactory.buildElasticsearchClient(this.elasticsearchConfig.getUri());
    }

    private RetryTemplate createRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(this.elasticsearchConfig.getRequestTimeout_Millis());

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(this.elasticsearchConfig.getIndexInitialization_RetryMaxAttempts());

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    private String removeLeadingSlash(String path) {
        if (path.startsWith("/") || path.startsWith("\\")) {
            return removeLeadingSlash(path.substring(1));
        } else {
            return path;
        }
    }

    private InputStream getIndexSettingsAsStream() {
        InputStream indexSettings;

        try {
            LOGGER.info("read elasticsearch index settings from file {}", this.elasticsearchConfig.getIndexInitialization_SettingsFile());
            indexSettings = new FileInputStream(this.elasticsearchConfig.getIndexInitialization_SettingsFile().trim());
        } catch (FileNotFoundException ex) {
            LOGGER.info("could not read elasticsearch index settings, file {} not found, use default index settings from application resources", this.elasticsearchConfig.getIndexInitialization_SettingsFile());
            String defaultIndexSettingsPath = removeLeadingSlash(this.elasticsearchConfig.getIndexInitialization_SettingsFile_Resources().trim());
            indexSettings = getClass().getClassLoader().getResourceAsStream(defaultIndexSettingsPath);
        }

        return indexSettings;
    }

    private class InitializationStatus {

        boolean isIndexAcknowledged = false;
        Optional<Exception> exception = Optional.empty();
    }
}
