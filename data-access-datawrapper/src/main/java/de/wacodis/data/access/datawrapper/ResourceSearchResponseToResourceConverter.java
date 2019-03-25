/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper;

import de.wacodis.dataaccess.model.AbstractResource;
import java.util.List;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public interface ResourceSearchResponseToResourceConverter {
    
    List<AbstractResource> convertToResource(ResourceSearchResponseContainer searchResponse);
    
}
