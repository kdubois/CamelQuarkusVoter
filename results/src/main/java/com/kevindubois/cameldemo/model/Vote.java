package com.kevindubois.cameldemo.model;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity
public class Vote extends PanacheEntity{

    public String stackname;
    public String fullname;
    public int counter;
}