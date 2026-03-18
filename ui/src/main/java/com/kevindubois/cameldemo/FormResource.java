package com.kevindubois.cameldemo;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
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

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

@Path("/form")
public class FormResource {

    @Inject
    Template form;

    @ConfigProperty(name = "ingester.url")
    String ingesterUrl;
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showForm() {
        // Use empty string for relative URL (same origin) to avoid CORS issues
        return form.data("formurl", "");
    }

    /**
     * Proxy endpoint for form submission to ingester service
     * This avoids CORS issues by allowing the frontend to POST to the same origin
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
