package com.kevindubois.cameldemo.client;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.kevindubois.cameldemo.model.Vote;

@ApplicationScoped
@Path("/getresults")
@RegisterRestClient(configKey="processor-api")
public interface ProcessorRestClient {
    @GET
    List<Vote> getVotes();
}
