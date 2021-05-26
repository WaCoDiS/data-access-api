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
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.productbackend;

import de.wacodis.dataaccess.model.AbstractBackend;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author Arne
 */
public class AbstractBackendFilterProvider implements ProductBackendFilterProvider<AbstractBackend> {

    private final static String BACKENDTYPE_ATTRIBUTE = "serviceDefinition.backendType";

    @Override
    public List<QueryBuilder> getFiltersForBackend(AbstractBackend backend) {
        return Arrays.asList(QueryBuilders.termQuery(BACKENDTYPE_ATTRIBUTE, backend.getBackendType()));
    }

    @Override
    public Class<AbstractBackend> supportedBackendType() {
        return AbstractBackend.class;
    }

}
