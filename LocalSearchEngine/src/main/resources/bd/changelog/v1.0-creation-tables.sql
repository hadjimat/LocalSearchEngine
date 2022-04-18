--liquibase formatted sql
--changeset root:create-all-tables

CREATE TABLE IF NOT EXISTS _site
(
    id INT NOT NULL AUTO_INCREMENT,
    status ENUM('INDEXING', 'INDEXED', 'FAILED') NOT NULL,
    status_time DATETIME NOT NULL,
    last_error TEXT,
    url VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS _page
(
    id INT NOT NULL AUTO_INCREMENT,
    site_id INT NOT NULL,
    path TEXT NOT NULL,
    code INT NOT NULL,
    content MEDIUMTEXT NOT NULL,
    PRIMARY KEY(id),
    UNIQUE KEY `page_site_key` (path(500), site_id),
    FOREIGN KEY (site_id) REFERENCES _site(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS _field
(
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    selector VARCHAR(255) NOT NULL,
    weight FLOAT NOT NULL,
    PRIMARY KEY(id)
);

INSERT INTO _field (name, selector, weight)
VALUES ('title', 'title', 1.0),('body', 'body', 0.8);

CREATE TABLE IF NOT EXISTS _lemma
(
    id INT NOT NULL AUTO_INCREMENT,
    site_id INT NOT NULL,
    lemma VARCHAR(255) NOT NULL,
    frequency INT NOT NULL,
    PRIMARY KEY(id),
    UNIQUE KEY `lemma_site_key` (lemma, site_id),
    FOREIGN KEY (site_id) REFERENCES _site(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS _index
(
    id INT NOT NULL AUTO_INCREMENT,
    page_id INT NOT NULL,
    field_id INT NOT NULL,
    lemma_id INT NOT NULL,
    lemma_rank float NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (page_id) REFERENCES _page(id) ON DELETE CASCADE,
    FOREIGN KEY (field_id) REFERENCES _field(id),
    FOREIGN KEY (lemma_id) REFERENCES _lemma(id) ON DELETE CASCADE
);