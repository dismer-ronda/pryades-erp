create table companies_contacts
(
	id bigint not null,
  	
	ref_company bigint not null,
  	name varchar(128) not null,
  	email varchar(128),
  	phone varchar(64),

  	constraint pk_companies_contacts primary key( id )
);

alter table companies drop column contact_person;
alter table companies drop constraint uk_companies_email;

alter table quotations add column ref_contact bigint;
alter table quotations add column ref_user bigint;

alter table quotations add constraint fk_quotations_contact foreign key (ref_contact) references companies_contacts(id);
alter table quotations add constraint fk_quotations_user foreign key (ref_user) references users(id);

alter table shipments add column ref_consignee_contact bigint;
alter table shipments add column ref_notify_contact bigint;

alter table shipments add constraint fk_shipments_consignee_contact foreign key (ref_consignee_contact) references companies_contacts(id);
alter table shipments add constraint fk_shipments_notify_contact foreign key (ref_notify_contact) references companies_contacts(id);

alter table shipments add column ref_user bigint;
alter table shipments add constraint fk_shipments_user foreign key (ref_user) references users(id);

****************

alter table quotations alter column ref_contact set not null;
alter table quotations alter column ref_user set not null;

alter table shipments alter column ref_consignee_contact set not null;
alter table shipments alter column ref_notify_contact set not null;
alter table shipments alter column ref_user set not null;
