# User schema
 
# --- !Ups

CREATE SEQUENCE user_id_seq;
CREATE TABLE users (
    id integer NOT NULL DEFAULT nextval('user_id_seq'),
    email varchar(256) not null,
    password varchar(256) not null
);
 
# --- !Downs
 
DROP TABLE users;
DROP SEQUENCE user_id_seq;
