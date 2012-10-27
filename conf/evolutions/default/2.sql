# User schema update: is_superuser column
 
# --- !Ups

ALTER TABLE users ADD COLUMN is_superuser BOOLEAN;
 
# --- !Downs
 
ALTER TABLE users DROP COLUMN is_superuser;

