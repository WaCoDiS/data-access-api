package de.wacodis.dataaccess.controller;

import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2018-09-14T15:57:11.999+02:00[Europe/Berlin]")
@Controller
@RequestMapping("${openapi.waCoDiSDataAccess.base-path:/dataAccess}")
public class InsertApiController implements InsertApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public InsertApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
}
