-- noinspection SqlNoDataSourceInspectionForFile

CREATE TABLE IF NOT EXISTS users
(
    user_id    UUID         NOT NULL
        CONSTRAINT user_pkey
            PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(256) NOT NULL
        CONSTRAINT users_email_unique
            UNIQUE,
    password   VARCHAR(256) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS section
(
    section_id    UUID         NOT NULL
        CONSTRAINT section_pkey
            PRIMARY KEY,
    section_name  VARCHAR(50)  NOT NULL,
    section_title VARCHAR(250) NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS icons
(
    icon_id UUID         NOT NULL
        CONSTRAINT icon_pkey
            PRIMARY KEY,
    title   VARCHAR(50)  NOT NULL,
    link    VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS feed
(
    feed_id    UUID         NOT NULL
        CONSTRAINT feed_pkey
            PRIMARY KEY,
    feed_name  VARCHAR(50)  NOT NULL,
    feed_title VARCHAR(250) NOT NULL,
    feed_type  VARCHAR(5)   NOT NULL,
    xml_url    VARCHAR(250) NOT NULL,
    html_url   VARCHAR(250) NOT NULL,
    icon       UUID
        CONSTRAINT feed_icon_fkey
            REFERENCES icons
            ON UPDATE RESTRICT ON DELETE RESTRICT,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE  IF NOT EXISTS news
(
    news_id    UUID         NOT NULL
        CONSTRAINT news_pkey
            PRIMARY KEY,
    news_title  VARCHAR(50)  NOT NULL,
    news_description TEXT,
    link    VARCHAR(250) NOT NULL,
    guid   VARCHAR(250) NOT NULL,
    feed       UUID
        CONSTRAINT news_feed_fkey
            REFERENCES feed
            ON UPDATE RESTRICT ON DELETE RESTRICT,
    read_at TIMESTAMP    NOT NULL,
    published_at TIMESTAMP
)