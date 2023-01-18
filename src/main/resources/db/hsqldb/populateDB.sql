INSERT INTO vets VALUES (1, 'Jannis', 'Herlt');
INSERT INTO vets VALUES (2, 'Sophie', 'Mießner');
INSERT INTO vets VALUES (3, 'Josephina', 'Ochynski');
INSERT INTO vets VALUES (4, 'Wednesday', 'Addams');
INSERT INTO vets VALUES (5, 'Steve', 'Harrington');
INSERT INTO vets VALUES (6, 'Carl', 'Meyer');

INSERT INTO specialties VALUES (1, 'drugs');
INSERT INTO specialties VALUES (2, 'histology');
INSERT INTO specialties VALUES (3, 'physiology');

INSERT INTO vet_specialties VALUES (1, 3);
INSERT INTO vet_specialties VALUES (2, 2);
INSERT INTO vet_specialties VALUES (3, 1);
INSERT INTO vet_specialties VALUES (4, 3);
INSERT INTO vet_specialties VALUES (5, 2);

INSERT INTO types VALUES (1, 'elephant');
INSERT INTO types VALUES (2, 'giraffe');
INSERT INTO types VALUES (3, 'parrot');
INSERT INTO types VALUES (4, 'chameleon');
INSERT INTO types VALUES (5, 'fish');
INSERT INTO types VALUES (6, 'mouse');
INSERT INTO types VALUES (7, 'guinea pig');
INSERT INTO types VALUES (8, 'rat');
INSERT INTO types VALUES (9, 'horse');
INSERT INTO types VALUES (10, 'cow');
INSERT INTO types VALUES (11, 'sheep');

INSERT INTO owners VALUES (1, 'Reiner', 'Wolf', '3519 Privet Drive', 'Green Bay', '6060385123');
INSERT INTO owners VALUES (2, 'Nancy', 'Wheeler', '641 Oak Street', 'Cologne', '6042978461');
INSERT INTO owners VALUES (3, 'Tom', 'Gibb', '2223 Hopewell Way', 'Surrey', '9856730922');
INSERT INTO owners VALUES (4, 'Luke', 'Wilson', '19353 West 16th Ave.', 'Oakville', '6088954321');
INSERT INTO owners VALUES (5, 'Peter', 'McTavish', '1100 Suite, 550 11th Ave.', 'London', '6078933348');
INSERT INTO owners VALUES (6, 'Jessica', 'Monroe', '119 Pegasus Way', 'Nebraska', '1122998472');
INSERT INTO owners VALUES (7, 'Henry', 'Hess', '3475 Mainway', 'Omaha', '8965700922');
INSERT INTO owners VALUES (8, 'Kim', 'Adeniran', '440 Main Street', 'Langley', '6667889215');
INSERT INTO owners VALUES (9, 'Mandy', 'Minau',  '3475 Mainway', 'New Brunswick', '98534907');
INSERT INTO owners VALUES (10, 'Jane', 'Smith', '4314 Highland Road East', 'Roseville', '60598264358');
INSERT INTO owners VALUES (11, 'Jenny', 'Freeman', '441 East Valley', 'Washington DC', '0983875699');

INSERT INTO pets VALUES (1, 'Lenny', '2011-10-06', 1, 11);
INSERT INTO pets VALUES (2, 'Benny', '2013-09-07', 2, 10);
INSERT INTO pets VALUES (3, 'Cherry', '2018-05-18', 3, 9);
INSERT INTO pets VALUES (4, 'Diamond', '2014-05-09', 4, 8);
INSERT INTO pets VALUES (5, 'Lilly', '2019-12-22', 5, 7);
INSERT INTO pets VALUES (6, 'Jerry', '2012-07-31', 6, 6);
INSERT INTO pets VALUES (7, 'Amalou', '2019-12-25', 9, 8);
INSERT INTO pets VALUES (8, 'Mila', '2013-11-14', 5, 9);
INSERT INTO pets VALUES (9, 'Luke', '2020-03-16', 11, 3);
INSERT INTO pets VALUES (10, 'Pelle', '2021-06-07', 10, 2);
INSERT INTO pets VALUES (11, 'Sock', '2012-04-29', 1, 11);
INSERT INTO pets VALUES (12, 'Neri', '2022-09-13', 7, 4);
INSERT INTO pets VALUES (13, 'Reeve', '2018-02-06', 2, 5);

INSERT INTO visits VALUES (1, 13,6, '2023-03-04', 'Ear Infection');
INSERT INTO visits VALUES (2, 12,5, '2023-03-15', 'Dental Cleaning');
INSERT INTO visits VALUES (3, 11,4, '2023-04-02', 'Vomiting');
INSERT INTO visits VALUES (4, 10,3,'2023-04-12', 'Skin Infection');
INSERT INTO visits VALUES (5, 9,2, '2023-05-05', 'Diet Plan');
INSERT INTO visits VALUES (6, 8,1, '2023-05-22', 'Dental Problems');
INSERT INTO visits VALUES (7, 7,4, '2023-06-01', 'Leg Pain');
INSERT INTO visits VALUES (8, 6,4, '2023-06-25', 'Skin Infection');
INSERT INTO visits VALUES (9, 5,4,'2023-07-08', 'Itchy Skin');
INSERT INTO visits VALUES (10, 4,4, '2023-07-27', 'Urinary Problems');
INSERT INTO visits VALUES (11, 3,4, '2023-08-11', 'Diarrhea');
INSERT INTO visits VALUES (12, 2,4, '2023-08-28', 'Back Pain');
INSERT INTO visits VALUES (13, 6,4, '2023-01-03', 'Ear Infections');
INSERT INTO visits VALUES (14, 1,4, '2022-11-17', 'Urinary Problems');
INSERT INTO visits VALUES (15, 9,4, '2022-05-25', 'Castration');
INSERT INTO visits VALUES (16, 3,5, '2015-08-01', 'Ultrasonic');
INSERT INTO visits VALUES (17, 12,6, '2018-10-30', 'Dental Cleaning');
INSERT INTO visits VALUES (18, 4,1, '2018-06-06', 'Vomiting');
INSERT INTO visits VALUES (19, 10,2, '2021-09-24', 'Urinary Problems');
INSERT INTO visits VALUES (20, 2, 3,'2020-01-16', 'Castration');
INSERT INTO visits VALUES (21, 8,4, '2022-02-04', 'Dental Cleaning');
INSERT INTO visits VALUES (22, 7,5, '2019-12-06', 'Vomiting');
INSERT INTO visits VALUES (23, 11,2, '2017-07-21', 'Diet Plan');
INSERT INTO visits VALUES (24, 5,3, '2022-04-29', 'Castration');


INSERT INTO users(username,password,enabled) VALUES ('admin','{noop}admin', true);

INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_OWNER_ADMIN');
INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_VET_ADMIN');
INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_ADMIN');
