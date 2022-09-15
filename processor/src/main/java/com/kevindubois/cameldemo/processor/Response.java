package com.kevindubois.cameldemo.processor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class Response {

    private String stackname;

    public String getStackname(){
        return stackname;
    }

    public void setStackname(String stackname) {
        this.stackname = stackname;
    }
}
