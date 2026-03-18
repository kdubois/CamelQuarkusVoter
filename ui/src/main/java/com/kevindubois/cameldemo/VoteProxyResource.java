package com.kevindubois.cameldemo;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Proxy resource for frontend JavaScript calls to backend services.
 * This avoids CORS and mixed content issues by allowing the frontend to use relative URLs.
 */
@Path("/")
public class VoteProxyResource {

    @ConfigProperty(name = "ingester.url")
    String ingesterUrl;

    /**
     * Proxy endpoint for vote submission to ingester service.
     * Frontend JavaScript calls this with a relative URL to avoid mixed content errors.
     */
    @POST
    @Path("/favstackxform")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitVote(@FormParam("shortname") String shortname) {
        Client client = ClientBuilder.newClient();
        try {
            Form form = new Form();
            form.param("shortname", shortname);
            
            Response response = client.target(ingesterUrl)
                    .path("/favstackxform")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.form(form));
            
            return Response.status(response.getStatus())
                    .entity(response.readEntity(String.class))
                    .build();
        } finally {
            client.close();
        }
    }
}

