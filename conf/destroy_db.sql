-- Gets rid of the database.  Undoes initial_setup.sql's work.
-- Run as `mysql -u root < initial_setup.sql`
-- DANGER:  WILL PERMANENTLY EAT ALL OF YOUR DATA!
DROP DATABASE test_db;
DROP USER 'test_db_user'@'localhost';
