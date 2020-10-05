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

