---------------------------------------------    
-- ref_profiles
---------------------------------------------
insert into profiles (id, description) values (0, 'Developer');
insert into profiles (id, description) values (1, 'Administrator');
insert into profiles (id, description) values (2, 'User');

---------------------------------------------    
-- rights
---------------------------------------------
insert into rights (id, code, description) values (1, 'login','Login');
insert into rights (id, code, description) values (2, 'configuration','Configuration');
insert into rights (id, code, description) values (3, 'configuration.users','Users');
insert into rights (id, code, description) values (4, 'configuration.users.add','Add user');
insert into rights (id, code, description) values (5, 'configuration.users.modify','Modify user');
insert into rights (id, code, description) values (6, 'configuration.users.delete','Delete user');

insert into profiles_rights (ref_profile, ref_right) values (0, 1);
insert into profiles_rights (ref_profile, ref_right) values (0, 2);
insert into profiles_rights (ref_profile, ref_right) values (0, 3);
insert into profiles_rights (ref_profile, ref_right) values (0, 4);
insert into profiles_rights (ref_profile, ref_right) values (0, 5);
insert into profiles_rights (ref_profile, ref_right) values (0, 6);
insert into profiles_rights (ref_profile, ref_right) values (0, 7);

insert into rights (id, code, description) values (8, 'configuration.parameters','Parameters');
insert into rights (id, code, description) values (9, 'configuration.parameters.modify','Modify parameter');
insert into profiles_rights (ref_profile, ref_right) values (0, 8);
insert into profiles_rights (ref_profile, ref_right) values (0, 9);

insert into rights (id, code, description) values (10, 'configuration.profiles','Profiles');
insert into rights (id, code, description) values (11, 'configuration.profiles.modify','Modify profile');
insert into profiles_rights (ref_profile, ref_right) values (0, 10);
insert into profiles_rights (ref_profile, ref_right) values (0, 11);

insert into rights (id, code, description) values (12, 'configuration.audits','Audit');
insert into profiles_rights (ref_profile, ref_right) values (0, 12);

insert into rights (id, code, description) values (13, 'configuration.reports','Reports');
insert into rights (id, code, description) values (14, 'configuration.reports.add','Add report');
insert into rights (id, code, description) values (15, 'configuration.reports.modify','Modify report');
insert into rights (id, code, description) values (16, 'configuration.reports.delete','Delete report');
insert into profiles_rights (ref_profile, ref_right) values (0, 13);
insert into profiles_rights (ref_profile, ref_right) values (0, 14);
insert into profiles_rights (ref_profile, ref_right) values (0, 15);
insert into profiles_rights (ref_profile, ref_right) values (0, 16);

insert into rights (id, code, description) values (17, 'configuration.tasks','Tasks');
insert into rights (id, code, description) values (18, 'configuration.tasks.add','Add task');
insert into rights (id, code, description) values (19, 'configuration.tasks.modify','Modify task');
insert into rights (id, code, description) values (20, 'configuration.tasks.delete','Delete task');
insert into profiles_rights (ref_profile, ref_right) values (0, 17);
insert into profiles_rights (ref_profile, ref_right) values (0, 18);
insert into profiles_rights (ref_profile, ref_right) values (0, 19);
insert into profiles_rights (ref_profile, ref_right) values (0, 20);

insert into rights (id, code, description) values (21, 'main.log','Show system log');
insert into profiles_rights (ref_profile, ref_right) values (0, 21);

insert into rights (id, code, description) values (22, 'configuration.companies','Empresas');
insert into rights (id, code, description) values (23, 'configuration.companies.add','Add company');
insert into rights (id, code, description) values (24, 'configuration.companies.modify','Modify company');
insert into rights (id, code, description) values (25, 'configuration.companies.delete','Delete company');
insert into profiles_rights (ref_profile, ref_right) values (0, 22);
insert into profiles_rights (ref_profile, ref_right) values (0, 23);
insert into profiles_rights (ref_profile, ref_right) values (0, 24);
insert into profiles_rights (ref_profile, ref_right) values (0, 25);

insert into rights (id, code, description) values (26, 'configuration.quotations','Cotizaciones');
insert into rights (id, code, description) values (27, 'configuration.quotations.add','Añadir cotización');
insert into rights (id, code, description) values (28, 'configuration.quotations.modify','Modificar cotización');
insert into rights (id, code, description) values (29, 'configuration.quotations.delete','Eliminar cotización');
insert into profiles_rights (ref_profile, ref_right) values (0, 26);
insert into profiles_rights (ref_profile, ref_right) values (0, 27);
insert into profiles_rights (ref_profile, ref_right) values (0, 28);
insert into profiles_rights (ref_profile, ref_right) values (0, 29);

insert into rights (id, code, description) values (30, 'configuration.invoices','Facturas');
insert into rights (id, code, description) values (31, 'configuration.invoices.add','Añadir factura');
insert into rights (id, code, description) values (32, 'configuration.invoices.modify','Modificar factura');
insert into rights (id, code, description) values (33, 'configuration.invoices.delete','Eliminar factura');
insert into profiles_rights (ref_profile, ref_right) values (0, 30);
insert into profiles_rights (ref_profile, ref_right) values (0, 31);
insert into profiles_rights (ref_profile, ref_right) values (0, 32);
insert into profiles_rights (ref_profile, ref_right) values (0, 33);

