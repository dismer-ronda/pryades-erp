CREATE SEQUENCE gendat START 10000;
alter sequence gendat OWNER TO pryades;

CREATE SEQUENCE gencfg START 1000;
alter sequence gencfg OWNER TO pryades;

CREATE SEQUENCE seq_quotations 	START 200001;
alter sequence seq_quotations OWNER TO pryades;

CREATE SEQUENCE seq_invoices START 200001;
alter sequence seq_invoices OWNER TO pryades;

CREATE SEQUENCE seq_shipments START 20001;
alter sequence seq_shipments OWNER TO pryades;

CREATE SEQUENCE seq_purchases START 202000001;
alter sequence seq_purchases OWNER TO pryades;

-- user profiles
create table profiles (
	id bigint not null,
	description varchar(128) not null,
	
	constraint pk_profiles primary key(id),
	constraint uk_profiles_description unique (description)	
);
alter table profiles OWNER TO pryades;
	
-- user rights
create table rights (
	id bigint not null,
	code varchar(128),
	display_order integer,
	
	constraint pk_rights primary key(id),
	constraint uk_rights_code unique (code)	
);
alter table rights OWNER TO pryades;
	
create table profiles_rights (
    ref_profile bigint not null,
    ref_right bigint not null,
    
    constraint pk_profiles_rights primary key(ref_profile, ref_right),
    
    constraint fk_profiles_rights_profile foreign key (ref_profile) references profiles(id),
    constraint fk_profiles_rights_right foreign key (ref_right) references rights(id)		
    );
alter table profiles_rights OWNER TO pryades;

-- users
create table users (
    id bigint not null,
    
    name varchar(128) not null,   			-- user name
    login varchar(64) not null,    			-- user login
    email varchar(128) not null,    		-- user email
	pwd varchar(32) not null,       		-- password hash

	status integer not null,        		-- 0=logged -- 1=Never logged -- 2=Request change -- 3=password expired -- 4=too manu retries
	changed integer not null,        		-- password changed date
	retries integer not null,      			-- number of retries failed

   	ref_profile bigint not null,   			-- user's profile

	signature bytea,						-- user signature file

	constraint pk_users primary key(id),
    constraint fk_users_profile foreign key (ref_profile) references profiles(id),
	constraint uk_users_code unique(login),
	constraint uk_users_email unique(email)
    );
alter table users OWNER TO pryades;

-- plattform parameters
create table parameters (
	id bigint not null,
    
	description varchar(256) not null,					-- name of the parameter	
    value varchar(256) not null,						-- value of the parameter
	display_order integer not null default(0),
	
	constraint pk_parameters primary key(id),
	constraint uk_parameters unique(description)
);
alter table parameters OWNER TO pryades;
create index ix_parameters_description on parameters(description);

-- user_defaults
create table user_defaults (
	id bigint not null,
    
	data_key varchar(64) not null,		-- key for data	
	data_value text,					-- value for data	
	
	ref_user bigint not null,			-- user	

	constraint pk_user_defaults primary key(id),
	constraint uk_user_defaults_key unique(ref_user, data_key),
	constraint fk_user_defaults_user foreign key (ref_user) references users(id)
);
alter table user_defaults OWNER TO pryades;

-- audits
create table audits (
	id bigint not null,
    
	audit_date bigint not null,					-- datetime of audit					  
	audit_type integer not null,				-- type of event: 0: login, 1: logout, 2: create register, etc
	audit_details varchar(256),					-- text with audit details
	duration integer not null default 30,		-- duration of event
	ref_user bigint,							-- usuario asociado
	
    constraint pk_audits primary key(id),
	constraint fk_audits_user foreign key (ref_user) references users(id)
);
alter table audits OWNER TO pryades;
create index ix_audits_date on audits(audit_date);
create index ix_audits_type on audits(audit_type);
create index ix_audits_user on audits(ref_user);

