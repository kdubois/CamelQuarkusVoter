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
        shortname varchar(255),
        primary key (id)
    );

    alter table if exists CAMEL_MESSAGEPROCESSED 
       add constraint UK3gjvyad9j4kd1xeog8xws0skp unique (processorName, messageId);
       
INSERT INTO vote (id, shortname, fullname, counter) VALUES      
(nextval('hibernate_sequence'), 'intellij', 'Intellij', 1),      
(nextval('hibernate_sequence'), 'vscode', 'VScode', 0),      
(nextval('hibernate_sequence'), 'eclipse', 'Eclipse', 0),      
(nextval('hibernate_sequence'), 'openshiftdevspaces', 'Openshift Dev Spaces', 0),   
(nextval('hibernate_sequence'), 'vim', 'Vim', 0),   
(nextval('hibernate_sequence'), 'other', 'Other', 0);

SELECT * FROM vote;