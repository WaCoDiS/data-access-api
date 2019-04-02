/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper;

import java.util.Optional;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 * @param <T>
 */
public class RequestResponse<T> {
    
    private final RequestResult status;
    private final Optional<T> responseObject;

    public RequestResponse(RequestResult status, Optional<T> responseObject) {
        this.status = status;
        this.responseObject = responseObject;
    }
   
    public RequestResult getStatus() {
        return status;
    }

    public Optional<T> getResponseObject() {
        return responseObject;
    }
}
