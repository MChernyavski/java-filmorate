package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(long id);

    List<Long> addLike(long filmId, long userId);

    Film removeLike(long filmId, long userId);

    List<Film> getMostPopularFilms(int count);
}