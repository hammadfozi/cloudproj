/* POPULATE IMPORTANT DATABASE */

/* Populate USER_PROFILE Table */
INSERT INTO USER_PROFILE (type)
VALUES ('USER');

INSERT INTO USER_PROFILE (type)
VALUES ('ADMIN');



/* Populate one Admin User which will further create other users for the application using GUI */
INSERT INTO APP_USER (password, first_name, last_name, email)
VALUES
  ('$2a$10$4eqIF5s/ewJwHK1p8lqlFOEm2QIA0S8g6./Lok.pQxqcxaBZYChRm', 'Test', 'Admin', 'admin@hyatt.com');
INSERT INTO APP_USER (password, first_name, last_name, email)
VALUES
  ('$2a$10$4eqIF5s/ewJwHK1p8lqlFOEm2QIA0S8g6./Lok.pQxqcxaBZYChRm', 'Test', 'Customer', 'customer@test.com');


/* Populate JOIN Table */
INSERT INTO APP_USER_USER_PROFILE (user_id, user_profile_id)
  SELECT
    usr.id,
    profile.id
  FROM APP_USER AS usr, USER_PROFILE AS profile
  where usr.email = 'admin@hyatt.com' and profile.type = 'ADMIN';

INSERT INTO APP_USER_USER_PROFILE (user_id, user_profile_id)
  SELECT
    usr.id,
    profile.id
  FROM APP_USER AS usr, USER_PROFILE AS profile
  where usr.email = 'customer@test.com' and profile.type = 'USER';