package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long id = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Добавили фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("ERROR: Не существует фильма с таким id {} ", film.getId());
            throw new NotFoundException("Отсутствует фильм c id " + film.getId());
        }
        films.put(film.getId(), film);
        log.info("Обновили фильм: {}", film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Получили список фильмов");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(long id) {
        if (!films.containsKey(id)) {
            log.error("ERROR: Не существует фильма с таким id {} ", id);
            throw new NotFoundException("Отсутствует фильма c id " + id);
        }
        return films.get(id);
    }
}
