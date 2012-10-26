# Tasks schema
 
# --- !Ups

ALTER TABLE users ADD COLUMN is_superuser BOOLEAN;
 
# --- !Downs
 
ALTER TABLE users DROP COLUMN is_superuser;

