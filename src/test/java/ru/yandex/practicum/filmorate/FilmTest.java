package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.InMemoryFilmService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.service.InMemoryFilmService.LENGTH_DESCRIPTION;
import static ru.yandex.practicum.filmorate.service.InMemoryFilmService.MIN_RELEASE_DATE;

public class FilmTest {
    FilmController filmController;
    Film film1;
    Film film2;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController(new InMemoryFilmService());
        film1 = Film.builder()
                .id(1)
                .name("Человек-паук")
                .description("Как Питер становится супер-героем")
                .releaseDate(LocalDate.of(2002, 04, 30))
                .duration(120)
                .build();

        film2 = Film.builder()
                .id(2)
                .name("Форрест Гамп")
                .description("История жизни Форреста Гампа")
                .releaseDate(LocalDate.of(1994, 06, 24))
                .duration(142)
                .build();
    }

    @Test
    public void addFilmTest() {
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        List<Film> getFilms = filmController.getAllFilms();
        assertEquals(2, getFilms.size());
        assertNotNull(getFilms);
    }

    @Test
    public void updateFilmTest() {
        Film actualFilm = filmController.addFilm(film1);
        actualFilm.setId(film1.getId());
        actualFilm.setName("Человек-шмель");
        actualFilm.setDuration(240);
        Film newFilm = filmController.updateFilm(actualFilm);
        assertEquals(actualFilm, newFilm);
        assertNotNull(newFilm);
    }

    @Test
    public void addFilmWithEmptyNameTest() {
        film1.setName("");
        ValidateException exception = assertThrows(ValidateException.class, () -> filmController.addFilm(film1));
        String newErrorMessage = "Название фильма не может быть пустым";
        assertEquals(newErrorMessage, exception.getMessage());
    }

    @Test
    public void addFilmWithTooLongDescriptionTest() {
        film1.setDescription("Школьника кусает паук, он становится пауком и начинает пользовать по стенам." +
                "Помогает людям бороться со злом блаблаблаблаблаблаблаблаблаблаблаблаблаблабла" +
                "блаблаблаблаблаблаблаблаблаблаблаблаблаблаблаблаблаблаблаблаблаблаблаблаблаблабла" +
                "блаблаблаблаблабла\n");
        ValidateException exception = assertThrows(ValidateException.class, () -> filmController.addFilm(film1));
        String newErrorMessage = "Максимальная длина описания — " + LENGTH_DESCRIPTION;
        assertEquals(newErrorMessage, exception.getMessage());
    }

    @Test
    public void addFilmWithWrongReleaseDateTest() {
        film1.setReleaseDate(LocalDate.of(1890, 12, 11));
        ValidateException exception = assertThrows(ValidateException.class, () -> filmController.addFilm(film1));
        String newErrorMessage = "Дата релиза фильма — не раньше " + MIN_RELEASE_DATE;
        assertEquals(newErrorMessage, exception.getMessage());
    }

    @Test
    public void addFilmWithNegativeDurationTest() {
        film1.setDuration(-240);
        ValidateException exception = assertThrows(ValidateException.class, () -> filmController.addFilm(film1));
        String newErrorMessage = "Продолжительность фильма должна быть положительной";
        assertEquals(newErrorMessage, exception.getMessage());
    }
}
