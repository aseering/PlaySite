# Wiki schema
 
# --- !Ups

CREATE TABLE wikipages (
    id integer NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(id),
    uri varchar(256) not null,
    content text not null,
    author varchar(256) not null,
    create_date timestamp not null default now(),
    updater varchar(256),
    modified_date timestamp
);
DROP TRIGGER IF EXISTS wikipages_modified_date_trigger;
DELIMITER //
CREATE TRIGGER update_tablename_trigger BEFORE UPDATE ON wikipages
 FOR EACH ROW SET NEW.modified_date = NOW()
//
DELIMITER ;
CREATE UNIQUE INDEX wikipages_uri_index ON wikipages (uri);
 
# --- !Downs
 
DROP INDEX wikipages_uri_index;
DROP TABLE wikipages;


