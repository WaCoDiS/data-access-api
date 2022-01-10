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
