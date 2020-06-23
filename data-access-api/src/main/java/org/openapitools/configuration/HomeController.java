package org.openapitools.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;


/**
 * Home redirection to OpenAPI api documentation
 */
@Controller
public class HomeController {

    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class.getName());

    @RequestMapping("/")
    public String index(@RequestHeader(value = "x-original-uri", required = false) Optional<String> xOriginalUri) {
        if (xOriginalUri.isPresent() && !xOriginalUri.get().isEmpty()) {
            String origWithoutTrailingSlash = xOriginalUri.get();
            if (origWithoutTrailingSlash.charAt(origWithoutTrailingSlash.length() - 1) == '/') {
                origWithoutTrailingSlash = origWithoutTrailingSlash.substring(0, origWithoutTrailingSlash.length() - 1);
            }

            String target = String.format("redirect:%s/swagger-ui.html", origWithoutTrailingSlash);
            LOG.info("Redirecting to swagger UI: {}", target);
            return target;
        }

        return "redirect:swagger-ui.html";
    }


}
