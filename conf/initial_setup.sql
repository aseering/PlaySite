-- Run this as an admin user to create the database
-- Run as `mysql -u root < initial_setup.sql`
CREATE DATABASE test_db DEFAULT CHARACTER SET 'utf8' DEFAULT COLLATE 'utf8_bin';
CREATE USER 'test_db_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON test_db.* TO 'test_db_user'@'localhost';
