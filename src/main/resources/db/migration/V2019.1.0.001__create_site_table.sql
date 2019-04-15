-- Create site table for milestones publisher service
CREATE TABLE status(status_id VARCHAR(37) NOT NULL,
                   status VARCHAR(36) NOT NULL,
                   PRIMARY KEY (status_id));