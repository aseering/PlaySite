# Wiki schema
 
# --- !Ups

CREATE SEQUENCE wikipages_id_seq;
CREATE TABLE wikipages (
    id integer NOT NULL DEFAULT nextval('wikipages_id_seq'),
    uri varchar(256) not null,
    content text not null,
    author varchar(256) not null,
    create_date timestamp not null default now(),
    updater varchar(256),
    modified_date timestamp NOT NULL default now()
);
CREATE UNIQUE INDEX wikipages_uri_index ON wikipages (uri);
 
# --- !Downs
 
DROP INDEX wikipages_uri_index;
DROP TABLE wikipages;
DROP SEQUENCE wikipages_id_seq;

