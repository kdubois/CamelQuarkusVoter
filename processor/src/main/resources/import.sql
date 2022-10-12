INSERT INTO vote (id, shortname, fullname, counter) VALUES 
    (nextval('hibernate_sequence'), 'intellij', 'IntelliJ', 0),      
    (nextval('hibernate_sequence'), 'vscode', 'VScode', 0),      
    (nextval('hibernate_sequence'), 'eclipse', 'Eclipse', 0),      
    (nextval('hibernate_sequence'), 'openshiftdevspaces', 'Openshift Dev Spaces', 0),   
    (nextval('hibernate_sequence'), 'vim', 'Vim', 0),   
    (nextval('hibernate_sequence'), 'other', 'Other', 0);