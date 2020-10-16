alter table transactions drop column ref_source;

alter table transactions add column ref_target bigint;
alter table transactions add column transfer bigint;
alter table transactions add constraint fk_transactions_target foreign key (ref_target) references accounts(id);

alter table accounts add column credit real not null default 0;