-- tasks
create table tasks (
	id bigint not null,

	description varchar (64) not null,			-- description of the task

	timezone varchar(128) not null,
	language varchar(16) not null,
	month varchar(64),							-- month to create the report (* for all months)
	day varchar(64),							-- day to create the report (* for all days, number for specific day or MON, TUE, WED for week days)
	hour varchar(64),							-- hour to create the report (* for all hours)
	times integer not null, 					-- number of times task is executed: 0 for infinite number 
	
	clazz integer not null,						-- class of report
	details text,								-- text with report details
	
	system integer not null, 					-- system task 

	constraint pk_tasks primary key(id)
);
alter table tasks OWNER TO pryades;
create index ix_tasks_clazz on tasks(clazz);
create index ix_tasks_system on tasks(system);

CREATE TABLE companies
(
	id bigint not null,
  	
	alias varchar(64) not null,
  	name varchar(128) not null,
  	tax_id varchar(64),
  	address text,
  	email varchar(128),
  	phone varchar(64),
	taxable boolean not null default false, 	
	language varchar(16) not null,
	signature boolean not null default false, 	
	type_company integer not null default 1,			-- 1: provider, 2: customer, 3:transporter, 4: insurer, 5: bank 	
	logo bytea,

  	constraint pk_companies primary key( id ),

  	constraint uk_companies_alias unique(alias)
);
alter table companies OWNER TO pryades;
create index ix_companies_alias on companies(alias);
create index ix_companies_tax_id on companies(tax_id);

create table quotations 
(
	id bigint not null,

	title varchar(64 not null),
	
	number int not null,						
	quotation_date bigint not null,						
	validity int not null,
	
	ref_customer bigint not null,

	reference_request varchar(64),
	reference_order varchar(64),
	
	packaging varchar(64) not null,
	delivery varchar(64) not null,
	warranty varchar(64) not null,
	payment_terms text not null,
	tax_rate real not null default 0,
	status int not null default 0,
	
	weight real not null default 0,
	volume  real not null default 0,

	ref_contact bigint not null,
	ref_user bigint not null,

  	constraint pk_quotations primary key( id ),

  	constraint fk_quotations_customer foreign key (ref_customer) references companies(id),
  	constraint fk_quotations_contact foreign key (ref_contact) references companies_contacts(id),
  	constraint fk_quotations_user foreign key (ref_user) references users(id),
  	constraint uk_quotations_number unique(number)
);
alter table quotations OWNER TO pryades;
create index ix_quotations_reference_request on quotations(reference_request);
create index ix_quotations_reference_order on quotations(reference_order);

create table quotations_deliveries 
(
	id bigint not null,

	ref_quotation bigint not null,

	departure_date bigint not null,
	departure_port varchar(64),
	arrival_port varchar(64),
	
	incoterms varchar(32),
	cost real not null,
	
	free_delivery boolean not null, 	

  	constraint pk_quotations_deliveries primary key( id ),

  	constraint fk_quotations_deliveries_quotation foreign key (ref_quotation) references quotations(id) on delete cascade
);
alter table quotations_deliveries OWNER TO pryades;

create table quotations_lines
(
	id bigint not null,

	ref_quotation bigint not null,
	line_order int not null default 0,
	
	origin varchar(64),
	reference varchar(64),
	title varchar (64) not null,
	description text,

	cost real not null,
	real_cost real not null,
	margin real not null,
	tax_rate real not null default 0,

	ref_provider bigint,

  	constraint pk_quotations_lines primary key( id ),
  	
  	constraint fk_quotations_lines_quotation foreign key (ref_quotation) references quotations(id) on delete cascade,
  	constraint fk_quotations_lines_provider foreign key (ref_provider) references companies(id) on delete set null
);
alter table quotations_lines OWNER TO pryades;
create index ix_quotations_lines_title on quotations_lines(title);

create table quotations_lines_deliveries
(
	id bigint not null,

	ref_quotation_delivery bigint not null,
	ref_quotation_line bigint not null,
	
	quantity int not null,

  	constraint pk_quotations_lines_deliveries primary key( id ),
  	
  	constraint fk_quotations_lines_deliveries_quotation_delivery foreign key (ref_quotation_delivery) references quotations_deliveries(id) on delete cascade,
  	constraint fk_quotations_lines_deliveries_quotation_line foreign key (ref_quotation_line) references quotations_lines(id) on delete cascade
);
alter table quotations_lines_deliveries OWNER TO pryades;

