package com.kevindubois.cameldemo.model;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.transaction.Transactional;

import org.hibernate.annotations.common.util.impl.Log;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Entity
@Named("vote")
@ApplicationScoped
@RegisterForReflection
public class Vote extends PanacheEntity{

    public String name;
    public int counter;

    private Logger log = Logger.getLogger(Vote.class);

    public void updateCounter(String name) {
        update("counter = counter + 1 WHERE name = ?1", name);
        log.log(Level.INFO, this.listAll());
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id='" + id + '\'' +
                ", name=" + name + '\'' +
                ", counter=" + counter +
                '}';
    }
}