/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.dataaccess.util;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ErrorFactory {

    private final static int DEFAULT_STATUSCODE = 500;
    
    /**
     * build error object with default status code 500
     *
     * @param errorMessage
     * @return
     */
    public static de.wacodis.dataaccess.model.Error getErrorObject(String errorMessage) {
        return getErrorObject(DEFAULT_STATUSCODE, errorMessage);
    }

    public static de.wacodis.dataaccess.model.Error getErrorObject(int code, String errorMessage) {
        de.wacodis.dataaccess.model.Error error = new de.wacodis.dataaccess.model.Error();
        error.setMessage(errorMessage);
        error.setCode(code);
        
        return error;
    }

}
