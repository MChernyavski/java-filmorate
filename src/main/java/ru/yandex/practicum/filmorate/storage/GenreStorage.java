package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public interface GenreStorage {

    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    List<Genre> getGenreByFilm(long id);
}
