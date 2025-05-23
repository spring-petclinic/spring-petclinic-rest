CREATE TABLE IF NOT EXISTS vets (
                                    id         INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                    first_name TEXT,
                                    last_name  TEXT
);
CREATE INDEX ON vets (last_name);

CREATE TABLE IF NOT EXISTS specialties (
                                           id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                           name TEXT
);
CREATE INDEX ON specialties (name);

CREATE TABLE IF NOT EXISTS vet_specialties (
                                               vet_id       INT NOT NULL REFERENCES vets (id),
                                               specialty_id INT NOT NULL REFERENCES specialties (id),
                                               UNIQUE (vet_id, specialty_id)
);

CREATE TABLE IF NOT EXISTS types (
                                     id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     name TEXT
);
CREATE INDEX ON types (name);

CREATE TABLE IF NOT EXISTS owners (
                                      id         INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                      first_name TEXT,
                                      last_name  TEXT,
                                      address    TEXT,
                                      city       TEXT,
                                      telephone  TEXT
);
CREATE INDEX ON owners (last_name);

CREATE TABLE IF NOT EXISTS pets (
                                    id         INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                    name       TEXT,
                                    birth_date DATE,
                                    type_id    INT NOT NULL REFERENCES types (id),
                                    owner_id   INT REFERENCES owners (id)
);
CREATE INDEX ON pets (name);
CREATE INDEX ON pets (owner_id);

CREATE TABLE IF NOT EXISTS visits (
                                      id          INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                      pet_id      INT REFERENCES pets (id),
                                      visit_date  DATE,
                                      description TEXT
);
CREATE INDEX ON visits (pet_id);

CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(20) NOT NULL ,
  password VARCHAR(60) NOT NULL ,
  enabled boolean NOT NULL DEFAULT true ,
  CONSTRAINT pk_users PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS roles (
  id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  username varchar(20) NOT NULL,
  role varchar(20) NOT NULL,
  FOREIGN KEY (username) REFERENCES users (username),
  CONSTRAINT uni_username_role UNIQUE (role, username)
);
