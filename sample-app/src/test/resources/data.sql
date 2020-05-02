INSERT INTO PERSON (id, first_name, last_name, email) VALUES (1, 'John', 'Doe', 'john.doe@acme.com');

INSERT INTO PERSON_PHONE_NUMBERS (person_id, phone_number) VALUES (1, '+33612345678');

INSERT INTO PERSON_NICK_NAMES (person_id, nick_names) VALUES (1, 'Johnny');

INSERT INTO ADDRESS (id, person_id, city, country, street, zip_code) VALUES (1, 1, 'Plaisir', 'FR', 'Rue des Peupliers', '78370');
