package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final int LENGTH_DESCRIPTION = 200;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        validateFilms(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilms(film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public Film addLike(long filmId, long userId) {
        userStorage.getUserById(userId);
        getFilmById(filmId).getLikes().add(userId);
        return getFilmById(filmId);
    }

    public Film removeLike(long filmId, long userId) {
        userStorage.getUserById(userId);
        getFilmById(filmId).getLikes().remove(userId);
        return getFilmById(filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getAllFilms()
                .stream()
                .sorted((f0, f1) -> Integer.compare(f1.getLikes().size(), f0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public void validateFilms(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("ERROR: поле Name не может быть пустым");
            throw new ValidateException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > LENGTH_DESCRIPTION) {
            log.error("ERROR: описание Description не может быть длиннее 200 символов");
            throw new ValidateException("Максимальная длина описания — " + LENGTH_DESCRIPTION);
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE) || film.getReleaseDate() == null) {
            log.error("ERROR: дата релиза у фильма не может быть раньше 28 декабря 1895 года");
            throw new ValidateException("Дата релиза фильма — не раньше " + MIN_RELEASE_DATE);
        }
        if (film.getDuration() <= 0) {
            log.error("ERROR: продолжительность фильма должна быть положительной");
            throw new ValidateException("Продолжительность фильма должна быть положительной");
        }
    }
}