INSERT INTO vote (id, stackname, fullname, counter) VALUES 
    (nextval('hibernate_sequence'), 'quarkus', 'Quarkus', 1), 
    (nextval('hibernate_sequence'), 'micronaut', 'Micronaut', 0), 
    (nextval('hibernate_sequence'), 'spring', 'Spring (Native)', 0), 
    (nextval('hibernate_sequence'), 'microprofile', 'Microprofile', 0), 
    (nextval('hibernate_sequence'), 'jakarta', 'Jakarta EE', 0), 
    (nextval('hibernate_sequence'), 'other', 'Other', 0);