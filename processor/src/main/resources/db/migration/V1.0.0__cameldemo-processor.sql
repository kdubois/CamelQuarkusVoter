create sequence hibernate_sequence start 1 increment 1;

    create table CAMEL_MESSAGEPROCESSED (
       id int8 not null,
        createdAt timestamp,
        messageId varchar(255),
        processorName varchar(255),
        primary key (id)
    );

    create table Vote (
       id int8 not null,
        counter int4 not null,
        fullname varchar(255),
        log bytea,
        stackname varchar(255),
        primary key (id)
    );

    alter table if exists CAMEL_MESSAGEPROCESSED 
       add constraint UK3gjvyad9j4kd1xeog8xws0skp unique (processorName, messageId);
       
INSERT INTO vote (id, stackname, fullname, counter) VALUES      
(nextval('hibernate_sequence'), 'quarkus', 'Quarkus', 1),      
(nextval('hibernate_sequence'), 'micronaut', 'Micronaut', 0),      
(nextval('hibernate_sequence'), 'spring', 'Spring (Native)', 0),      
(nextval('hibernate_sequence'), 'microprofile', 'Microprofile', 0),      
(nextval('hibernate_sequence'), 'jakarta', 'Jakarta EE', 0),      
(nextval('hibernate_sequence'), 'other', 'Other', 0);

SELECT * FROM vote;