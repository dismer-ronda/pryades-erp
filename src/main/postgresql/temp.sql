alter table invoices add column ref_shipment bigint;
alter table invoices add constraint fk_invoices_shipment foreign key (ref_shipment) references shipments(id) on delete set null;

alter table invoices drop column departure_port;
alter table invoices drop column incoterms;

create table shipments_boxes 
(
	id bigint not null,

	ref_shipment bigint not null,
	ref_container bigint,

	box_type int not null,
	
	width real not null,						
	length real not null,						
	height real not null,
	
  	constraint pk_shipments_boxes primary key( id ),

  	constraint fk_shipments_boxes_shipment foreign key (ref_shipment) references shipments(id) on delete cascade,
  	constraint fk_shipments_boxes_container foreign key (ref_container) references shipments_boxes(id) on delete cascade
);

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

