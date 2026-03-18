package com.kevindubois.cameldemo;


import java.util.List;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import com.kevindubois.cameldemo.client.ProcessorRestClient;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

@Path("/")
public class ResultsResource {

    @Inject
    Template results;

    @Inject
    @RestClient
    ProcessorRestClient processorRestClient;

    @ConfigProperty(name = "ingester.url", defaultValue = "")
    String ingesterUrl;

    @ConfigProperty(name = "processor.url", defaultValue = "")
    String processorUrl;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance listVotes(@Context UriInfo uriInfo) {
        List<Vote> votes = processorRestClient.getVotes();

        // If URLs are not configured or are localhost, construct them from the current request
        String finalIngesterUrl = ingesterUrl;
        String finalProcessorUrl = processorUrl;
        
        boolean needsAutoDetect = ingesterUrl == null || ingesterUrl.isEmpty() ||
                                  ingesterUrl.contains("localhost") || ingesterUrl.contains("127.0.0.1");
        
        if (needsAutoDetect) {
            String scheme = uriInfo.getBaseUri().getScheme();
            String host = uriInfo.getBaseUri().getHost();
            
            // Construct URLs based on Knative service naming convention
            if (host.contains(".")) {
                // Extract domain from current host (e.g., cameldemo-ui-cameldemo.apps.example.com -> apps.example.com)
                String domain = host.substring(host.indexOf(".") + 1);
                finalIngesterUrl = scheme + "://cameldemo-ingester." + domain;
                finalProcessorUrl = scheme + "://cameldemo-processor." + domain;
            } else {
                // Fallback to configured values if we can't determine the domain
                finalIngesterUrl = ingesterUrl;
                finalProcessorUrl = processorUrl;
            }
        }

        return results.data("votes", votes)
                      .data("ingesterUrl", finalIngesterUrl)
                      .data("processorUrl", finalProcessorUrl);
    }
}