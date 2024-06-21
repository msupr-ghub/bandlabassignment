-- Connect to the PostgreSQL default database
\c postgres;

-- Create the database
CREATE DATABASE test;

-- Create the user with a password
CREATE USER test WITH PASSWORD 'test';

-- Grant all privileges on the database to the user
GRANT ALL PRIVILEGES ON DATABASE test TO test;