INSERT INTO USERS (email, login, name, birthday)
VALUES ('mariach@gmail.ru', 'login1', 'maria', '1991-10-20'),
       ('miki@ya.ru', 'login2', 'miki', '1989-11-29');

INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES ('Очень хороший фильм', 'Описание этого фильма', '2000-10-10', 90, 1 ),
       ('Очень плохой фильм', 'Описание плохого фильма', '1990-10-10', 120, 2 ),
       ('Средний фильм', 'Описание среднего фильма', '1995-10-10', 100, 4);

SELECT FILMS.*, MR.NAME FROM FILMS JOIN MPA_RATING MR on MR.MPA_ID = FILMS.MPA_ID;