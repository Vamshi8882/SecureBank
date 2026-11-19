CREATE DATABASE bankdb;
SHOW DATABASES;
USE bankdb;
SHOW TABLES;
SELECT * FROM accounts;
SELECT * FROM kyc_records;
SELECT * FROM loan_application;
SELECT * FROM users;
SELECT *
FROM loan_applications
ORDER BY id DESC;