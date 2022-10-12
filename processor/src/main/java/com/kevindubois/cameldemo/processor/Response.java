package com.kevindubois.cameldemo.processor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class Response {

    private String shortname;

    public String getShortname(){
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
}
