/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
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

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class ElasticsearchClientFactory {

    /**
     * get new elasitcsearch client
     *
     * @param host
     * @param port
     * @param protocol
     * @return
     */
    public RestHighLevelClient buildElasticsearchClient(String host, int port, String protocol) {
        RestHighLevelClient elasticsearchClient = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, protocol)));
        return elasticsearchClient;
    }

    ;
    
    /**
     * get new elasticsearch client, assumes port 9200 and protocol http
     * @param host
     * @return 
     */
    public RestHighLevelClient buildElasticcsearchClient(String host) {
        return buildElasticsearchClient(host, 9200, "http");
    }

    /**
     * get new elasticsearch client, assumes port 9200
     *
     * @param host
     * @param protocol
     * @return
     */
    public RestHighLevelClient buildElasticcsearchClient(String host, String protocol) {
        return buildElasticsearchClient(host, 9200, protocol);
    }

    /**
     * get new ElasticsearchClient, derive host, port and protocol from uri (example: 'http://localhost:9200')
     * @param uri
     * @return 
     */
    public RestHighLevelClient buildElasticsearchClient(String uri){
        try{
        URL url = new URL(uri);
        String host = url.getHost();
        String protocol = url.getProtocol();
        int port = url.getPort();
        
        return buildElasticsearchClient(host, port, protocol);
        }catch(MalformedURLException ex){
            throw new IllegalArgumentException("cannot derive host, port or protocl from uri + " + uri, ex);
        }
    }
    
}
