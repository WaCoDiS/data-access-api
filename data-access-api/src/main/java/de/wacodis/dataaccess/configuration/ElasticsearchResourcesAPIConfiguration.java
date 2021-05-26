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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Configuration
@ConfigurationProperties("spring.resources-api.elasticsearch")
@EnableConfigurationProperties
public class ElasticsearchResourcesAPIConfiguration {
    
    private String uri;
    private String indexName;
    private long requestTimeout_Millis;
    
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
}
