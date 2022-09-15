package com.kevindubois.cameldemo.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Vote {

    public String stackname;

    /**
     * Default constructor required for Jackson serializer
     */
    public Vote() { }

    public Vote(String stackname) {
        this.stackname = stackname;
    }

    public String getStackname() {
        return stackname;
    }

    public void setStackname(String stackname) {
        this.stackname = stackname;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "stackname='" + stackname + '\'' +
                '}';
    }
}