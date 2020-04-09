package de.wacodis.data.access.datawrapper.resourceconverter;

import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class CopernicusDataEnvelopeConverterTest {

    private final static String SCIHUB_URL = "https://scihub.copernicus.eu/dhus/odata/v1/Products";
    private final static String CODE_DE_URL = "https://zipper.prod.cloud.code-de.org/download";

    @Test
    public void testConvertToResourceForCodeDe(){
        String id = "testId";
        CopernicusDataEnvelope dataEnv = new CopernicusDataEnvelope();
        dataEnv.setDatasetId(id);
        dataEnv.setPortal(CopernicusDataEnvelope.PortalEnum.CODE_DE);

        CopernicusDataEnvelopeConverter converter = new CopernicusDataEnvelopeConverter();
        AbstractResource resource = converter.convertToResource(dataEnv, null);

        assertEquals(CODE_DE_URL + "/" + id, resource.getUrl());
    }

    @Test
    public void testConvertToResourceForSciHub(){
        String id = "testId";
        CopernicusDataEnvelope dataEnv = new CopernicusDataEnvelope();
        dataEnv.setDatasetId(id);
        dataEnv.setPortal(CopernicusDataEnvelope.PortalEnum.SENTINEL_HUB);

        CopernicusDataEnvelopeConverter converter = new CopernicusDataEnvelopeConverter();
        AbstractResource resource = converter.convertToResource(dataEnv, null);

        assertEquals(SCIHUB_URL + "('" + id + "')/$value", resource.getUrl());
    }
}
