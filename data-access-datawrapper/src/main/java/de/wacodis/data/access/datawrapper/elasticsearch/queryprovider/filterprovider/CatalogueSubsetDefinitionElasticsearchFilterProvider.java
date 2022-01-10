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
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import de.wacodis.dataaccess.model.CatalogueSubsetDefinition;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class CatalogueSubsetDefinitionElasticsearchFilterProvider implements SubsetDefinitionElasticsearchFilterProvider {
    
    private static final String URLFILTER_ATTRIBUTE = "catalougeUrl";
    private static final String RECORDIDFILTER_ATTRIBUTE = "recordRefId";

    @Override
    public List<QueryBuilder> buildFiltersForSubsetDefinition(AbstractSubsetDefinition subset) {
        if (subset instanceof CatalogueSubsetDefinition) {
            CatalogueSubsetDefinition catalogueSubset = (CatalogueSubsetDefinition) subset;
            List<QueryBuilder> filters = new ArrayList<>();

            QueryBuilder urlFilter = QueryBuilders.termQuery(URLFILTER_ATTRIBUTE, catalogueSubset.getServiceUrl());
            QueryBuilder recordIDFilter = QueryBuilders.termQuery(RECORDIDFILTER_ATTRIBUTE, catalogueSubset.getDatasetIdentifier());

            filters.add(urlFilter);
            filters.add(recordIDFilter);

            return filters;
        } else {
            throw new IllegalArgumentException("wrong type of SubsetDefinition, subset is of type " + subset.getClass().getSimpleName() + ", expected " + CatalogueSubsetDefinition.class.getSimpleName());
        }
    }

}
