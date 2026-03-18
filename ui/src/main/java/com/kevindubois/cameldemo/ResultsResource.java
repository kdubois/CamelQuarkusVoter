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

        // For frontend JavaScript, always use relative URLs (empty string) to avoid CORS and mixed content issues
        // The UI service provides proxy endpoints (/favstackxform and /getresults) for frontend calls
        String finalIngesterUrl = "";
        String finalProcessorUrl = "";

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