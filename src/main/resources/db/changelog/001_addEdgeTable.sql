--liquibase formatted sql
--changeset Christian Sterzl:addEdgeTable

CREATE TABLE edge (
    from_id integer,
    to_id integer,
    PRIMARY KEY(from_id, to_id)
);