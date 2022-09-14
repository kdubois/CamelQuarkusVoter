package com.kevindubois.cameldemo.model;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.Entity;
import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Entity
@Named("vote")
@ApplicationScoped
@RegisterForReflection
public class Vote extends PanacheEntity{

    public String stackname;
    public String fullname;    
    public int counter;

    private Logger log = Logger.getLogger(Vote.class);

    public void updateCounter(String stackname) {

            int rowsUpdated = update("counter = counter + 1 WHERE stackname = ?1", stackname);
            if (rowsUpdated > 0 ){
                log.info("DB has been updated for " + stackname);
                log.info(find("stackname = ?1", stackname).firstResult().toString());
            } else {
                log.error("No rows updated for stackname " + stackname);
            }              
   
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id='" + id + '\'' +
                ", stackname=" + stackname + '\'' +
                ", fullname=" + fullname + '\'' +
                ", counter=" + counter +
                '}';
    }
}