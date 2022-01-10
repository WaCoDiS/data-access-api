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
import de.wacodis.dataaccess.model.DwdSubsetDefinition;
import de.wacodis.dataaccess.model.WacodisProductSubsetDefinition;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author Arne
 */
public class WacodisProductDataSubsetDefinitionElasticsearchFilterProvider implements SubsetDefinitionElasticsearchFilterProvider {

    private final static String BACKENDTYPE_ATTRIBUTE = "serviceDefinition.backendType";
    private final static String PRODUCTTYPE_ATTRIBUTE = "productType";

    @Override
    public List<QueryBuilder> buildFiltersForSubsetDefinition(AbstractSubsetDefinition subset) {
        if (subset instanceof WacodisProductSubsetDefinition) {
            List<QueryBuilder> queries = new ArrayList<>();
            WacodisProductSubsetDefinition productSubset = (WacodisProductSubsetDefinition) subset;

            QueryBuilder collectionFilter = QueryBuilders.termQuery(BACKENDTYPE_ATTRIBUTE, productSubset.getBackendType());
            queries.add(collectionFilter);
            QueryBuilder productTypeFilter = QueryBuilders.termQuery(PRODUCTTYPE_ATTRIBUTE, productSubset.getProductType());
            queries.add(productTypeFilter);

            return queries;
        } else {
            throw new IllegalArgumentException("wrong type of SubsetDefinition, subset is of type " + subset.getClass().getSimpleName() + ", expected " + DwdSubsetDefinition.class.getSimpleName());
        }
    }

}
