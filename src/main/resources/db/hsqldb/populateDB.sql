INSERT INTO vets VALUES (1, 'James', 'Carter');


INSERT INTO specialties VALUES (1, 'radiology');


INSERT INTO vet_specialties VALUES (1, 1);


INSERT INTO types VALUES (1, 'cat');


INSERT INTO owners VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');

INSERT INTO pets VALUES (1, 'Leo', '2010-09-07', 1, 1);

INSERT INTO visits VALUES (1, 1, '2013-01-01', 'rabies shot','Carter');


INSERT INTO users(username,password,enabled) VALUES ('admin','{noop}admin', true);

INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_OWNER_ADMIN');
INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_VET_ADMIN');
INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_ADMIN');
