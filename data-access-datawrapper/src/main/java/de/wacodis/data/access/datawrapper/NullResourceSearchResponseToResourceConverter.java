/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper;

import de.wacodis.dataaccess.model.AbstractResource;
import java.util.ArrayList;
import java.util.List;

/**
 * implementation returns empty List
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class NullResourceSearchResponseToResourceConverter implements ResourceSearchResponseToResourceConverter{

    /**
     * returns empty list
     * @param searchResponse
     * @return 
     */
    @Override
    public List<AbstractResource> convertToResource(ResourceSearchResponseContainer searchResponse) {
        return new ArrayList<>();
    }

}
