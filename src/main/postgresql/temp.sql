create table banks
(
	id bigint not null,
  	
	ref_bank bigint not null,
  	name varchar(128) not null,
  	account varchar(128),
  	balance real not null default 0,

  	constraint pk_banks primary key( id ),
  	constraint fk_banks_bank foreign key (ref_bank) references companies(id)
);

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
