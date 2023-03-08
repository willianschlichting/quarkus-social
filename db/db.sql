create database quarkus-social;

create table users (
	id bigserial not null primary key,
	name character varying(250) not null,
	age integer not null
);