create table quotations_attachments 
(
	id bigint not null,

	ref_quotation bigint not null,

	title varchar(64),
	format varchar(64),
	
	data bytea,
	
  	constraint pk_quotations_attachments primary key( id ),

  	constraint fk_quotations_attachments_quotation foreign key (ref_quotation) references quotations(id) on delete cascade
);
alter table quotations_attachments OWNER TO pryades;

create table shipments 
(
	id bigint not null,

	number int not null,						
	
	shipment_date bigint not null,						
	departure_date bigint,						

	incoterms varchar(32) not null,

	description text not null,
	
	departure_port varchar(64) not null,
	arrival_port varchar(64) not null,

	carrier varchar(64),
	tracking varchar(64),
	
	ref_consignee bigint not null,
	ref_notify bigint not null,
	ref_transporter bigint not null,

	status int not null default 0,
	
	ref_consignee_contact bigint not null,
	ref_notify_contact bigint not null,
	ref_transporter_contact bigint not null,

	ref_user bigint,

  	constraint pk_shipments primary key( id ),

  	constraint fk_shipments_consignee foreign key (ref_consignee) references companies(id),
  	constraint fk_shipments_notify foreign key (ref_notify) references companies(id),
	constraint fk_shipments_consignee_contact foreign key (ref_consignee_contact) references companies_contacts(id),
	constraint fk_shipments_notify_contact foreign key (ref_notify_contact) references companies_contacts(id),
  	constraint fk_shipments_transporter foreign key (ref_transporter) references companies(id),
	constraint fk_shipments_transporter_contact foreign key (ref_transporter_contact) references companies_contacts(id),
	constraint fk_shipments_user foreign key (ref_user) references users(id)
);
alter table shipments OWNER TO pryades;

create table invoices 
(
	id bigint not null,

	ref_quotation bigint not null,
	ref_shipment bigint,

	title varchar(128) not null,
	payment_terms text not null,
	
	number int not null,						
	invoice_date bigint not null,						

	transport_cost real not null,

	free_delivery boolean not null, 	
	collected real not null default 0,

  	constraint pk_invoices primary key( id ),

  	constraint fk_invoices_quotation foreign key (ref_quotation) references quotations(id),
  	constraint fk_invoices_shipment foreign key (ref_shipment) references shipments(id) on delete set null
);
alter table invoices OWNER TO pryades;

create table invoices_lines
(
	id bigint not null,

	ref_invoice bigint not null,
	ref_quotation_line bigint not null,

	quantity int not null,

  	constraint pk_invoices_lines primary key( id ),
  	
  	constraint fk_invoices_lines_invoice foreign key (ref_invoice) references invoices(id) on delete cascade,
  	constraint fk_invoices_lines_quotation_line foreign key (ref_quotation_line) references quotations_lines(id)
);
alter table invoices_lines OWNER TO pryades;

create table shipments_boxes 
(
	id bigint not null,

	ref_shipment bigint,
	ref_container bigint,

	box_type int not null,
	label varchar(32) not null,
	label_type int not null default 0,
	
	width real not null,						
	length real not null,						
	height real not null,
	
  	constraint pk_shipments_boxes primary key( id ),

  	constraint fk_shipments_boxes_shipment foreign key (ref_shipment) references shipments(id) on delete cascade,
  	constraint fk_shipments_boxes_container foreign key (ref_container) references shipments_boxes(id) on delete cascade
);
alter table shipments_boxes OWNER TO pryades;

create table shipments_boxes_lines 
(
	id bigint not null,

	ref_box bigint not null,
	ref_invoice_line bigint not null,
	
	quantity int not null,
	
	net_weight real not null,
	gross_weight real not null, 
	
	constraint pk_shipments_boxes_lines primary key( id ),

  	constraint fk_shipments_boxes_lines_box foreign key (ref_box) references shipments_boxes(id) on delete cascade,
  	constraint fk_shipments_boxes_lines_invoice_line foreign key (ref_invoice_line) references invoices_lines(id)
);
alter table shipments_boxes_lines OWNER TO pryades;


