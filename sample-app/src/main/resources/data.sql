INSERT INTO PERSON (id, first_name, last_name, email) VALUES (1, 'John', 'Doe', 'john.doe@acme.com');

INSERT INTO PERSON_PHONE_NUMBERS (person_id, phone_number) VALUES (1, '+33612345678');

INSERT INTO PERSON_NICK_NAMES (person_id, nick_names) VALUES (1, 'Johnny');

INSERT INTO VEHICLE (id, person_id, vehicle_type, brand, model) VALUES (1, 1, 'CAR', 'RENAULT', 'CLIO');

INSERT INTO ADDRESS (id, city, country, street, zip_code) VALUES (1, 'Plaisir', 'FR', 'Rue des Peupliers', '78370');

INSERT INTO PERSON_ADDRESS(person_id, address_id) VALUES (1, 1);