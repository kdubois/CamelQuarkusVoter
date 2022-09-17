package com.kevindubois.cameldemo;


import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;


@Path("/result")
public class ResultsResource {

    @Inject
    Template results;


    @GET
    @Consumes(MediaType.TEXT_HTML) 
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance listVotes() {
        return results.data(null);
    }
}