insert into rights (id, code, description) values (34, 'configuration.shipments','Envíos');
insert into rights (id, code, description) values (35, 'configuration.shipments.add','Añadir envío');
insert into rights (id, code, description) values (36, 'configuration.shipments.modify','Modificar envío');
insert into rights (id, code, description) values (37, 'configuration.shipments.delete','Eliminar envío');
insert into profiles_rights (ref_profile, ref_right) values (0, 34);
insert into profiles_rights (ref_profile, ref_right) values (0, 35);
insert into profiles_rights (ref_profile, ref_right) values (0, 36);
insert into profiles_rights (ref_profile, ref_right) values (0, 37);

insert into rights (id, code, description) values (38, 'configuration.tasks.dispatch','Dispatch task');
insert into profiles_rights (ref_profile, ref_right) values (0, 38);

insert into rights (id, code) values (39, 'configuration.operations');
insert into rights (id, code) values (40, 'configuration.operations.add');
insert into rights (id, code) values (41, 'configuration.operations.modify');
insert into rights (id, code) values (42, 'configuration.operations.delete');
insert into profiles_rights (ref_profile, ref_right) values (0, 39);
insert into profiles_rights (ref_profile, ref_right) values (0, 40);
insert into profiles_rights (ref_profile, ref_right) values (0, 41);
insert into profiles_rights (ref_profile, ref_right) values (0, 42);

insert into rights (id, code) values (43, 'configuration.purchases');
insert into rights (id, code) values (44, 'configuration.purchases.add');
insert into rights (id, code) values (45, 'configuration.purchases.modify');
insert into rights (id, code) values (46, 'configuration.purchases.delete');
insert into profiles_rights (ref_profile, ref_right) values (0, 43);
insert into profiles_rights (ref_profile, ref_right) values (0, 44);
insert into profiles_rights (ref_profile, ref_right) values (0, 45);
insert into profiles_rights (ref_profile, ref_right) values (0, 46);

insert into rights (id, code) values (47, 'configuration.accounts');
insert into rights (id, code) values (48, 'configuration.accounts.add');
insert into rights (id, code) values (49, 'configuration.accounts.modify');
insert into rights (id, code) values (50, 'configuration.accounts.delete');
insert into profiles_rights (ref_profile, ref_right) values (0, 47);
insert into profiles_rights (ref_profile, ref_right) values (0, 48);
insert into profiles_rights (ref_profile, ref_right) values (0, 49);
insert into profiles_rights (ref_profile, ref_right) values (0, 50);

---------------------------------------------    
-- users 
---------------------------------------------
insert into users (id, name, login, email, pwd, status, changed, retries, ref_profile) values (1,'Dismer Ronda Betancourt','dismer','dismer.ronda@pryades.com','0cc175b9c0f1b6a831c399e269772661',0,20200717, 0, 0, 1, 0);

---------------------------------------------    
-- parameters 
---------------------------------------------
insert into parameters (id, description, value, display_order) values (1, 'Login fails new password', '10', 1);
insert into parameters (id, description, value, display_order) values (2, 'Login fails block account', '20', 2);
insert into parameters (id, description, value, display_order) values (3, 'Password min size', '8', 3);
insert into parameters (id, description, value, display_order) values (4, 'Password valid time (days)', '365', 4);
insert into parameters (id, description, value, display_order) values (5, 'Mail host address', '', 5);
insert into parameters (id, description, value, display_order) values (6, 'Mail sender email', '', 6);
insert into parameters (id, description, value, display_order) values (7, 'Mail sender user', '', 7);
insert into parameters (id, description, value, display_order) values (8, 'Mail sender password', '', 8);
insert into parameters (id, description, value, display_order) values (9, 'Mail host port', '25', 9);
insert into parameters (id, description, value, display_order) values (10, 'Mail auth', 'false', 10);

insert into parameters (id, description, value, display_order) values (21, 'Duration of login/logout log events (days)', '180', 21);

insert into parameters (id, description, value, display_order) values (31, 'Proxy host for outgoing connections', '', 31);
insert into parameters (id, description, value, display_order) values (32, 'Proxy port for outgoing connections', '', 32);

insert into parameters (id, description, value, display_order) values (41, 'SOCKS5 Proxy host for outgoing connections', '', 41);
insert into parameters (id, description, value, display_order) values (42, 'SOCKS5 Proxy port for outgoing connections', '', 42);

insert into parameters (id, description, value, display_order) values (51, 'Enable password min size', '1', 51);
insert into parameters (id, description, value, display_order) values (52, 'Enable password must contains uppercase', '1', 52);
insert into parameters (id, description, value, display_order) values (53, 'Enable password must contains digit', '1', 53);
insert into parameters (id, description, value, display_order) values (54, 'Enable password must contains symbol', '1', 54);
insert into parameters (id, description, value, display_order) values (55, 'Enable password forbid contains id', '1', 55);
insert into parameters (id, description, value, display_order) values (56, 'Enable password forbid reuse passwords', '1', 56);

insert into parameters (id, description, value, display_order) values (61, 'Log default', 'E', 61);
insert into parameters (id, description, value, display_order) values (62, 'Log tasks', 'IUDE', 62);
insert into parameters (id, description, value, display_order) values (63, 'Log users', 'IUDE', 63);
insert into parameters (id, description, value, display_order) values (64, 'Log users defaults', 'IUDE', 64);
insert into parameters (id, description, value, display_order) values (65, 'Max number of rows to export', '1000', 65);

insert into parameters (id, description, value, display_order) values (66, 'Page size default', '25', 66);
