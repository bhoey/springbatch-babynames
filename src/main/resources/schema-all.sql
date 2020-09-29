DROP TABLE babynames IF EXISTS;

CREATE TABLE babynames  (
    id IDENTITY NOT NULL PRIMARY KEY,
    year SMALLINT,
    name VARCHAR(32),
    num INT
);