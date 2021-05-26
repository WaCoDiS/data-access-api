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
package de.wacodis.data.access.datawrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 * @param <T>
 */
public class RequestResponse<T> {
    
    private final RequestResult status;
    private final Optional<T> responseObject;
    private final List<Exception> exceptions;

    public RequestResponse(RequestResult status, Optional<T> responseObject) {
        this.status = status;
        this.responseObject = responseObject;
        this.exceptions = new ArrayList<>();
    }
    
    public void addException(Exception e){
        this.exceptions.add(e);
    }
    
    /**
     * returns unmodifiable list of exceptions, list can be empty
     * @return 
     */
    public List<Exception> getExcetions(){
        return Collections.unmodifiableList(this.exceptions);
    }
   
    public RequestResult getStatus() {
        return status;
    }

    public Optional<T> getResponseObject() {
        return responseObject;
    }
}
