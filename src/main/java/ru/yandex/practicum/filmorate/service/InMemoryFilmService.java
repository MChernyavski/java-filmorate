package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InMemoryFilmService implements FilmService {

    private int id = 1;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final int LENGTH_DESCRIPTION = 200;
    private final Map<Integer,Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        validateFilms(film);
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Добавили фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("ERROR: Не существует фильма с таким id {} ", film.getId());
            throw new ValidateException("Отсутствует фильм c id " + film.getId());
        }
        validateFilms(film);
        films.put(film.getId(), film);
        log.info("Обновили фильм: {}", film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Получили список фильмов");
        return new ArrayList<>(films.values());
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
