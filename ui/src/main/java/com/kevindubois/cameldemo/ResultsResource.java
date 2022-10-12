package com.kevindubois.cameldemo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.kevindubois.cameldemo.client.ProcessorRestClient;
import com.kevindubois.cameldemo.model.Vote;

import io.quarkus.logging.Log;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

@Path("/")
public class ResultsResource {

    @Inject
    Template results;

    @Inject
    @RestClient
    ProcessorRestClient processorRestClient;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance listVotes() {
        List<Vote> votes = new ArrayList<>();
        try {
            votes = processorRestClient.getVotes();
        } catch (Exception e) {
            Log.error(e);
        }
        return results.data("votes", votes);
    }
}