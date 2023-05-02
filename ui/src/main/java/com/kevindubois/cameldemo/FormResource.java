package com.kevindubois.cameldemo;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

@Path("/form")
public class FormResource {

    @Inject
    Template form;

    @ConfigProperty(name = "ingester.url")
    String url;
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showForm() {
        return form.data("formurl", url);
    }
}
