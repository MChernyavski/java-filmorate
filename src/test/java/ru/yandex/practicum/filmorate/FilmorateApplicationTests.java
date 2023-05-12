package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
    class FilmorateApplicationTests {
        private final UserDbStorage userStorage;
        private final FilmDbStorage filmStorage;
        private final FriendshipDbStorage friendshipDbStorage;
        private final GenreDbStorage genreDbStorage;
        private final MpaDbStorage mpaDbStorage;
        private final LikesDbStorage likesDbStorage;
        private final FilmGenreDbStorage filmGenreDbStorage;

        User.UserBuilder userBuilder;
        Film.FilmBuilder filmBuilder;
        Genre.GenreBuilder genreBuilder;
        MpaRating.MpaRatingBuilder mpaBuilder;

        private final LocalDate testReleaseDate = LocalDate.of(2000, 1, 1);

        private final JdbcTemplate jdbcTemplate;

        @BeforeEach
        public void setup() {
            userBuilder = User.builder()
                    .email("e@mail.ru")
                    .login("Login")
                    .name("Name")
                    .birthday(LocalDate.of(1985, 9, 7));

            mpaBuilder = MpaRating.builder()
                    .id(1);

            genreBuilder = Genre.builder()
                    .id(1);

            filmBuilder = Film.builder()
                    .name("Film name")
                    .description("Film description")
                    .releaseDate(testReleaseDate)
                    .duration(90)
                    .mpaRating(mpaBuilder.build());
        }

        @AfterEach
        public void cleanDb() {
            JdbcTestUtils.deleteFromTables(jdbcTemplate,
                    "users", "films", "friendship", "film_genre", "likes");
            jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN user_id RESTART WITH 1");
            jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN film_id RESTART WITH 1");
        }

        @Test
        public void testAddUser() {
            User user = userBuilder.build();
            User userAdded = userStorage.addUser(user);
            assertThat(userAdded)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("id", 1L);
        }

        @Test
        public void testFindUserById() {
            User user = userBuilder.build();
            User userAdded = userStorage.addUser(user);
            User userFound = userStorage.getUserById(userAdded.getId());
            assertThat(userFound)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("id", 1L)
                    .isEqualTo(userAdded);

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> userStorage.getUserById(-1L)
            );
            assertEquals("Пользователь с id -1 не найден", ex.getMessage());

            ex = assertThrows(
                    NotFoundException.class,
                    () -> userStorage.getUserById(999L)
            );
            assertEquals("Пользователь с id 999 не найден", ex.getMessage());
        }

        @Test
        public void testListUsers() {
            List<User> users = userStorage.getAllUsers();
            assertThat(users)
                    .isNotNull()
                    .isEqualTo(Collections.EMPTY_LIST);

            User user = userBuilder.build();
            userStorage.addUser(user);
            users = userStorage.getAllUsers();
            assertNotNull(users);
            assertEquals(users.size(), 1);
            assertEquals(users.get(0).getId(), 1);
        }

        @Test
        public void testUpdateUser() {
            User user = userBuilder.build();
            userStorage.addUser(user);
            User userToUpdate = userBuilder.id(1L).name("Name Updated").build();
            User userUpdated = userStorage.updateUser(userToUpdate);
            assertThat(userUpdated)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("id", 1L)
                    .hasFieldOrPropertyWithValue("name", "Name Updated");

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> userStorage.updateUser(userBuilder.id(-1L).build())
            );
            assertEquals("Пользователь с id -1 не найден", ex.getMessage());

            ex = assertThrows(
                    NotFoundException.class,
                    () -> userStorage.updateUser(userBuilder.id(999L).build())
            );
            assertEquals("Пользователь с id 999 не найден", ex.getMessage());
        }

        @Test
        public void testAddFilm() {
            Film film = filmBuilder.build();
            Film filmAdded = filmStorage.addFilm(film);
            assertThat(filmAdded)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("id", 1L);
        }

        @Test
        public void testFindFilmById() {
            Film film = filmBuilder.build();
            Film filmAdded = filmStorage.addFilm(film);
            Film filmFound = filmStorage.getFilmById(filmAdded.getId());
            assertThat(filmFound)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("id", 1L)
                    .hasFieldOrPropertyWithValue("mpa", mpaBuilder.name("G").build());
            //.isEqualTo(filmAdded);

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> filmStorage.getFilmById(-1L)
            );
            assertEquals("Фильм с id -1 не найден", ex.getMessage());

            ex = assertThrows(
                    NotFoundException.class,
                    () -> filmStorage.getFilmById(999L)
            );
            assertEquals("Фильм с id 999 не найден", ex.getMessage());
        }

        @Test
        public void testListFilms() {
            List<Film> films = filmStorage.getAllFilms();
            assertThat(films)
                    .isNotNull()
                    .isEqualTo(Collections.EMPTY_LIST);

            Film film = filmBuilder.build();
            filmStorage.addFilm(film);
            films = filmStorage.getAllFilms();
            assertNotNull(films);
            assertEquals(films.size(), 1);
            assertEquals(films.get(0).getId(), 1);
        }

        @Test
        public void testUpdateFilm() {
            Film film = filmBuilder.build();
            filmStorage.addFilm(film);
            Film filmToUpdate = filmBuilder.id(1L).name("Film name Updated").build();
            Film filmUpdated = filmStorage.updateFilm(filmToUpdate);
            assertThat(filmUpdated)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("id", 1L)
                    .hasFieldOrPropertyWithValue("name", "Film name Updated");

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> filmStorage.updateFilm(filmBuilder.id(-1L).build())
            );
            assertEquals("Фильм с id -1 не найден", ex.getMessage());

            ex = assertThrows(
                    NotFoundException.class,
                    () -> filmStorage.updateFilm(filmBuilder.id(999L).build())
            );
            assertEquals("Фильм с id 999 не найден", ex.getMessage());
        }

        @Test
        public void testListTopFilms() {
            List<Film> topFilms = filmStorage.getMostPopularFilms(10);
            assertThat(topFilms)
                    .isNotNull()
                    .isEqualTo(Collections.EMPTY_LIST);

            filmStorage.addFilm(filmBuilder.build());
            filmStorage.addFilm(filmBuilder.build());
            userStorage.addUser(userBuilder.build());

            topFilms = filmStorage.getMostPopularFilms(1);
            assertNotNull(topFilms);
            assertEquals(topFilms.size(), 1);
            assertEquals(topFilms.get(0).getId(), 1);

            likesDbStorage.addLike(2, 1);
            topFilms = filmStorage.getMostPopularFilms(2);
            assertNotNull(topFilms);
            assertEquals(topFilms.size(), 2);
            assertEquals(topFilms.get(0).getId(), 2);
        }


        @Test
        public void testGetFriendsByUser() {
            User user = userBuilder.build();
            userStorage.addUser(user);
            User friend = userBuilder.name("friend").build();
            userStorage.addUser(friend);

            List<Long> friends = friendshipDbStorage.getAllFriends(1);
            assertThat(friends)
                    .isNotNull()
                    .isEqualTo(Collections.EMPTY_LIST);

            friendshipDbStorage.addToFriend(1, 2);
            friends = friendshipDbStorage.getAllFriends(1);
            assertNotNull(friends);
            assertEquals(friends.size(), 1);
            assertEquals(friends.get(0), 2);
        }


    }