create table users_companies 
(
	id bigint not null,

	ref_user bigint not null,
	ref_company bigint not null
	
	constraint pk_users_companies primary key( id ),

  	constraint fk_users_companies_user foreign key (ref_user) references users(id) on delete cascade,
  	constraint fk_users_companies_company foreign key (ref_company) references companies(id)
);
alter table users_companies OWNER TO pryades;

create table companies_contacts
(
	id bigint not null,
  	
	ref_company bigint not null,
  	name varchar(128) not null,
  	email varchar(128),
  	phone varchar(64),

  	constraint pk_companies_contacts primary key( id ),
  	constraint fk_companies_contacts_company foreign key (ref_company) references companies(id) on delete cascade
);
alter table companies_contacts OWNER TO pryades;

create table operations
(
	id bigint not null,

	status int not null,						
	title varchar(128) not null,

	ref_quotation bigint,

  	constraint pk_operations primary key( id ),
  	constraint uk_operations_quotation unique(ref_quotation),
  	constraint fk_operations_quotation foreign key (ref_quotation) references quotations(id)
);
alter table operations OWNER TO pryades;

create table purchases 
(
	id bigint not null,

	purchase_type int not null, -- 1: producto para cliente, gasto empresa, etc						

	number int not null,						
	title varchar(128) not null,
	description text,

	purchase_date bigint not null,
	register_date bigint not null,

	net_price real not null,
	net_tax real not null,					-- total de impuestos (por si hay más de uno o algún error de redondeo del proveedor)
	net_retention real not null,			-- total de retenciones (por si hay más de uno o algún error de redondeo del proveedor)

	payed real not null default 0,
	
	status int not null,

	ref_operation bigint not null,
	ref_buyer bigint not null,
	ref_provider bigint not null,
	ref_contact bigint,

	invoice bytea,							-- documento pdf de la factura de compra
	invoice_number varchar(64),
	
	quotation bytea,						-- documento pdf de la oferta
	quotation_number varchar(64);

	payment bytea,						    -- documento pdf de los pagos

  	constraint pk_purchases primary key( id ),
  	constraint fk_purchases_provider foreign key (ref_provider) references companies(id),
  	constraint fk_purchases_contact foreign key (ref_contact) references companies_contacts(id),
  	constraint fk_purchases_operation foreign key (ref_operation) references operations(id),
  	constraint fk_purchases_buyer foreign key (ref_buyer) references users(id)
);
alter table purchases OWNER TO pryades;

create table accounts
(
	id bigint not null,

	account_type int not null,
  	name varchar(128) not null,
  	number varchar(128),
  	credit real not null,

	ref_company bigint not null,
	
  	constraint pk_accounts primary key( id ),
  	constraint fk_accounts_company foreign key (ref_company) references companies(id)
);
alter table accounts OWNER TO pryades;

create table transactions 
(
	id bigint not null,

	transaction_date bigint not null,
	transaction_type bigint not null,

	amount real not null,
  	balance	real not null,
  	
	description varchar(64),

	ref_purchase bigint,			-- id de la compra
	ref_invoice bigint,				-- id de la venta
	ref_target bigint,				-- id de la cuenta asociada si es una transferencia
	transfer bigint,				-- id de la transaccion  

  	ref_account bigint not null,

  	constraint pk_transactions primary key( id ),
  	constraint fk_transactions_purchase foreign key (ref_purchase) references purchases(id),
  	constraint fk_transactions_invoice foreign key (ref_invoice) references invoices(id),
  	constraint fk_transactions_target foreign key (ref_target) references accounts(id),
  	constraint fk_transactions_account foreign key (ref_account) references accounts(id)
);
alter table transactions OWNER TO pryades;

create table shipments_attachments 
(
	id bigint not null,

	ref_shipment bigint not null,

	title varchar(64),
	
	data bytea,
	
  	constraint pk_shipments_attachments primary key( id ),

  	constraint fk_shipments_attachments_shipment foreign key (ref_shipment) references shipments(id) on delete cascade
);
alter table shipments_attachments OWNER TO pryades;
