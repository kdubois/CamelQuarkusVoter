package com.kevindubois.cameldemo.model;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Entity
@Named("vote")
@ApplicationScoped
@RegisterForReflection
@Cacheable
public class Vote extends PanacheEntity{

    public String shortname;
    public String fullname;    
    public int counter;

    private Logger log = Logger.getLogger(Vote.class);

    public void updateCounter(String shortname) {
        

            int rowsUpdated = update("counter = counter + 1 WHERE shortname = ?1", shortname);
            if (rowsUpdated > 0 ){
                log.info("DB has been updated for " + shortname);
                
            } else {
                log.error("No rows updated for shortname " + shortname);
            } 
    }

    public List<Vote> orderedList() {
        return Vote.listAll(Sort.descending("counter"));
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id='" + id + '\'' +
                ", shortname=" + shortname + '\'' +
                ", fullname=" + fullname + '\'' +
                ", counter=" + counter +
                '}';
    }
}