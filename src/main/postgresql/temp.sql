alter table purchases add column net_retention real not null default 0;

create table pits 
(
	id bigint not null,

	number int not null,						

	pit_type int not null, -- 1: compra, nómina						
	pit_date bigint not null,

	title varchar(128) not null,
	description text,
	
	net_value real not null,
	payed real not null,

	ref_person bigint not null,

  	constraint pk_pits primary key( id ),
  	constraint fk_pits_person foreign key (ref_person) references companies(id)
);
alter table pits OWNER TO pryades;

