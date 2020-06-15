INSERT INTO PERSON (id, created_on, updated_on, first_name, last_name, email, birthday, height, weight)
    VALUES (1, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', 'John', 'Doe', 'john.doe@acme.com', '1981-03-12 10:36:00', 174.0, 70.5);
INSERT INTO PERSON (id, created_on, updated_on, first_name, last_name, email, birthday, height, weight)
    VALUES (2, '2020-03-07T10:36:00Z', '2020-04-07T12:00:00Z', 'Jane', 'Doe', 'jane.doe@acme.com', '1981-11-26 12:30:00', 165.0, 68.0);
INSERT INTO PERSON (id, created_on, updated_on, first_name, last_name)
    VALUES (3, '2020-03-08T10:36:00Z', '2020-04-08T12:00:00Z', 'Bob', 'Nullos');

INSERT INTO PERSON_PHONE_NUMBERS (person_id, phone_number) VALUES (1, '+33612345678');
INSERT INTO PERSON_PHONE_NUMBERS (person_id, phone_number) VALUES (2, '+33687654321');

INSERT INTO PERSON_NICK_NAMES (person_id, nick_names) VALUES (1, 'Johnny');
INSERT INTO PERSON_NICK_NAMES (person_id, nick_names) VALUES (1, 'Joe');

INSERT INTO VEHICLE (id, created_on, updated_on, person_id, vehicle_type, brand, model) VALUES (1, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', 1, 'CAR', 'Renault', 'Clio');
INSERT INTO VEHICLE (id, created_on, updated_on, person_id, vehicle_type, brand, model) VALUES (2, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', 1, 'MOTORBIKE', 'Harley-Davidson', 'Livewire');

INSERT INTO ADDRESS (id, created_on, updated_on, city, country, street, zip_code) VALUES (1, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', 'Plaisir', 'FR', 'Rue des Peupliers', '78370');
INSERT INTO ADDRESS (id, created_on, updated_on, city, country, street, zip_code) VALUES (2, '2020-03-12T09:36:00Z', '2020-04-17T11:00:00Z', 'Le-Bois-Plage-En-Ré', 'FR', 'Rue des Petits Pois', '17051');

INSERT INTO PERSON_ADDRESS(person_id, address_id) VALUES (1, 1);
INSERT INTO PERSON_ADDRESS(person_id, address_id) VALUES (1, 2);
INSERT INTO PERSON_ADDRESS(person_id, address_id) VALUES (2, 1);
INSERT INTO PERSON_ADDRESS(person_id, address_id) VALUES (2, 2);

INSERT INTO JOB(id, person_id, created_on, updated_on, title, company, salary, hire_date) VALUES (1, 1, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', 'Lab Technician', 'Acme', '50000', '2019-09-01T09:00:00Z');
INSERT INTO JOB(id, person_id, created_on, updated_on, title, company, salary, hire_date) VALUES (2, 2, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', 'Commercial Fisherman', 'Fishing & Co', '60000', '2019-09-01T09:00:00Z');