INSERT INTO
APP_USER(user_id, email, name, password, username)
VALUES
    (3, 'jane_doe@gmail.com', 'Jane Doe', 'hashedpassword', 'jane_girl18'),
    (5, 'joao_silva12@gmail.com', 'Joao Silva', 'hashedpassword2', 'joaozinn'),
    (6, 'thirdwheel@gmail.com', 'John Doe', 'hashedpassword3', 'third_wheel'),
    (7, 'fourthwheel@gmail.com', 'Fortis', 'hashedpassword4', 'fourth_wheel'),
    (8, 'fifthwheel@gmail.com', 'Fivus', 'hashedpassword5', 'fifth_wheel'),
    (9, 'fulano@hotmail.com', 'Fulano Fulanaldo', 'snjadhkajakjha', 'fulaninn'),
    (10, 'hated@mail.com', 'Hated Guy', 'hatedpsdas', 'hated13'),
    (11, 'emailToEdit@mail.com', 'Edited Guy', 'pasdsjudhas', 'edi_guy'),
    (12, 'andanotheremail@mail.com', 'Real Guy', '{bcrypt}$2a$10$1CEwa1T.h9g5q35pllz/2ODIP5WmtMqVzDwpxXkP6b0RxP46RZTqu',
     'real_guy');

INSERT INTO
APP_USER_FRIENDS(app_user_user_id, friends_user_id)
VALUES
    (6, 7),
    (7, 6),
    (3, 5),
    (5, 3),
    (10, 3),
    (3, 10);

INSERT INTO
EVENT(id, title, all_day, start, ending, owner_user_id)
VALUES
    (15, 'Third Event', false, '2023-02-03T12:00:00', '2023-02-04T23:00:00', 6),
    (3, 'Second Event', false, '2023-02-03T12:00:00', '2023-02-04T23:00:00', 6),
    (4, 'First Event', false, '2023-02-03T12:00:00', '2023-02-04T23:00:00', 6),
    (5, 'Zero Event', false, '2023-02-03T12:00:00', '2023-02-04T23:00:00', 6),
    (2, 'Fifth Event', true, '2025-02-03T12:00:00', '2025-02-04T00:00:00', 8);

INSERT INTO
INVITATION(id, is_confirmed, event_id, invited_user_id)
VALUES
    (1, false, 2, 6),
    (4, false, 3, 8);

INSERT INTO
SHIFT(shift_id, boarding_date, leaving_date, unavailability_end_date, unavailability_start_date, owner_user_id)
VALUES
    (11, '2025-04-15', '2025-05-17', '2025-05-18', '2025-04-13', 3),
    (12, '2027-04-15', '2027-04-18', '2027-04-19', '2027-04-14', 5);