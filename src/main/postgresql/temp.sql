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


CREATE SEQUENCE seq_purchases 	START 202000001;

create table purchases 
(
	id bigint not null,

	purchase_type int not null, -- 1: producto para cliente, gasto empresa, etc						

	number int not null,						
	title varchar(128) not null,

	purchase_date bigint not null,
	register_date bigint not null,
	
	double net_price not null,
	double net_tax not null,				-- total de impuestos (por si hay más de uno o algún error de redondeo del proveedor)

	payed real not null default 0,

	ref_provider bigint not null,
	ref_operation bigint not null,

	invoice bytea,							-- documento pdf de la factura de compra
	invoice_number varchar(64) not null,

  	constraint pk_purchases primary key( id ),
  	constraint fk_purchases_provider foreign key (ref_provider) references companies(id),
  	constraint fk_purchases_operation foreign key (ref_operation) references operations(id)
);

create table pits 
(
	id bigint not null,

	number int not null,						

	pit_type int not null, -- 1: compra, nómina						
	pit_date bigint not null,

	title varchar(128) not null,
	
	net_value real not null,
	payed real not null,

	ref_person bigint not null,

  	constraint pk_pits primary key( id ),
  	constraint fk_pits_person foreign key (ref_person) references companies(id)
);
