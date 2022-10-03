package com.kevindubois.cameldemo;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
