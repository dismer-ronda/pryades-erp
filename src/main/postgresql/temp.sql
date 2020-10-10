insert into rights (id, code) values (47, 'configuration.accounts');
insert into rights (id, code) values (48, 'configuration.accounts.add');
insert into rights (id, code) values (49, 'configuration.accounts.modify');
insert into rights (id, code) values (50, 'configuration.accounts.delete');
insert into profiles_rights (ref_profile, ref_right) values (0, 47);
insert into profiles_rights (ref_profile, ref_right) values (0, 48);
insert into profiles_rights (ref_profile, ref_right) values (0, 49);
insert into profiles_rights (ref_profile, ref_right) values (0, 50);

create table accounts
(
	id bigint not null,

	account_type int not null,
  	name varchar(128) not null,
  	number varchar(128),
  	balance real not null default 0,

	ref_company bigint not null,
	
  	constraint pk_accounts primary key( id ),
  	constraint fk_accounts_company foreign key (ref_company) references companies(id)
);
alter table accounts OWNER TO pryades;

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

