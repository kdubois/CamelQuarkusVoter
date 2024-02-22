package com.kevindubois.cameldemo.client;

import java.util.List;

import com.kevindubois.cameldemo.Vote;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationScoped
@Path("/getresults")
@RegisterRestClient(configKey="processor-api")
public interface ProcessorRestClient {
    @GET
    List<Vote> getVotes();
}
