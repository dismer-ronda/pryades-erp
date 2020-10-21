alter table shipments add ref_transporter bigint;
alter table shipments add ref_transporter_contact bigint;

alter table shipments add constraint fk_shipments_transporter foreign key (ref_transporter) references companies(id);
alter table shipments add constraint fk_shipments_transporter_contact foreign key (ref_transporter_contact) references companies_contacts(id);

alter table shipments alter column ref_transporter set not null;