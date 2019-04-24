/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
