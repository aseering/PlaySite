# Wiki schema: add BEFORE INSERT trigger to initialize modified_date
 
# --- !Ups

CREATE TRIGGER wikipages_modified_date_insert_trigger BEFORE INSERT ON wikipages FOR EACH ROW SET NEW.modified_date = NOW();

# --- !Downs

DROP TRIGGER wikipages_modified_date_insert_trigger;
