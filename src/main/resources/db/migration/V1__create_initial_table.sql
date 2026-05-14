CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA spring_test;
CREATE TABLE IF NOT EXISTS users(
    id uuid PRIMARY KEY DEFAULT spring_test.uuid_generate_v4(),
    username text
);