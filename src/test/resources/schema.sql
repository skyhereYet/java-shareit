DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                     name VARCHAR(255) NOT NULL,
                                     email VARCHAR(512) NOT NULL,
                                     CONSTRAINT pk_user PRIMARY KEY (id),
                                     CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                        description VARCHAR(512) NOT NULL,
                                        requestor_id BIGINT NOT NULL,
                                        created     TIMESTAMP WITHOUT TIME ZONE,
                                        FOREIGN KEY (requestor_id) REFERENCES users (id) ON UPDATE RESTRICT ON DELETE CASCADE,
                                        CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                     name VARCHAR(255) NOT NULL,
                                     description VARCHAR(512) NOT NULL,
                                     available BOOLEAN,
                                     owner_id BIGINT NOT NULL,
                                     request_id BIGINT,
                                     FOREIGN KEY (owner_id) REFERENCES users (id) ON UPDATE RESTRICT ON DELETE CASCADE,
                                     FOREIGN KEY (request_id) REFERENCES requests (id) ON UPDATE RESTRICT ON DELETE CASCADE,
                                     CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                        start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                        end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                        item_id BIGINT NOT NULL,
                                        booker_id BIGINT NOT NULL,
                                        status VARCHAR(128) NOT NULL,
                                        FOREIGN KEY (item_id) REFERENCES items (id) ON UPDATE RESTRICT ON DELETE CASCADE,
                                        FOREIGN KEY (booker_id) REFERENCES users (id) ON UPDATE RESTRICT ON DELETE CASCADE,
                                        CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                        text VARCHAR(2048) NOT NULL,
                                        item_id BIGINT NOT NULL,
                                        author_id BIGINT NOT NULL,
                                        created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                        FOREIGN KEY (item_id) REFERENCES items (id) ON UPDATE RESTRICT ON DELETE CASCADE,
                                        FOREIGN KEY (author_id) REFERENCES users (id) ON UPDATE RESTRICT ON DELETE CASCADE,
                                        CONSTRAINT pk_comment PRIMARY KEY (id)
)