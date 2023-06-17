CREATE TABLE IF NOT EXISTS users
(
    id
    INT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY,
    name
    VARCHAR
(
    255
) NOT NULL,
    login VARCHAR
(
    255
) NOT NULL,
    email VARCHAR
(
    255
) NOT NULL,
    avatar VARCHAR
(
    255
),
    PRIMARY KEY
(
    id
)
    );
