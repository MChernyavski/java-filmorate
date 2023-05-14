package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.dao.LikesDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikesDbStorage likesDbStorage;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final int LENGTH_DESCRIPTION = 200;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage,
                           UserStorage userStorage,
                           FilmGenreStorage filmGenreStorage, MpaStorage mpaStorage, GenreStorage genreStorage,
                           LikesDbStorage likesDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.likesDbStorage = likesDbStorage;
    }

    @Override
    public Film addFilm(Film film) {
        validateFilms(film);
        filmStorage.addFilm(film);
        film.getGenres().forEach(genre -> filmGenreStorage.addGenreToFilm(film.getId(), genre.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilms(film);
        filmStorage.updateFilm(film);
        filmGenreStorage.removeGenreFromFilm(film.getId());
        film.getGenres().forEach(genre -> filmGenreStorage.addGenreToFilm(film.getId(), genre.getId()));
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms()
                .stream()
                .peek(film -> genreStorage.getGenreByFilm(film.getId())
                        .forEach(film::addGenre))
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(long id) {
        Film film = filmStorage.getFilmById(id);
        genreStorage.getGenreByFilm(film.getId()).forEach(film::addGenre);
        return film;
    }

    @Override
    public List<Long> addLike(long filmId, long userId) {
        getFilmById(filmId);
        userStorage.getUserById(userId);
        likesDbStorage.addLike(filmId, userId);
        return likesDbStorage.getLikesByFilm(filmId);
    }

    @Override
    public Film removeLike(long filmId, long userId) {
        userStorage.getUserById(userId);
        likesDbStorage.deleteLike(filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getMostPopularFilms(count)
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
