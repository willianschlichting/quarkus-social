create database quarkus-social;

create table users (
	id bigserial not null primary key,
	name character varying(250) not null,
	age integer not null
);

create table posts (
	id bigserial not null primary key,
	post_text text not null,
	date_time timestamp not null,
	user_id bigint not null references users(id)
);

create table followers(
	id bigserial not null primary key,
	user_id bigint not null references users(id),
	follower_id bigint not null references users(id),
);