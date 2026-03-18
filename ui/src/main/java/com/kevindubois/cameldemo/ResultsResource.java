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

        // For frontend JavaScript, use empty string to indicate relative URLs (same origin)
        // This avoids CORS issues by using the UI service as a proxy
        String finalIngesterUrl = "";
        String finalProcessorUrl = "";
        
        // Check if we need to use external URLs (for direct backend calls)
        boolean needsExternalUrls = ingesterUrl != null && !ingesterUrl.isEmpty() &&
                                    !ingesterUrl.contains("localhost") && !ingesterUrl.contains("127.0.0.1") &&
                                    !ingesterUrl.contains(".svc.cluster.local");
        
        if (needsExternalUrls) {
            // Use configured external URLs if available
            finalIngesterUrl = ingesterUrl;
            finalProcessorUrl = processorUrl;
        } else {
            // Auto-detect external URLs based on current request
            String scheme = uriInfo.getBaseUri().getScheme();
            String host = uriInfo.getBaseUri().getHost();
            
            if (host.contains(".")) {
                // Extract domain from current host (e.g., cameldemo-ui-cameldemo.apps.example.com -> apps.example.com)
                String domain = host.substring(host.indexOf(".") + 1);
                finalIngesterUrl = scheme + "://cameldemo-ingester." + domain;
                finalProcessorUrl = scheme + "://cameldemo-processor." + domain;
            }
        }

        return results.data("votes", votes)
                      .data("ingesterUrl", finalIngesterUrl)
                      .data("processorUrl", finalProcessorUrl);
    }

    /**
     * Proxy endpoint for frontend JavaScript to call /getresults
     * This avoids CORS issues by allowing the frontend to use relative URLs
     */
    @GET
    @Path("/getresults")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Vote> getResults() {
        return processorRestClient.getVotes();
    }
}