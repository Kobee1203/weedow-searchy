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

INSERT INTO CHARACTERISTIC_MAPPING(person_id, characteristic_name, value) VALUES (1, 'eyes', 'blue');
INSERT INTO CHARACTERISTIC_MAPPING(person_id, characteristic_name, value) VALUES (1, 'hair', 'brown');
INSERT INTO CHARACTERISTIC_MAPPING(person_id, characteristic_name, value) VALUES (2, 'eyes', 'green');
INSERT INTO CHARACTERISTIC_MAPPING(person_id, characteristic_name, value) VALUES (2, 'hair', 'blond');

INSERT INTO VEHICLE (id, created_on, updated_on, person_id, vehicle_type, brand, model) VALUES (1, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', 1, 'CAR', 'Renault', 'Clio E-Tech');
INSERT INTO VEHICLE (id, created_on, updated_on, person_id, vehicle_type, brand, model) VALUES (2, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', 1, 'MOTORBIKE', 'Harley-Davidson', 'Livewire');
INSERT INTO VEHICLE (id, created_on, updated_on, person_id, vehicle_type, brand, model) VALUES (3, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', 2, 'CAR', 'Porsche', '911 Carrera S');

INSERT INTO FEATURE (id, created_on, updated_on, name, description) VALUES (1, '2020-03-12T10:36:00Z', '2020-03-12T10:36:00Z', 'gps', 'Connected navigation with cartography and connected navigation services (3 years): Google search for addresses and points of interest, real-time traffic information, fuel prices, warning zones.');
INSERT INTO FEATURE (id, created_on, updated_on, name, description) VALUES (2, '2020-03-12T10:36:00Z', '2020-03-12T10:36:00Z', 'airbag', 'Dual deployment driver front airbag and passenger front airbag. Front seat side airbags and front and rear side curtain airbags for optimum safety of all occupants.');
INSERT INTO FEATURE (id, created_on, updated_on, name, description) VALUES (3, '2020-03-12T10:36:00Z', '2020-03-12T10:36:00Z', 'overspeed', 'Your vehicle is on watch at all times: its camera reads traffic signs and informs you of the latest speed limits. In the event of overspeeding, your dashboard and navigation system will give you a visual warning when you exceed the speed limit.');
INSERT INTO FEATURE (id, created_on, updated_on, name, description) VALUES (4, '2020-03-12T10:36:00Z', '2020-03-12T10:36:00Z', 'eba', 'Emergency Brake Assist (EBA) applies maximum braking power when it detects a sudden braking maneuver by the driver. This helps reduce braking distances.');
INSERT INTO FEATURE (id, created_on, updated_on, name, description) VALUES (5, '2020-03-12T10:36:00Z', '2020-03-12T10:36:00Z', 'airConditioning', 'Automatic air conditioning: select your temperature and the system will then manage the other parameters for your comfort.');
INSERT INTO FEATURE (id, created_on, updated_on, name, description) VALUES (6, '2020-03-12T10:36:00Z', '2020-03-12T10:36:00Z', 'backCamera', '');

INSERT INTO FEATURE_MAPPING (vehicle_id, feature_id) VALUES (1, 1);
INSERT INTO FEATURE_MAPPING (vehicle_id, feature_id) VALUES (1, 2);
INSERT INTO FEATURE_MAPPING (vehicle_id, feature_id) VALUES (1, 3);
INSERT INTO FEATURE_MAPPING (vehicle_id, feature_id) VALUES (3, 4);
INSERT INTO FEATURE_MAPPING (vehicle_id, feature_id) VALUES (3, 5);
INSERT INTO FEATURE_MAPPING (vehicle_id, feature_id) VALUES (3, 6);

INSERT INTO ADDRESS (id, created_on, updated_on, city, country, street, zip_code) VALUES (1, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', 'Plaisir', 'FR', 'Rue des Peupliers', '78370');
INSERT INTO ADDRESS (id, created_on, updated_on, city, country, street, zip_code) VALUES (2, '2020-03-12T09:36:00Z', '2020-04-17T11:00:00Z', 'Porticcio', 'FR', 'Allée du sable fin', '20166');
INSERT INTO ADDRESS (id, created_on, updated_on, city, country, street, zip_code) VALUES (3, '2020-03-12T09:36:00Z', '2020-04-17T11:00:00Z', 'Le-Bois-Plage-En-Ré', 'FR', 'Rue des Petits Pois', '17051');

INSERT INTO PERSON_ADDRESS(person_id, address_id) VALUES (1, 1);
INSERT INTO PERSON_ADDRESS(person_id, address_id) VALUES (1, 2);
INSERT INTO PERSON_ADDRESS(person_id, address_id) VALUES (2, 1);
INSERT INTO PERSON_ADDRESS(person_id, address_id) VALUES (2, 3);

INSERT INTO JOB(id, person_id, created_on, updated_on, active, title, company, salary, hire_date) VALUES (1, 1, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', true, 'Lab Technician', 'Acme', '50000', '2019-09-01T09:00:00Z');
INSERT INTO JOB(id, person_id, created_on, updated_on, active, title, company, salary, hire_date) VALUES (2, 2, '2020-03-12T10:36:00Z', '2020-04-17T12:00:00Z', true, 'Commercial Fisherman', 'Fishing & Co', '60000', '2019-09-01T09:00:00Z');

INSERT INTO TASK(id, created_on, updated_on, name, description) VALUES (1, '2020-12-06T12:00:00Z','2020-12-06T12:00:00Z','Go shopping', 'Go shopping with the family');
INSERT INTO TASK(id, created_on, updated_on, name, description) VALUES (2, '2020-12-06T12:00:00Z','2020-12-06T12:00:00Z','Go to the doctor''s appointment', 'Go to the doctor''s appointment next Monday');
INSERT INTO TASK(id, created_on, updated_on, name, description) VALUES (3, '2020-12-06T12:00:00Z','2020-12-06T12:00:00Z','Clean up the garage', null);

INSERT INTO PERSON_TASKS(person_id, task_id, task_date) VALUES (1, 1, '2020-12-24T12:00:00Z');
INSERT INTO PERSON_TASKS(person_id, task_id, task_date) VALUES (2, 1, '2020-12-24T12:00:00Z');
INSERT INTO PERSON_TASKS(person_id, task_id, task_date) VALUES (1, 2, '2020-12-28T12:00:00Z');
INSERT INTO PERSON_TASKS(person_id, task_id, task_date) VALUES (1, 3, '2020-12-30T12:00:00Z');