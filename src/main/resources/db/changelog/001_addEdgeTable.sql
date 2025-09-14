--liquibase formatted sql
--changeset Christian Sterzl:addEdgeTable

DROP TABLE IF EXISTS edge;

CREATE TABLE edge (
    from_id integer,
    to_id integer,
    PRIMARY KEY(from_id, to_id)
);

CREATE INDEX ON edge (from_id);
CREATE INDEX ON edge (to_id);