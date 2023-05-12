package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface FilmGenreStorage {

    boolean addGenreToFilm(long filmId, long genreId) ;
    boolean removeGenreFromFilm(long filmId, int genreId);
    List<FilmGenre> getGenreByFilm(long id);
}
