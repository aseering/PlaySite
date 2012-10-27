# User schema update: unique index on users.email
 
# --- !Ups

CREATE UNIQUE INDEX users_email_index ON users (email);
 
# --- !Downs
 
DROP INDEX users_email_index;

